package com.sinapsi.webservice.engine.system;

import com.sinapsi.model.UserInterface;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Email adapter that send a email
 * 
 * @author Ayoub
 *
 */
public class EmailAdapter {
    private UserInterface user;
    private final String username;
    private final String password;
    
    public static final String SERVICE_EMAIL = "SERVICE_EMAIL";

    public EmailAdapter(UserInterface u) {
        this.user = u;
        ResourceBundle bundle = ResourceBundle.getBundle("email");
        username = bundle.getString("email.user");
        password = bundle.getString("email.password");
    }

    /**
     * Send email
     * 
     * @param user user interface
     * @param msg message of the email
     * @param subject subject of the email
     */
    public void sendMailToUser(String msg, String subject) {
        Properties props = System.getProperties();
        props.put("mail.smtp.starttls.enable", true);
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.user", username);
        props.put("mail.smtp.password", password);
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", true);

        Session session = Session.getInstance(props, null);
        MimeMessage message = new MimeMessage(session);

        try {
            InternetAddress from = new InternetAddress(username);
            message.setSubject(subject);
            message.setFrom(from);
            message.addRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(user.getEmail()));

            // Create a multi-part to combine the parts
            Multipart multipart = new MimeMultipart("alternative");

            // Create your text message part
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(msg);

            // Add the text part to the multipart
            multipart.addBodyPart(messageBodyPart);

            // Add html part to multi part
            multipart.addBodyPart(messageBodyPart);

            // Associate multi-part with message
            message.setContent(multipart);

            // Send message
            Transport transport = session.getTransport("smtp");
            transport.connect("smtp.gmail.com", username, password);
            System.out.println("Transport: " + transport.toString());
            transport.sendMessage(message, message.getAllRecipients());

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
