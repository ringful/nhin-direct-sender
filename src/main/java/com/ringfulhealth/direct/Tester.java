package com.ringfulhealth.direct;

public class Tester {
    
    public static void main(String[] args) throws Exception {
        if (args.length != 7) {
            System.out.println("Please provide all required 7 parameters - 1. provider 2. username 3. password 4. from 5. to 6. subject 7. body");
            System.exit(0);
        }
        
        String provider = args [0];
        String username = args [1];
        String password = args [2];
        String from = args [3];
        String to = args [4];
        String subject = args [5];
        String body = args [6];
        
        if (!to.endsWith("direct.healthvault-stage.com")) {
            System.out.println("The test program can only send to Healthvault staging right now. Since that is the only key exchange we have established.");
            System.exit(0);
        }
        
        Client client = null;
        if (provider.equalsIgnoreCase("gmail")) {
            client = new GmailClient (
                username, password,
                "/HV_Ringful_Test.p12", 
                "HV Ringful Test", 
                "ringful");
        } else if (provider.equalsIgnoreCase("sendgrid")) {
            client = new SendGridClient (
                username, password,
                "/HV_Ringful_Test.p12", 
                "HV Ringful Test", 
                "ringful");
        } else {
            System.out.println("We only support GMail and SendGrid as senders for now. To support your own sender, please consider contributing to this project!");
            System.exit(0);
        }
        
        client.sendMessage(from, to, subject, body);
        System.out.println("Success! Go check http://direct.healthvault-stage.com/ now");
    }
    
}