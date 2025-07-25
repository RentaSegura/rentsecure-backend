package app.rentsecure.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class MailTestRunner implements CommandLineRunner {
    private final JavaMailSender mailSender;

    public MailTestRunner(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void run(String... args) {
        // SimpleMailMessage msg = new SimpleMailMessage();
        // msg.setFrom("kg.vicente.23@gmail.com");
        // msg.setTo("kg.vicente.23@gmail.com");
        // msg.setSubject("Test SMTP RentSecure");
        // msg.setText("¡Hola desde Spring Boot!");
        // mailSender.send(msg);
        // System.out.println("Email enviado!");
    }
}