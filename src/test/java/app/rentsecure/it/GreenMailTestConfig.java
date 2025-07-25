package app.rentsecure.it;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@TestConfiguration
public class GreenMailTestConfig {

    /** Inicia un SMTP embebido en el puerto 3025 */
    @Bean(initMethod = "start", destroyMethod = "stop")
    public GreenMail greenMail() {
        var mail = new GreenMail(ServerSetupTest.SMTP);   // SMTP --> puerto 3025
        mail.setUser("test", "pwd");
        return mail;
    }

    /** Hace que Spring envíe correos al servidor embebido */
    @Bean
    public JavaMailSender mailSender(GreenMail greenMail) {
        var sender = new JavaMailSenderImpl();
        sender.setHost("localhost");
        sender.setPort(greenMail.getSmtp().getPort());    // 3025
        return sender;
    }
}
