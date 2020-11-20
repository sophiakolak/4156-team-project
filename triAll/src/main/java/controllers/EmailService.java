package controllers;

import models.Notification;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;

public class EmailService {
	
	private Mailer mailer;
	
	public EmailService() {
		mailer = MailerBuilder.withSMTPServer("smtp.mailtrap.io", 25).buildMailer();
	}
	
	public void sendEmails(Notification n) {
		Email e = EmailBuilder.startingBlank().from("jg3796@columbia.edu").to(
				n.getPartEmail()).withSubject("Clinical TriAll").withPlainText("You have a match.").buildEmail();
		mailer.sendMail(e);
		e = EmailBuilder.startingBlank().from("jg3796@columbia.edu").to(
				n.getResEmail()).withSubject("Clinical TriAll").withPlainText("You have a match.").buildEmail();
		mailer.sendMail(e);
	}
}