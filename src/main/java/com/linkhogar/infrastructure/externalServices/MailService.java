package com.linkhogar.infrastructure.externalServices;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MailService {

    @Value("${BREVO_API_KEY}") // Lee la clave de Railway
    private String brevoApiKey;

    private final String fromEmail = "info@linkhogar.com";

    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;

    @Async
    public void sendPasswordResetEmail(String toEmail, String code) {
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

        sendEmailViaBrevoAPI(toEmail, "Código de restablecimiento de contraseña - LinkHogar", htmlMsg);
    }

    @Async
    public void sendVerificationEmail(String toEmail, String token) {
        System.out.println("Intentando enviar correo de verificación a: " + toEmail);
        String verificationLink = frontendUrl + "/verify?token=" + token;

        String htmlMsg = "<div style='font-family: Arial, sans-serif; padding: 20px;'>"
                + "<h2>¡Bienvenido a LinkHogar!</h2>"
                + "<p>Gracias por registrarte. Para activar tu cuenta y empezar a buscar tu próximo hogar, haz clic en el siguiente enlace:</p>"
                + "<br>"
                + "<a href='" + verificationLink + "' style='background-color: #0d6efd; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>Verificar mi cuenta</a>"
                + "<br><br>"
                + "<p style='color: #6c757d; font-size: 0.9em;'>Este enlace expirará en 24 horas. Si no has creado una cuenta en LinkHogar, puedes ignorar este correo.</p>"
                + "</div>";

        sendEmailViaBrevoAPI(toEmail, "Verifica tu cuenta en LinkHogar", htmlMsg);
    }

    // --- EL MOTOR DE ENVÍO POR API (Sustituye al antiguo JavaMailSender) ---
    private void sendEmailViaBrevoAPI(String toEmail, String subject, String htmlContent) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.brevo.com/v3/smtp/email";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", brevoApiKey);

        // ✅ ObjectMapper escapa correctamente todos los caracteres especiales
        ObjectMapper mapper = new ObjectMapper();

        try {
            Map<String, Object> body = new HashMap<>();
            body.put("sender", Map.of("name", "LinkHogar", "email", fromEmail));
            body.put("to", List.of(Map.of("email", toEmail)));
            body.put("subject", subject);
            body.put("htmlContent", htmlContent); // Sin escapado manual

            String requestJson = mapper.writeValueAsString(body);

            HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            System.out.println("Correo enviado a: " + toEmail + " | Respuesta: " + response.getBody());

        } catch (HttpClientErrorException e) {
            System.err.println("Error HTTP: " + e.getStatusCode());
            System.err.println("Body de Brevo: " + e.getResponseBodyAsString()); // ← aquí está el motivo real
        } catch (Exception e) {
            System.err.println("Error general: " + e.getMessage());
        }
    }
}
