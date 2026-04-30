package com.linkhogar.infrastructure.externalServices;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${BREVO_API_KEY}") // Lee la clave de Railway
    private String brevoApiKey;

    private final String fromEmail = "info@linkhogar.com";

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

    @Async
    public void sendPasswordResetEmail(String toEmail, String code) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.brevo.com/v3/smtp/email";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", brevoApiKey);

        // Tu diseño HTML exacto
        String htmlMsg = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px;'>"
                + "<h2 style='color: #333; text-align: center;'>Cambio de contraseña</h2>"
                + "<p style='color: #555; font-size: 16px;'>Hola,</p>"
                + "<p style='color: #555; font-size: 16px;'>Hemos recibido una solicitud para cambiar la contraseña de tu cuenta en LinkHogar. Tu código de seguridad es:</p>"
                + "<div style='text-align: center; margin: 30px 0;'>"
                + "<span style='font-size: 24px; font-weight: bold; background-color: #f4f4f4; padding: 10px 20px; border-radius: 5px; letter-spacing: 5px; color: #333; border: 1px dashed #ccc;'>" + code + "</span>"
                + "</div>"
                + "<p style='color: #555; font-size: 16px;'>Introdúcelo en la aplicación para establecer tu nueva contraseña.</p>"
                + "<hr style='border: 0; border-top: 1px solid #eee; margin: 30px 0;' />"
                + "<p style='color: #999; font-size: 12px; text-align: center;'>Si no has solicitado este cambio, por favor ignora este correo.</p>"
                + "</div>";

        // Por seguridad, escapamos las comillas dobles si las hubiera en el HTML para no romper el JSON
        String safeHtml = htmlMsg.replace("\"", "\\\"");

        // Construimos el JSON para Brevo
        String requestJson = """
            {
               "sender": {"name": "LinkHogar", "email": "%s"},
               "to": [{"email": "%s"}],
               "subject": "Código de restablecimiento de contraseña - LinkHogar",
               "htmlContent": "%s"
            }
            """.formatted(fromEmail, toEmail, safeHtml);

        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

        try {
            // Hacemos la petición HTTP POST a Brevo
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            System.out.println("¡Correo enviado con estilo por API! Respuesta: " + response.getBody());
        } catch (Exception e) {
            System.out.println("Fallo al enviar correo HTTP: " + e.getMessage());
            throw new RuntimeException("Error al enviar el correo de recuperación", e);
        }
    }
}
