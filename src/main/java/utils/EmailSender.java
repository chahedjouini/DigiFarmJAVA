package utils;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
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

    public static boolean sendGreetingsEmail(String toEmail, String nom, String prenom) {
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
            // Créer le message multipart pour inclure l'image
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("عيد الأضحى مبارك - DigiFarm");

            // Créer la partie multipart
            Multipart multipart = new MimeMultipart("related");

            // Première partie - html
            MimeBodyPart messageBodyPart = new MimeBodyPart();

            // Corps du message avec design attrayant et référence à l'image intégrée
            String htmlContent = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; background-color: #f9f9f9; border-radius: 10px;'>"
                    + "<div style='text-align: center; margin-bottom: 20px;'>"
                    + "<h1 style='color: #8E44AD; margin-bottom: 5px;'>عيد الأضحى مبارك!</h1>"
                    + "<div style='height: 4px; background: linear-gradient(to right, #8E44AD, #3498DB); margin: 10px auto; width: 100px;'></div>"
                    + "</div>"
                    // Ajouter l'image en haut du message
                    + "<div style='text-align: center; margin-bottom: 20px;'>"
                    + "<img src='cid:eid-image' style='max-width: 100%; border-radius: 8px;' alt='Aid Idhha Mubarak' />"
                    + "</div>"
                    + "<p style='font-size: 16px; color: #333; text-align: right;'>عزيزي/عزيزتي <strong>" + prenom + " " + nom + "</strong>،</p>"
                    + "<p style='font-size: 16px; color: #333; line-height: 1.5; text-align: right;'>في هذه المناسبة المباركة لعيد الأضحى، يقدم لكم فريق DigiFarm أطيب التمنيات.</p>"
                    + "<p style='font-size: 16px; color: #333; line-height: 1.5; text-align: right;'>نتمنى أن يجلب هذا العيد لكم ولعائلتكم:</p>"
                    + "<ul style='color: #555; line-height: 1.6; text-align: right; direction: rtl;'>"
                    + "<li>السعادة والفرح</li>"
                    + "<li>الصحة والازدهار</li>"
                    + "<li>السلام والبركات</li>"
                    + "</ul>"
                    + "<p style='font-size: 16px; color: #333; line-height: 1.5; text-align: right;'>نشكركم على ثقتكم وولائكم لـ DigiFarm.</p>"
                    + "<div style='text-align: center; margin-top: 30px;'>"
                    + "<p style='font-style: italic; color: #666;'>مع خالص التحيات،</p>"
                    + "<p style='font-weight: bold; color: #2E7D32;'>فريق DigiFarm</p>"
                    + "</div>"
                    + "</div>";

            messageBodyPart.setContent(htmlContent, "text/html; charset=utf-8");
            multipart.addBodyPart(messageBodyPart);

            // Deuxième partie - l'image
            messageBodyPart = new MimeBodyPart();
            // Chemin de l'image
            String imagePath = "src/main/resources/images/ImageMail.png";

            DataSource fds = new FileDataSource(imagePath);
            messageBodyPart.setDataHandler(new DataHandler(fds));
            messageBodyPart.setHeader("Content-ID", "<eid-image>");
            messageBodyPart.setDisposition(MimeBodyPart.INLINE);

            multipart.addBodyPart(messageBodyPart);

            // Ajouter les parties au message
            message.setContent(multipart);

            // Envoyer le message
            Transport.send(message);
            System.out.println("Email de vœux avec image envoyé avec succès à " + toEmail);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'envoi de l'email à " + toEmail + ": " + e.getMessage());
            return false;
        }
    }
}