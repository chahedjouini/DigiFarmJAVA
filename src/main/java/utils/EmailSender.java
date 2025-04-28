package utils;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailSender {

    private static final String FROM_EMAIL = "ayari.ahmed.0291@gmail.com"; // Votre adresse email
    private static final String PASSWORD = "akkf pbaq iqju hmib"; // Mot de passe d'application Google

    public static boolean sendPasswordResetEmail(String toEmail, String resetCode) {
        // Configuration des propriétés pour Gmail
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // Créer une session avec authentification
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, PASSWORD);
            }
        });

        try {
            // Créer le message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Réinitialisation de votre mot de passe");

            // Corps du message
            String htmlContent = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>"
                    + "<h2 style='color: #2E7D32;'>Réinitialisation de votre mot de passe</h2>"
                    + "<p>Vous avez demandé la réinitialisation de votre mot de passe.</p>"
                    + "<p>Voici votre code de réinitialisation :</p>"
                    + "<p><strong>" + resetCode + "</strong></p>"
                    + "<p>Ce code est valable pendant 10 minutes.</p>"
                    + "<p>Si vous n'avez pas demandé cette réinitialisation, vous pouvez ignorer cet email.</p>"
                    + "<p>L'équipe DigiFarm</p>"
                    + "</div>";

            message.setContent(htmlContent, "text/html; charset=utf-8");

            // Envoyer le message
            Transport.send(message);
            System.out.println("Email envoyé avec succès à " + toEmail);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }
}