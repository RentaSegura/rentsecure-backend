package app.rentsecure.it;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import com.icegreen.greenmail.util.GreenMail;

import app.rentsecure.auth.dto.JwtResponse;
import app.rentsecure.auth.dto.LoginRequest;
import app.rentsecure.auth.dto.RegisterRequest;
import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.assertj.core.api.Assertions.assertThat; // Usando AssertJ para aserciones más legibles

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
class AuthFlowIT {

    @Autowired
    TestRestTemplate rest;
    @Autowired
    GreenMail greenMail;

    @Test
    void register_verify_login_flow() throws MessagingException, IOException {
        // 1. Register
        var reg = new RegisterRequest();
        reg.setEmail("mila@test.com");
        reg.setPassword("P@ss12345");
        reg.setFullName("Mila");

        ResponseEntity<String> regResponse = rest.postForEntity("/api/auth/register", reg, String.class);
        assertThat(regResponse.getStatusCode().is2xxSuccessful()).isTrue();

        // 2. Capturar email (de forma segura y robusta)
        // Espera un máximo de 5 segundos a que llegue 1 email
        greenMail.waitForIncomingEmail(5000, 1);

        MimeMessage msg = greenMail.getReceivedMessages()[0];
        String emailContent = getHtmlContent(msg);

        // Extraer el link con una expresión regular
        String link = extractVerificationLink(emailContent);
        assertThat(link).isNotNull(); // Asegurarnos de que encontramos el link

        // 3. Verify
        ResponseEntity<String> verifyResponse = rest.getForEntity(link, String.class);
        assertThat(verifyResponse.getStatusCode().is2xxSuccessful()).isTrue();

        // 4. Login (de forma segura)
        var signIn = new LoginRequest();
        signIn.setEmail("mila@test.com");
        signIn.setPassword("P@ss12345");

        ResponseEntity<JwtResponse> loginResponse = rest.postForEntity("/api/auth/login", signIn, JwtResponse.class);

        // Aserciones robustas sobre la respuesta del login
        assertThat(loginResponse.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(loginResponse.getBody()).isNotNull();

        String jwt = loginResponse.getBody().getToken();
        System.out.println("JWT Token: " + jwt);

        // Aserción final: el token JWT no debe ser nulo ni estar vacío
        assertThat(jwt).isNotNull().isNotBlank();
    }

    /**
     * Extrae el link de verificación del contenido del email usando una expresión regular.
     */
    private String extractVerificationLink(String text) {
        // Regex para encontrar una URL que empiece con http://localhost y contenga "?token="
        Pattern pattern = Pattern.compile("(http://localhost:[0-9]+/api/auth/verify\\?token=[a-zA-Z0-9\\-]+)");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            // El grupo 0 o 1 contendrá la URL completa encontrada
            return matcher.group(1);
        }
        return null; // O lanzar una excepción si el link no se encuentra
    }
    
    /**
     * Método de ayuda para obtener el contenido HTML de un email, incluso si es multipart.
     * Esto hace el test más robusto por si en el futuro el formato del email cambia.
     */
    private String getHtmlContent(MimeMessage msg) throws IOException, MessagingException {
        if (msg.isMimeType("text/html")) {
            // JavaMail decodifica automáticamente "quoted-printable"
            return (String) msg.getContent();
        }

        if (msg.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) msg.getContent();
            for (int i = 0; i < mimeMultipart.getCount(); i++) {
                BodyPart bodyPart = mimeMultipart.getBodyPart(i);
                if (bodyPart.isMimeType("text/html")) {
                    return (String) bodyPart.getContent();
                }
            }
        }
        
        // Como fallback, devuelve el contenido principal si no es multipart ni html plano
        return (String) msg.getContent();
    }
}