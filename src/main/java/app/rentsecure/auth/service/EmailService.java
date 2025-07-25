package app.rentsecure.auth.service;

public interface EmailService {
    void sendVerificationEmail(String to, String verificationLink);
}
