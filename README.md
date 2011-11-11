
== Background

The NHIN Direct Project is a secure email specification (based on S/MIME) for
healthcare providers to exchange patient information in a secure and HIPAA
compliant manner. It provides a simple mechanism for patients and clincians to
send protected health information from one system (e.g., a hospital patient
portal) to another (e.g., a personal health record).  Microsoft HealthVault is
a pioneer in implementing the Direct protocol.

This project provides a easy API to send Direct secure messages using a generic
SMTP service. The Quick Start demonstrates how to use GMail (or Google Apps)
accounts to send records into HealthVault's staging environment.  Support for
more SMTP providers can be easily added via community contribution.

This project does NOT provide a fully featured email server to receive Direct
secure email messages from another provider. For a fully featured Direct email gateway, please refer to this project: http://code.google.com/p/nhin-d/

== Quick start

1. Add a few JARs to your local maven repo: (I could not find those two JARs in official Mavne repos. If you can please contact me)

mvn install:install-file -DgroupId=javamail.crypto.bouncycastle -DartifactId=smime -Dversion=060622 -Dpackaging=jar -Dfile=bin/smime_060622.jar

mvn install:install-file -DgroupId=javamail -DartifactId=crypto -Dversion=060622 -Dpackaging=jar -Dfile=bin/crypto_060622.jar

2. Build the binary: 

mvn clean
mvn package
cp target/sender-1.0.jar bin/
cd bin

3. Run the tester app:

java -jar sender-1.0.jar gmail "no-reply-test@ringful.com" password "no-reply-test@ringful.com" "yourname@direct.healthvault-stage.com" "test subject" "test message from Google apps"

NOTE:
    
    * You need password on the command line to send email through no-reply-test@ringful.com. We change this password often. Email the administrator of this project for the current one.

    * You must have an account at http://direct.healthvault-stage.com/ in order to view the message.

== To use your own GMail or Google Apps address

1. Create a public / private key pair for the email address, and export the key pair to a yourname.p12 file. On the Mac this can be easily done via Keychain Assistant.

2. Export a digital certificate for the public key from the p12 key pair, and email that to hvbd@microsoft.com to set it up on HealthVault staging server.

2. Copy yourname.p12 to src/main/resources

3. Edit Tester.java to reflect your p12 file name, private key name, keystore passcode, as well as GMail account name and password.

4. Build and run. Notice that the GMail account username must match the FROM address.

== Advanced topics

1. The library is designed so that it can use any SMTP gateway provider to send NHIN Direct messages. We are showing how to use it in GMail here. Your contribution for other providers are wlecome here!

2. The library can send message to any NHIN Direct enabled Perosnal Health Record and Patient Portal. We are only using Microsoft HealthVault Stage server as an example here.

 

