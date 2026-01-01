package extrafeatures;

import java.io.File;
import java.util.Properties;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;

public class EmailerGmail {

    public static void sendDailyReport(String reportDate, String fromGmail, String appPassword, String toEmail) throws Exception {
        ExtraStore.initEmailOnly();

        if (fromGmail == null || fromGmail.trim().length() == 0) throw new Exception("From Gmail is empty.");
        if (appPassword == null || appPassword.trim().length() == 0) throw new Exception("App password is empty.");
        if (toEmail == null || toEmail.trim().length() == 0) throw new Exception("To Email is empty.");

        if (ExtraStore.alreadySentToday(reportDate)) {
            throw new Exception("Already SENT today for " + reportDate + " (check data/extra_email_log.txt).");
        }

        Object[][] dayRows = Reports.filterByDate(Reports.loadSalesTable(), reportDate, reportDate);
        double total = Reports.sumAmount(dayRows);

        String receiptPath = ExtraStore.RECEIPTS_DIR + "/sales_" + reportDate + ".txt";
        File attach = new File(receiptPath);
        if (!attach.exists()) throw new Exception("Receipt file not found: " + receiptPath);

        String subject = "Daily Sales Report - " + reportDate;
        String body = "Daily Sales Report\n" +
                "Report Date: " + reportDate + "\n" +
                "Total Sales Amount: " + String.format(java.util.Locale.US,"%.2f", total) + "\n";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromGmail, appPassword);
            }
        });

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(fromGmail));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            msg.setSubject(subject);

            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(body);

            MimeBodyPart attPart = new MimeBodyPart();
            DataSource src = new FileDataSource(attach);
            attPart.setDataHandler(new DataHandler(src));
            attPart.setFileName(attach.getName());

            Multipart mp = new MimeMultipart();
            mp.addBodyPart(textPart);
            mp.addBodyPart(attPart);

            msg.setContent(mp);
            Transport.send(msg);

            ExtraStore.logEmail(reportDate, toEmail, total, receiptPath, "SENT");
        } catch (Exception e) {
            ExtraStore.logEmail(reportDate, toEmail, total, receiptPath, "FAILED: " + e.getMessage());
            throw e;
        }
    }
}
