package com.linkhogar.infrastructure.externalServices;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;

    @Async
    public void sendVerificationEmail(String toEmail, String token) {
        try {
            System.out.println("Intentando enviar correo de verificación a: " + toEmail);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Verifica tu cuenta en LinkHogar");

            String verificationLink = frontendUrl + "/verify?token=" + token;

            String htmlMsg = "<div style='font-family: Arial, sans-serif; padding: 20px;'>"
                    + "<h2>¡Bienvenido a LinkHogar!</h2>"
                    + "<p>Gracias por registrarte. Para activar tu cuenta y empezar a buscar tu próximo hogar, haz clic en el siguiente enlace:</p>"
                    + "<br>"
                    + "<a href='" + verificationLink + "' style='background-color: #0d6efd; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>Verificar mi cuenta</a>"
                    + "<br><br>"
                    + "<p style='color: #6c757d; font-size: 0.9em;'>Este enlace expirará en 24 horas. Si no has creado una cuenta en LinkHogar, puedes ignorar este correo.</p>"
                    + "</div>";

            helper.setText(htmlMsg, true);
            mailSender.send(message);
            System.out.println("¡Correo enviado con éxito a: " + toEmail + "!");
        } catch (MessagingException e) {
            System.err.println("EXPLOSIÓN AL ENVIAR CORREO a " + toEmail);
            System.err.println("Motivo exacto: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
