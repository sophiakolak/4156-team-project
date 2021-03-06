package models;

import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;

public class EmailService {
  
  /**
   * Sends email denoting a new match.
   * @param t Trial associated with the match.
   * @param to Recipient address.
   */
  public static String newMatchSend(Trial t, String to) {
    Mailer mailer = MailerBuilder.withSMTPServer("smtp.gmail.com", 587, "triallclinical@gmail.com", 
        "MontagueCapulet").withTransportStrategy(TransportStrategy.SMTP_TLS).buildMailer();
    String body = "<html><p>Congratulations! You meet the criteria for trial " + t.getName() 
        + ".<p>" + "<br> <p>Log into your account to view this this trial. If you choose to "
        + "accept a place in this trial, the researcher will reach out to you shortly.<html>";
    Email e = EmailBuilder.startingBlank().from("triallclinical@gmail.com").to(to)
        .withSubject("Clinical TriAll").withHTMLText(body).buildEmail();
    mailer.sendMail(e);
    return body;
  }
  
  /**
   * Sends email denoting a match's acceptance.
   * @param t Trial associated with the match.
   * @param to Recipient address.
   * @param email Address of other party to the match.
   */
  public static String acceptMatchSend(Trial t, String to, String email) {
    Mailer mailer = MailerBuilder.withSMTPServer("smtp.gmail.com", 587, "triallclinical@gmail.com", 
        "MontagueCapulet").withTransportStrategy(TransportStrategy.SMTP_TLS).buildMailer();
    String body = "<html><p>Congratulations! A new participant has accepted a tentative "
        + "place in " + t.getName() + ".<p>" + "<br> <p>You can reach out to them at this "
        + "email address: " + email + "<p><html>";
    Email e = EmailBuilder.startingBlank().from("triallclinical@gmail.com").to(to)
        .withSubject("Clinical TriAll").withHTMLText(body).buildEmail();
    mailer.sendMail(e);
    return body;
  }
}
