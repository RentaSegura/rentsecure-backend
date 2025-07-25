package app.rentsecure.auth.service.impl;

import app.rentsecure.auth.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendVerificationEmail(String to, String link) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom("kg.vicente.23@gmail.com");
        msg.setTo(to);
        msg.setSubject("Verifica tu cuenta - RentSecure");
        msg.setText("¡Bienvenido!\n\nHaz clic para verificar tu correo:\n" + link);
        mailSender.send(msg);
    }
}