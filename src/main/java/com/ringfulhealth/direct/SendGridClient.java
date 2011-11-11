package com.ringfulhealth.direct;

import java.util.Properties;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

public class SendGridClient extends Client {

    String username, password;
    
    public SendGridClient (String username, String password, String p12KeyStore, String priKeyName, String priKeyPass) {
        super.p12KeyStore = p12KeyStore;
        super.priKeyName = priKeyName;
        super.priKeyPass = priKeyPass;
        
        this.username = username;
        this.password = password;
    }
    
    protected Session createSession() {
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", "smtp.sendgrid.net");
        props.put("mail.smtp.port", 587);
        props.put("mail.smtp.auth", "true");
 
        return Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                }
        );
    }

    protected void transport(Session session, MimeMessage msg) throws Exception {
        Transport transport = session.getTransport();
        transport.connect();
        transport.sendMessage(msg, msg.getRecipients(javax.mail.Message.RecipientType.TO));
        transport.close();
    }
       
}