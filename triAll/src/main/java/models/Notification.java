package models;

import controllers.EmailService;

public class Notification {
	private String time;
	private String partEmail;
	private String resEmail;
	
	public Notification(String p, String r){
		partEmail = p;
		resEmail = r;
	}
	
	public String getPartEmail() {
		return partEmail;
		
	}
	
	public String getResEmail() {
		return resEmail;
	}
	
	public void notifyUsers() {
      EmailService e = new EmailService();
      e.sendEmails(this);
      time = java.time.LocalTime.now().toString();
	}
}