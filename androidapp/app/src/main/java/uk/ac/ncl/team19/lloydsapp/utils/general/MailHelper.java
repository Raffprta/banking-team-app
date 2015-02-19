package uk.ac.ncl.team19.lloydsapp.utils.general;

import android.support.v4.app.Fragment;

import java.util.Calendar;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import uk.ac.ncl.team19.lloydsapp.R;

/**
 *  This Helper class was adapted and retrieved from: http://stackoverflow.com/questions/4345032/how-to-send-a-simple-email-programatically-exists-a-simple-way-to-do-it
 *  All attributions and credits belong to the original author.
 */

public class MailHelper {

    private String toList;
    private String ccList;
    private String bccList;
    private String subject;
    final private static String SMTP_SERVER = "smtp.gmail.com";
    private String from;
    private String txtBody;
    private String replyToList;
    private boolean authenticationRequired = false;
    private Fragment frag;

    public MailHelper(String from, String toList, String subject, String txtBody, Fragment frag) {
        this.txtBody = txtBody;
        this.subject = subject;
        this.from = from;
        this.toList = toList;
        this.ccList = null;
        this.bccList = null;
        this.replyToList = null;
        this.authenticationRequired = true;
        this.frag = frag;
    }


    public void sendAuthenticated() throws AddressException, MessagingException {
        authenticationRequired = true;
        send();
    }

    /**
     * Send an e-mail
     *
     * @throws MessagingException
     * @throws AddressException
     */
    public void send() throws AddressException, MessagingException {
        Properties props = new Properties();

        // set the host smtp address
        props.put("mail.smtp.host", SMTP_SERVER);
        props.put("mail.user", from);

        props.put("mail.smtp.starttls.enable", "true");  // needed for gmail
        props.put("mail.smtp.auth", "true"); // needed for gmail
        props.put("mail.smtp.port", "587");  // gmail smtp port

        /*Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("mobile@mydomain.com", "mypassword");
            }
        };*/


        Session session;

        if (authenticationRequired) {
            Authenticator auth = new SMTPAuthenticator();
            props.put("mail.smtp.auth", "true");
            session = Session.getDefaultInstance(props, auth);
        } else {
            session = Session.getDefaultInstance(props, null);
        }

        // get the default session
        session.setDebug(true);

        // create message
        Message msg = new javax.mail.internet.MimeMessage(session);

        // set from and to address
        try {
            msg.setFrom(new InternetAddress(from, from));
            msg.setReplyTo(new InternetAddress[]{new InternetAddress(from,from)});
        } catch (Exception e) {
            msg.setFrom(new InternetAddress(from));
            msg.setReplyTo(new InternetAddress[]{new InternetAddress(from)});
        }

        // set send date
        msg.setSentDate(Calendar.getInstance().getTime());

        // parse the recipients TO address
        java.util.StringTokenizer st = new java.util.StringTokenizer(toList, ",");
        int numberOfRecipients = st.countTokens();

        javax.mail.internet.InternetAddress[] addressTo = new javax.mail.internet.InternetAddress[numberOfRecipients];

        int i = 0;
        while (st.hasMoreTokens()) {
            addressTo[i++] = new javax.mail.internet.InternetAddress(st
                    .nextToken());
        }
        msg.setRecipients(javax.mail.Message.RecipientType.TO, addressTo);

        // parse the replyTo addresses
        if (replyToList != null && !"".equals(replyToList)) {
            st = new java.util.StringTokenizer(replyToList, ",");
            int numberOfReplyTos = st.countTokens();
            javax.mail.internet.InternetAddress[] addressReplyTo = new javax.mail.internet.InternetAddress[numberOfReplyTos];
            i = 0;
            while (st.hasMoreTokens()) {
                addressReplyTo[i++] = new javax.mail.internet.InternetAddress(
                        st.nextToken());
            }
            msg.setReplyTo(addressReplyTo);
        }

        // parse the recipients CC address
        if (ccList != null && !"".equals(ccList)) {
            st = new java.util.StringTokenizer(ccList, ",");
            int numberOfCCRecipients = st.countTokens();

            javax.mail.internet.InternetAddress[] addressCC = new javax.mail.internet.InternetAddress[numberOfCCRecipients];

            i = 0;
            while (st.hasMoreTokens()) {
                addressCC[i++] = new javax.mail.internet.InternetAddress(st
                        .nextToken());
            }

            msg.setRecipients(javax.mail.Message.RecipientType.CC, addressCC);
        }

        // parse the recipients BCC address
        if (bccList != null && !"".equals(bccList)) {
            st = new java.util.StringTokenizer(bccList, ",");
            int numberOfBCCRecipients = st.countTokens();

            javax.mail.internet.InternetAddress[] addressBCC = new javax.mail.internet.InternetAddress[numberOfBCCRecipients];

            i = 0;
            while (st.hasMoreTokens()) {
                addressBCC[i++] = new javax.mail.internet.InternetAddress(st
                        .nextToken());
            }

            msg.setRecipients(javax.mail.Message.RecipientType.BCC, addressBCC);
        }

        // set header
        msg.addHeader("X-Mailer", "MyAppMailer");
        msg.addHeader("Precedence", "bulk");
        // setting the subject and content type
        msg.setSubject(subject);

        Multipart mp = new MimeMultipart("related");

        // set body message
        MimeBodyPart bodyMsg = new MimeBodyPart();
        bodyMsg.setText(txtBody, "iso-8859-1");

        mp.addBodyPart(bodyMsg);


        msg.setContent(mp);

        // send it
        try {
            javax.mail.Transport.send(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * SimpleAuthenticator is used to do simple authentication when the SMTP
     * server requires it.
     */
    private class SMTPAuthenticator extends javax.mail.Authenticator {

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {

            String username = frag.getResources().getString(R.string.mailer_user);
            String password = frag.getResources().getString(R.string.mailer_pass);

            return new PasswordAuthentication(username, password);
        }
    }

    public String getToList() {
        return toList;
    }

    public void setToList(String toList) {
        this.toList = toList;
    }

    public String getCcList() {
        return ccList;
    }

    public void setCcList(String ccList) {
        this.ccList = ccList;
    }

    public String getBccList() {
        return bccList;
    }

    public void setBccList(String bccList) {
        this.bccList = bccList;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTxtBody(String body) {
        this.txtBody = body;
    }

    public String getReplyToList() {
        return replyToList;
    }

    public void setReplyToList(String replyToList) {
        this.replyToList = replyToList;
    }

    public boolean isAuthenticationRequired() {
        return authenticationRequired;
    }

    public void setAuthenticationRequired(boolean authenticationRequired) {
        this.authenticationRequired = authenticationRequired;
    }

}
