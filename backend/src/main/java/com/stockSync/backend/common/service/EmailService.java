package com.stockSync.backend.common.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service("commonEmailService")
public class EmailService {

    private final JavaMailSender mailSender;
    private final String fromEmail;
    private final String fromName;

    public EmailService(
            JavaMailSender mailSender,
            @Value("${application.mail.from-email}") String fromEmail,
            @Value("${application.mail.from-name}") String fromName
    ) {
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
        this.fromName = fromName;
    }

    public void sendPasswordResetEmail(String to, String token) {
        String subject = "Restablecimiento de Contraseña - StockSync";
        // ¡CORRECCIÓN! Añadido el '#' para que funcione con createWebHashHistory
        String resetUrl = "http://localhost:5173/#/reset-password?token=" + token;
        String emailContent = "Hola,<br><br>"
                + "Has solicitado restablecer tu contraseña. Haz clic en el siguiente enlace para continuar:<br>"
                + "<a href=\"" + resetUrl + "\">Restablecer Contraseña</a><br><br>"
                + "Si no solicitaste esto, por favor ignora este correo.";

        sendEmail(to, subject, emailContent);
    }

    private void sendEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // El 'true' indica que el contenido es HTML

            mailSender.send(message);
            System.out.println("Email enviado exitosamente a: " + to);
            
        } catch (MessagingException | UnsupportedEncodingException e) {
            System.err.println("Error al enviar el correo: " + e.getMessage());
        }
    }
}
