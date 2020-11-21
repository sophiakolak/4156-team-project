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
		mailer = MailerBuilder.withSMTPServer("smtp.aol.com", 587, "clinicaltriall", "thzbyorvaoauajzs").withTransportStrategy(TransportStrategy.SMTP_TLS).buildMailer();
	}
	
	public void sendEmails(Notification n) {
		Email e = EmailBuilder.startingBlank().from("clinicaltriall@aol.com").to(
				n.getPartEmail()).withSubject("Clinical TriAll").withPlainText("You have a match.").buildEmail();
		mailer.sendMail(e);
		e = EmailBuilder.startingBlank().from("clinicaltriall@aol.com").to(
				n.getResEmail()).withSubject("Clinical TriAll").withPlainText("You have a match.").buildEmail();
		mailer.sendMail(e);
	}
	
	public void genericSend(String to) {
		Email e = EmailBuilder.startingBlank().from("clinicaltriall@aol.com").to(
				to).withSubject("Clinical TriAll").withPlainText("You have a match.").buildEmail();
		mailer.sendMail(e);
	}
}