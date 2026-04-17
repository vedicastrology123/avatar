package jyotish.main;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import jakarta.mail.util.ByteArrayDataSource; // Use this for Jakarta 10
import jakarta.activation.DataHandler;

// import jakarta.activation.DataSource;
import java.util.*;

public class SendUserEmail {

    private String loadTemplate(String fileName) {
        try (java.io.InputStream is = getClass().getClassLoader().getResourceAsStream("/templates/" + fileName)) {
            if (is == null) {
                throw new java.io.IOException("Template not found in classpath: templates/" + fileName);
            }
            return new String(is.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        } catch (java.io.IOException e) {
            System.err.println("Error reading template file: " + e.getMessage());
            return "<html><body>Hi ${userName}, your chart is attached.</body></html>";
        }
    }

    public void generateAndEmail(String userEmail, byte[] imageBytes, String firstName, String lastName, String date, String time, String city, String state, String country, String questions, String residency) {
        Random random = new Random();
        int rangedInt = random.nextInt(1000, 3000);
        String registration = "REG-" + String.valueOf(rangedInt);

        try {
            String javamailPswd = "gftr qnle lejk qtmo";

            Session session = Session.getInstance(getMailProperties(), new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    // YOUR GMAIL AND APP PASSWORD GO HERE
                    return new PasswordAuthentication(
                        "vedicastrology123@gmail.com", 
                        "gftr qnle lejk qtmo" // <--- 16-character App Password (no spaces needed)
                    );
                }
            });
            session.setDebug(false);

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("vedicastrology123@gmail.com", "Steve Hora"));
            Address[] replyTo = { new InternetAddress("vedicastrology123@gmail.com") };
            message.setReplyTo(replyTo);
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userEmail));
            message.setSubject("Vedic Horoscope prepared by Steve Hora, Your register No.: " + registration);

            // 3. Create Multi-part Message
            Multipart multipart = new MimeMultipart();


String cid = firstName.replaceAll("\\s+", "") + "_vedic-horoscope";

String imgTag = "<img src=\"cid:" + cid + "\" alt=\"Birth Chart\" style='width:100%; max-width:600px;'>";


            String htmlTemplate = loadTemplate("userMail.html"); 
            String finalHtml = htmlTemplate.replace("{{CHART_IMAGE}}", imgTag).replace("${firstName}", firstName).replace("${lastName}", lastName).replace("${date}", date).replace("${time}", time).replace( "${city}", city).replace( "${state}", state).replace("${country}", country).replace("${questions}", questions).replace("${residency}", residency).replace("${registration}", registration);
            // Text Body Part
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setContent(finalHtml, "text/html; charset=utf-8");
            textPart.setHeader("Content-Type", "text/html; charset=utf-8");
            multipart.addBodyPart(textPart);

            MimeBodyPart attachPart = new MimeBodyPart();
            ByteArrayDataSource source = new ByteArrayDataSource(imageBytes, "image/png");
            attachPart.setDataHandler(new DataHandler(source));

            attachPart.setFileName(firstName + "_Horoscope.png");

            attachPart.setHeader("Content-ID", "<" + cid + ">");

            attachPart.setDisposition(MimeBodyPart.INLINE);

            multipart.addBodyPart(attachPart);

            // 4. Send
            message.setContent(multipart);
            Transport.send(message);

        } catch (Exception e) {
            System.err.println("SMTP Error: " + e.getMessage());
            e.printStackTrace(); // This will tell you if it's a 'Spam' rejection
        }
    }

    // Placeholder for your SMTP config logic
    private Properties getMailProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.setProperty("mail.smtp.port", "587");
        props.put("mail.debug", "false");
        return props;
    }
    
    // Placeholder for your Gmail App Password logic
    private Authenticator getAuthenticator() {
        return new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("vedicastrology123@gmail.com", "your-app-password");
            }
        };
    }
}