package com.stockSync.backend.auth.service; // PAQUETE CORREGIDO

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service("authEmailService")
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${application.mail.from-name}")
    private String fromName;

    @Value("${FRONTEND_URL:http://localhost:5173}")
    private String frontendUrl;

    public void sendPasswordResetEmail(String to, String token) {
        String resetLink = frontendUrl + "/#/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromName + " <" + fromEmail + ">");
        message.setTo(to);
        message.setSubject("Restablecer contraseña - StockSync");
        message.setText("Hola,\n\n"
                + "Has solicitado restablecer tu contraseña para tu cuenta en StockSync.\n\n"
                + "Para continuar, haz clic en el siguiente enlace:\n"
                + resetLink + "\n\n"
                + "Si no solicitaste esto, puedes ignorar este correo de forma segura.\n\n"
                + "Saludos,\n"
                + "El equipo de " + fromName);

        mailSender.send(message);
    }
}