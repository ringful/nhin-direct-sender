package com.ringfulhealth.direct;

import java.io.ByteArrayInputStream;
import java.security.Key;
import java.security.cert.CertificateFactory;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import javax.mail.Header;
import net.suberic.crypto.EncryptionKeyManager;
import net.suberic.crypto.EncryptionManager;
import net.suberic.crypto.EncryptionUtils;
import net.suberic.crypto.bouncycastle.BouncySMIMEEncryptionKey;
import org.xbill.DNS.CERTRecord;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.Type;


public abstract class Client {
    
    protected String p12KeyStore;
    protected String priKeyName;
    protected String priKeyPass;
    
    public void sendMessage (String from, String to, String subject, String body) throws Exception {
        byte [] pubKeyCer = lookupCertificate (to);
        if (pubKeyCer == null || pubKeyCer.length == 0) {
            throw new Exception ("Cannot find public key certificate for " + to);
        }
        
        Session session = createSession ();
        
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(from));
        msg.setSender(new InternetAddress(from));
        msg.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
        msg.setSubject(subject);
        msg.setText(body);
        
        msg = signMsg(session, msg);
        msg = encryptMsg(session, msg, pubKeyCer);
        msg.saveChanges();
        
        Enumeration headers = msg.getAllHeaders();
        while (headers.hasMoreElements()) {
            Header h = (Header) headers.nextElement();
            System.out.println(h.getName() + ": " + h.getValue());
        }
        
        transport(session, msg);
    }
    
    public void sendMessage (String from, String to, String subject, Multipart multipart) throws Exception {
        byte [] pubKeyCer = lookupCertificate (to);
        if (pubKeyCer == null || pubKeyCer.length == 0) {
            throw new Exception ("Cannot find public key certificate for " + to);
        }
        
        Session session = createSession ();
        
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(from));
        msg.setSender(new InternetAddress(from));
        msg.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
        msg.setSubject(subject);
        msg.setContent(multipart);
        
        msg = signMsg(session, msg);
        msg = encryptMsg(session, msg, pubKeyCer);
        msg.saveChanges();
        
        transport(session, msg);
    }
    
    protected byte [] lookupCertificate (String email) {
        try {
            String domain = email.replaceAll("@", ".");
            
            CERTRecord cx = null;
            Record [] records = new Lookup(domain, Type.CERT).run();
            for (int i = 0; i < records.length; i++) {
                cx = (CERTRecord) records[i];
            }
            
            if (cx == null) {
                return null;
            } else {
                return cx.getCert ();
            }
            
        } catch (Exception e) {
            e.printStackTrace ();
            return null;
        }
    }
    
    protected MimeMessage encryptMsg(Session session, MimeMessage msg, byte [] pubKeyCer) throws Exception {
        EncryptionUtils encUtils = EncryptionManager.getEncryptionUtils(EncryptionManager.SMIME);
        
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate cert = (X509Certificate)cf.generateCertificate(new ByteArrayInputStream(pubKeyCer));
       
        // wrap certificate in BouncySMIMEEncryptionKey 
        BouncySMIMEEncryptionKey smimekey = new BouncySMIMEEncryptionKey();
        smimekey.setCertificate(cert);

        return encUtils.encryptMessage(session, msg, smimekey);
    }
    
    protected MimeMessage signMsg(Session session, MimeMessage mimeMessage) throws Exception {
        // Getting of the S/MIME EncryptionUtilities.
        EncryptionUtils encUtils = EncryptionManager.getEncryptionUtils(EncryptionManager.SMIME);

        // Loading of the S/MIME keystore from the file (stored as resource).
        char[] keystorePass = priKeyPass.toCharArray();
        EncryptionKeyManager encKeyManager = encUtils.createKeyManager();
        encKeyManager.loadPrivateKeystore(Client.class.getResourceAsStream(p12KeyStore), keystorePass);

        // Getting of the S/MIME private key for signing.
        Key privateKey = encKeyManager.getPrivateKey(priKeyName, keystorePass);

        // Signing the message.
        return encUtils.signMessage(session, mimeMessage, privateKey);
    }
    
    protected abstract Session createSession ();
    protected abstract void transport (Session session, MimeMessage msg) throws Exception;
    
}