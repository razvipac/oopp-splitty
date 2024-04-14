package client.utils;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class InviteUtils {

    /**
     * Sends email invitation to the given email address from ooppteam2@outlook.com
     * @param recipientEmail Address to send to
     * @param inviteCode Event invite code to send
     * @return True iff successful, false otherwise
     */
    public boolean sendInvitation(String recipientEmail, String inviteCode) {
        final String fromEmail = "ooppteam2@outlook.com"; //  email
        final String password = ".KWn6.WL#)m9KuL"; // password

        // Set up mail server properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp-mail.outlook.com"); // Outlook SMTP host
        props.put("mail.smtp.port", "587"); // Outlook SMTP port

        //create Authenticator object to pass in Session.getInstance argument
        Authenticator auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        };
        Session session = Session.getInstance(props, auth);
        try {
            sendEmail(session, recipientEmail, fromEmail, inviteCode);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Constructs email format and sends it to recipient
     * @param session the mail session
     * @param recipientEmail the email address of the recipient
     * @param fromEmail the email address from which the invitation is sent
     * @param inviteCode the invitation code to be included in the email
     * @throws RuntimeException if there's an issue with sending the email
     */
    public static void sendEmail(Session session, String recipientEmail, String fromEmail, String inviteCode){
        try {
            // Create a MimeMessage object
            MimeMessage message = new MimeMessage(session);
            // Set From: header field
            message.setFrom(new InternetAddress(fromEmail));
            // Set To: header field
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            // Set Subject: header field
            message.setSubject("Invitation to Our Event");
            // Set Content: text
            message.setText("Dear friend,\n\nYou are invited to our event!\n" +
                    " Your invitation code is: " + inviteCode + "\n\nBest regards,\nEvent Team");
            // Send message
            Transport.send(message);
            System.out.println("Sent invitation to " + recipientEmail);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
