package models;

import com.google.gson.JsonArray;
import units.SqliteDB;

public class Trial {

  private int id;
  private int resID;
  private String desc;
  private double lat;
  private double lon;
  private String location;
  private String start;
  private String end;
  private double pay;
  private int irb;
  private int partNeeded;
  private int partConfirmed;

  private Criteria crit;

  /**
   * Creates trial object.
   */
  public Trial(int id, User r, String d, double lat, double lon, String location, String s, 
      String e, double p, int irb, int pn, int pc, Criteria crit) {

    this.id = id;
    //this.researcher = r;
    this.desc = d;
    this.lat = lat;
    this.lon = lon;
    this.location = location;
    this.start = s;
    this.end = e;
    this.pay = p;
    this.irb = irb;
    this.partNeeded = pn;
    this.partConfirmed = pc;
    this.crit = crit;
  }
  
  /**
   * Creates trial object.
   */
  public Trial(SqliteDB db, JsonArray form, int parent) {
    desc = form.get(0).getAsJsonObject().get("value").getAsString();
    location = form.get(1).getAsJsonObject().get("value").getAsString();
    lat = form.get(2).getAsJsonObject().get("value").getAsDouble();
    lon = form.get(3).getAsJsonObject().get("value").getAsDouble();
    start = form.get(4).getAsJsonObject().get("value").getAsString();
    end = form.get(5).getAsJsonObject().get("value").getAsString();
    pay = form.get(6).getAsJsonObject().get("value").getAsDouble();
    irb = form.get(7).getAsJsonObject().get("value").getAsInt();
    partNeeded = form.get(8).getAsJsonObject().get("value").getAsInt();
    partConfirmed = 0;
    resID = parent;
    id = db.insertTrial(this);
    crit = new Criteria(db, form, id, "trial_criteria");
    checkMatches(db);
  }
  
  /**
   * Update trial after edits to information.
   * @param db Database.
   * @param form Form information to parse.
   * @return Whether the operation succeeds.
   */
  public boolean update(SqliteDB db, JsonArray form) {
    desc = form.get(1).getAsJsonObject().get("value").getAsString();
    location = form.get(2).getAsJsonObject().get("value").getAsString();
    lat = form.get(3).getAsJsonObject().get("value").getAsDouble();
    lon = form.get(4).getAsJsonObject().get("value").getAsDouble();
    start = form.get(5).getAsJsonObject().get("value").getAsString();
    end = form.get(6).getAsJsonObject().get("value").getAsString();
    pay = form.get(7).getAsJsonObject().get("value").getAsDouble();
    irb = form.get(8).getAsJsonObject().get("value").getAsInt();
    partNeeded = form.get(9).getAsJsonObject().get("value").getAsInt();
    if (db.updateTrial(this) == 0) {
      return false;
    }
    crit.updateCrit(db, form);
    checkMatches(db);
    return true;
  }
  
  /**
   * Checks for new matches.
   * @param db Database.
   */
  private void checkMatches(SqliteDB db) {
    if (partNeeded == partConfirmed) {
      return;
    }
    for (String email : db.partSet()) {
      User user = db.loadPart(email);
      if (crit.matches(user.getData()) && !db.matchExists(user.getID(), id)) {
        new Match(user, this, db);
        //send emails
      }
    }
  }

  public int getID() {
    return id;
  }
  
  public int getRes() {
    return resID;
  }

  public Criteria getCriteria() {
    return crit;
  }
  
  public String getDesc() {
    return desc;
  }

  public double getLat() {
    return this.lat;
  }

  public double getLong() {
    return this.lon;
  }
  
  public String getLocation() {
    return location;
  }

  public String getStart() {
    return start;
  }
  
  public String getEnd() {
    return end;
  }
  
  public int getIrb() {
    return irb;
  }
  
  public double getPay() {
    return pay;
  }
  
  public int getPartNeeded() {
    return partNeeded;
  }

  public int getPartConf() {
    return partConfirmed;
  }

  /**
   * Records a confirmed participant.
   * @param db Database.
   * @return Whether the trial could confirm the participant.
   */
  public boolean confirmOne(SqliteDB db) {
    if (partConfirmed == partNeeded) {
      return false;
    }
    partConfirmed++;
    if (db.updateTrial(this) == 0) {
      return false;
    }
    return true;
  }


}
