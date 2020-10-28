package com.samaritan.util;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.logging.Level;

/**
 * This class is used for sending an email message(html) to a recipient address.
 * By default the class sends the message from the gmail SMTP server.
 * Feel free to change the SMTP_HOST, SMTP_PORT, USER and PASSWORD fields to reflect your configuration.
 * @author electron
 */
public class EmailSender{

    //the email params
    private String to ;
    private String subject ;
    private String messageBody ;

    //classes from javax.mail to help send email
    private Session session ;

    //properties of the SMTP server
    private final String SMTP_HOST = "smtp.gmail.com" ;
    private final String SMTP_PORT = "587" ;

    //GMAIL credentials
    private final String USER = "labdul_qadir@st.ug.edu.gh" ;
    private final String PASSWORD = "elnhmzxvagofamks" ;

    //constructor
    public EmailSender(String to, String subject, String messageBody){

        //initialize email params
        this.to = to ;
        this.subject = subject ;
        this.messageBody = messageBody ;

        //put the properties of the smtp server into the properties object
        //for holding properties of the SMTP server
        Properties properties = new Properties();
        properties.put("mail.smtp.host",SMTP_HOST) ;
        properties.put("mail.smtp.port",SMTP_PORT) ;
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.auth", "true");

        //create the session object
        session = Session.getInstance(properties, new javax.mail.Authenticator(){

            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USER, PASSWORD) ;
            }
        });
        session.setDebug(true) ;

    }

    public void setTo(String to){

        this.to = to ;
    }

    public String getTo(){

        return to ;
    }

    public void setSubject(String subject){

        this.subject = subject ;
    }

    public String getSubject(){

        return subject ;
    }

    public void setMessageBody(String messageBody){

        this.messageBody = messageBody ;
    }

    public String getMessageBody(){

        return messageBody ;
    }


    /**
     * send the message to the email address in to
     * @return true if message was successfully sent
     */
    public boolean sendMessage(){

        try {
            // create a message
            Message message = new MimeMessage(session);
            // From Address
            message.setFrom(new InternetAddress(USER)) ;
            // TO Address
            InternetAddress toAddress = new InternetAddress(to) ;
            message.addRecipient(Message.RecipientType.TO, toAddress) ;
            // The Subject
            message.setSubject(subject) ;
            // Now the message body.
            message.setContent(messageBody, "text/html") ;
            // Finally, send the message!
            Transport.send(message) ;
            //write to log
            LoggingUtil.logMessageToConsole(getClass().getName(), Level.INFO, "Email message sent to " + to) ;
        }
        catch(MessagingException ex){

            ex.printStackTrace() ;
        }

        return true ;
    }

}

