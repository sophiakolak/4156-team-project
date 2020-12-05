package models;

import controllers.EmailService;

public class Notification {
  private String time;
  private String partEmail;
  private String resEmail;

  /**
   * Create new notification.
   */
  public Notification(String p, String r) {
    partEmail = p;
    resEmail = r;
  }

  public String getPartEmail() {
    return partEmail;

  }

  public String getResEmail() {
    return resEmail;
  }
  
  public String getTime() {
    return time;
  }

  /**
   * Send notification to stored email addresses.
   */
  public void notifyUsers() {
    EmailService e = new EmailService();
    e.sendEmails(this);
    time = java.time.LocalTime.now().toString();
  }
}