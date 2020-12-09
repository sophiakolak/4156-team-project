package models;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import units.SqliteDB;

public class Notification {
  private String time;
  private String trialName;
  private String message;

  /**
   * Create new notification.
   */
  public Notification(SqliteDB db, int id, String time, String message) {
    if (!isValidIsoDateTime(time)) {
      throw new IllegalArgumentException("invalid date/time");
    }
    this.time = time;
    if (id <= 0) {
      throw new IllegalArgumentException("ID must be > 0");
    }
    trialName = db.loadTrial(id).getName();
    if (message == "") {
      throw new IllegalArgumentException("Message must not be empty");
    }
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
  
  public void store(SqliteDB db, int trialID, int parent, boolean toRes) {
    if (toRes) {
      db.insertNotification(trialID, parent, 0, time, message);
    } else {
      db.insertNotification(trialID, 0, parent, time, message);
    }
  }
  
  /**
   * Checks whether the given time string is valid.
   * @param time Time string.
   * @return Whether it is valid.
   */
  private boolean isValidIsoDateTime(String time) {
    try {
      LocalTime.parse(time);
      return true;
    } catch (DateTimeParseException e) {
      return false;
    }
  }

}