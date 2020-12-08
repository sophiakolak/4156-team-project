package models;

import units.SqliteDB;

public class Match {

  private int id;
  private Trial trial;
  //private User researcher;
  //private User participant;
  private double distance;
  private String status;
  private String email;

  /**
   * Creates a new Trial-Participant match object with specified properties.
   * @param id The row in database of the match when stored
   * @param t The respective trial object of this match
   * @param d The distance between trial and participant locations
   * @param s The status of the match (e.g. accepted, rejected)
   */
  public Match(int id, Trial t, double d, String s) {
    if (id <= 0) {
      throw new IllegalArgumentException("Id must be > 0");
    }
    this.id = id;
    trial = t;
    if (d < 0.0) {
      throw new IllegalArgumentException("distance must be positive");
    }
    distance = d;
    if (s == "" || !(s.equals("accepted") || s.equals("rejected") || s.equals("pending"))) {
      throw new IllegalArgumentException("status must not be empty, must be either accepted, "
          + "rejected, or pending");
    }
    status = s;
  }
  
  /**
   * Creates a new Trial-Participant match object with specified properties.
   * @param u User associated with the match.
   * @param t Trial associated with the match.
   * @param db Database.
   */
  public Match(User u, Trial t, SqliteDB db) {
    trial = t;
    id = db.insertMatch(u, t);
    status = "pending";
    email = u.getEmail();
    EmailService.newMatchSend(t, email);
  }
  
  /**
   * Accepts the match.
   * @param db Database
   * @return Whether the operation was successful.
   */
  public boolean accept(SqliteDB db) {
    if (!trial.confirmOne(db)) {
      return false;
    }
    status = "accept";
    db.acceptMatch(trial.getID());
    EmailService.acceptMatchSend(trial, db.loadRes(trial.getRes()).getEmail(), email);
    return true;
  }
  
  /**
   * Rejects the match.
   * @param db Database
   * @return Whether the operation was successful.
   */
  public boolean reject(SqliteDB db) {
    status = "reject";
    db.rejectMatch(trial.getID());
    return true;
  }

  public double getDistance() {
    return distance;
  }
  
  public void setDistance(double d) {
    distance = d;
  }

  public int getID() {
    return this.id;
  }

  public Trial getTrial() {
    return trial;
  }

  public void setStatus(String status) {
    this.status = status;
  }
  
  public String getStatus() {
    return status;
  }

}
