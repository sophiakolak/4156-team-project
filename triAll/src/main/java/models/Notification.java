package models;

import units.SqliteDB;

public class Notification {
  private String time;
  private String trialName;
  private String message;

  /**
   * Create new notification.
   */
  public Notification(SqliteDB db, int id, String time, String message) {
    this.time = time;
    trialName = db.loadTrial(id).getName();
    this.time = time;
    this.message = message;
  }
  
  public String getTime() {
    return time;
  }
  
  public String getTrial() {
    return trialName;
  }
  
  public String getMessage() {
    return message;
  }
}