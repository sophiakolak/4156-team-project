package models;

import java.time.format.DateTimeFormatter;
import java.time.*; 
import java.time.format.DateTimeParseException;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

import com.google.gson.JsonArray;
import units.SqliteDB;

public class Trial {

  private int id;
  private int resID;
  private String name;
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
  public Trial(int id, String n, String d, double lat, double lon, String location, String s, 
      String e, double p, int irb, int pn, int pc, Criteria crit) {
    if (id < 0) {
      throw new IllegalArgumentException("id must not be less than 0");
    }
    this.id = id;
    if (n == "") {
      throw new IllegalArgumentException("name cannot be empty");
    }
    this.name = n;
    if (d == "") {
      throw new IllegalArgumentException("description must not be empty");
    }
    this.desc = d;
    if (lat < -90.0 || lat > 90.0) {
      throw new IllegalArgumentException("latitude must be between -90.0 and 90.0");
    }
    this.lat = lat;
    if (lon < -180.0 || lon > 180.0) {
      throw new IllegalArgumentException("longitude must be between -180.0 and 180.0");
    }
    this.lon = lon;
    if (location == "") {
      throw new IllegalArgumentException("location must not be empty");
    }
    this.location = location;
    if (!isValidDate(s)) {
      throw new IllegalArgumentException("start date is not valid");
    }
    this.start = s;
    if (!isValidDate(e)) {
      throw new IllegalArgumentException("end date is not valid");
    }
    this.end = e;
    if (pay < 0) {
      throw new IllegalArgumentException("pay must not be negative");
    }
    this.pay = p;
    if (Integer.toString(irb).length() < 4 || Integer.toString(irb).length() > 5) {
      throw new IllegalArgumentException("IRB number not valid");
    }
    this.irb = irb;
    if (pn < 0) {
      throw new IllegalArgumentException("participants needed must not be negative");
    }
    this.partNeeded = pn;
    if (pc < 0) {
      throw new IllegalArgumentException("participants confirmed must not be negative");
    }
    this.partConfirmed = pc;
    this.crit = crit;
  }
  
  /**
   * Creates trial object.
   */
  public Trial(SqliteDB db, JsonArray form, int parent) {
    name = form.get(0).getAsJsonObject().get("value").getAsString();
    desc = form.get(1).getAsJsonObject().get("value").getAsString();
    location = form.get(2).getAsJsonObject().get("value").getAsString();
    lat = form.get(3).getAsJsonObject().get("value").getAsDouble();
    lon = form.get(4).getAsJsonObject().get("value").getAsDouble();
    start = form.get(5).getAsJsonObject().get("value").getAsString();
    end = form.get(6).getAsJsonObject().get("value").getAsString();
    pay = form.get(7).getAsJsonObject().get("value").getAsDouble();
    irb = form.get(8).getAsJsonObject().get("value").getAsInt();
    partNeeded = form.get(9).getAsJsonObject().get("value").getAsInt();
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
      user.setData(db.loadData(user.getID()));
      if (crit.matches(user.getData()) && !db.matchExists(user.getID(), id)) {
        new Match(user, this, db);
      }
    }
  }

  public int getID() {
    return id;
  }
  
  public int getRes() {
    return resID;
  }
  
  public String getName() {
    return name;
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
  
  public boolean isValidDate(String date) {
    try {
        DateTimeFormatter.ISO_DATE.parse(date);
        return true;
    } catch (DateTimeParseException e) {
        return false;
    }
  }


}
