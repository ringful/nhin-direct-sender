package com.ringfulhealth.direct;

import java.util.Properties;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

public class GmailClient extends Client {

    String username, password;
    
    public GmailClient (String username, String password, String p12KeyStore, String priKeyName, String priKeyPass) {
        super.p12KeyStore = p12KeyStore;
        super.priKeyName = priKeyName;
        super.priKeyPass = priKeyPass;
        
        this.username = username;
        this.password = password;
    }
    
    protected Session createSession() {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
 
        return Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                }
        );
    }

    protected void transport(Session session, MimeMessage msg) throws Exception {
        Transport transport = session.getTransport("smtp");
        transport.connect();
        Transport.send(msg);
        transport.close();
    }
       
}