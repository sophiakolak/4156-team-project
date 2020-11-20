package controllers;

import models.Notification;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;

class EmailService {
	
	private Mailer mailer;
	
	public EmailService() {
		mailer = MailerBuilder.withSMTPServer("", 25).buildMailer();
	}
	
	public void sendEmails(Notification n) {
		Email e = EmailBuilder.startingBlank().from("").to(n.getPartEmail()).withSubject("").withHTMLText(" ").buildEmail();
		mailer.sendMail(e);
		e = EmailBuilder.startingBlank().from("").to(n.getResEmail()).withSubject("").withHTMLText(" ").buildEmail();
		mailer.sendMail(e);
	}
}