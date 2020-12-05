package controllers;

import models.Notification;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;

public class EmailService {

  private Mailer mailer;

  public EmailService() {
    mailer = MailerBuilder.withSMTPServer("smtp.gmail.com", 587, "triallclinical@gmail.com", 
        "MontagueCapulet").withTransportStrategy(TransportStrategy.SMTP_TLS).buildMailer();
  }

  /**
   * Send emails to addresses stored in a notification pair.
   * @param n Object storing addresses.
   */
  public void sendEmails(Notification n) {
    Email e = EmailBuilder.startingBlank().from("triallclinical@gmail.com").to(n.getPartEmail())
        .withSubject("Clinical TriAll").withPlainText("You have a match.").buildEmail();
    mailer.sendMail(e);
    e = EmailBuilder.startingBlank().from("triallclinical@gmail.com").to(n.getResEmail())
        .withSubject("Clinical TriAll").withPlainText("You have a match.").buildEmail();
    mailer.sendMail(e);
  }

  /**
   * Sends a single generic email.
   * @param to Recipient address of the email.
   */
  public void genericSend(String to) {
    Email e = EmailBuilder.startingBlank().from("triallclinical@gmail.com").to(to)
        .withSubject("Clinical TriAll").withPlainText("You have a match.").buildEmail();
    mailer.sendMail(e);
  }
}
