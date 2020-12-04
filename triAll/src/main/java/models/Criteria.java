package models;

import com.google.gson.JsonArray;
import units.SqliteDB;

public class Criteria {
  private int id;
  private int extID;
  private int minAge;
  private int maxAge;
  private double minHeight;
  private double maxHeight;
  private double minWeight;
  private double maxWeight;
  private String gender;
  private String race;
  private String nationality;

  /**
   * Creates criteria object.
   */
  public Criteria(int id, int extID, int minAge, int maxAge, double minHeight, double maxHeight, double minWeight,
      double maxWeight, String gender, String race, String nationality) {
    this.id = id;
    this.extID = extID;
    this.minAge = minAge;
    this.maxAge = maxAge;
    this.minHeight = minHeight;
    this.maxHeight = maxHeight;
    this.minWeight = minWeight;
    this.maxWeight = maxWeight;
    this.gender = gender;
    this.race = race;
    this.nationality = nationality;
  }
  
  public Criteria(SqliteDB db, JsonArray form, int parent, String table) {
    if (table.equals("participant_data")) {
      makeData(db, form, parent);
    } else if (table.equals("trial_criteria")) {
      makeCrit(db, form, parent);
    }
    
  }
  
  public void makeData(SqliteDB db, JsonArray form, int parent) {
    minAge = form.get(8).getAsJsonObject().get("value").getAsInt();
    maxAge = minAge;
    minHeight = form.get(13).getAsJsonObject().get("value").getAsInt();
    maxHeight = minHeight;
    minWeight = form.get(16).getAsJsonObject().get("value").getAsInt();
    maxWeight = minWeight;
    gender = form.get(7).getAsJsonObject().get("value").getAsString();
    race = form.get(17).getAsJsonObject().get("value").getAsString();
    nationality = form.get(18).getAsJsonObject().get("value").getAsString();
    extID = parent;
    id = db.insertCriteria("participant_data", this);
  }
  
  public void makeCrit(SqliteDB db, JsonArray form, int parent) {
    minAge = form.get(10).getAsJsonObject().get("value").getAsInt();
    maxAge = form.get(11).getAsJsonObject().get("value").getAsInt();
    minHeight = form.get(16).getAsJsonObject().get("value").getAsInt();
    maxHeight = form.get(20).getAsJsonObject().get("value").getAsInt();
    minWeight = form.get(23).getAsJsonObject().get("value").getAsInt();
    maxWeight = form.get(26).getAsJsonObject().get("value").getAsInt();
    gender = form.get(9).getAsJsonObject().get("value").getAsString();
    race = form.get(27).getAsJsonObject().get("value").getAsString();
    nationality = form.get(28).getAsJsonObject().get("value").getAsString();
    extID = parent;
    id = db.insertCriteria("trial_criteria", this);
  }
  
  public boolean updateData(SqliteDB db, JsonArray form) {
    minAge = form.get(7).getAsJsonObject().get("value").getAsInt();
    maxAge = form.get(7).getAsJsonObject().get("value").getAsInt();
    minHeight = form.get(12).getAsJsonObject().get("value").getAsInt();
    maxHeight = form.get(12).getAsJsonObject().get("value").getAsInt();
    minWeight = form.get(15).getAsJsonObject().get("value").getAsInt();
    maxWeight = form.get(15).getAsJsonObject().get("value").getAsInt();
    gender = form.get(6).getAsJsonObject().get("value").getAsString();
    race = form.get(16).getAsJsonObject().get("value").getAsString();
    nationality = form.get(17).getAsJsonObject().get("value").getAsString();
    if (db.updateCriteria("participant_data", this) == 0) {
      return false;
    }
    return true;
  }
  
  public boolean updateCrit(SqliteDB db, JsonArray form) {
    minAge = form.get(7).getAsJsonObject().get("value").getAsInt();
    maxAge = form.get(8).getAsJsonObject().get("value").getAsInt();
    minHeight = form.get(13).getAsJsonObject().get("value").getAsInt();
    maxHeight = form.get(17).getAsJsonObject().get("value").getAsInt();
    minWeight = form.get(20).getAsJsonObject().get("value").getAsInt();
    maxWeight = form.get(23).getAsJsonObject().get("value").getAsInt();
    gender = form.get(6).getAsJsonObject().get("value").getAsString();
    race = form.get(24).getAsJsonObject().get("value").getAsString();
    nationality = form.get(25).getAsJsonObject().get("value").getAsString();
    if(db.updateCriteria("trial_criteria", this) == 0) {
      return false;
    }
    return true;
  }

  public int getID() {
    return id;
  }
  
  public int getParent() {
    return extID;
  }

  public int getMinAge() {
    return minAge;
  }

  public double getMinHeight() {
    return minHeight;
  }

  public double getMinWeight() {
    return minWeight;
  }
  
  public int getMaxAge() {
    return maxAge;
  }

  public double getMaxHeight() {
    return maxHeight;
  }

  public double getMaxWeight() {
    return maxWeight;
  }

  public String getGender() {
    return gender;
  }

  public String getRace() {
    return race;
  }

  public String getNationality() {
    return nationality;
  }

  /**
   * Checks if criteria match.
   */
  public boolean matches(Criteria c) {
    if (c.getMinAge() >= minAge && c.getMinAge() <= maxAge && c.getMinHeight() >= minHeight && c.getMinHeight() <= maxHeight
        && c.getMinWeight() >= minWeight && c.getMinWeight() <= maxWeight && c.getGender().equals(gender)
        && c.getRace().equals(race) && c.getNationality().equals(nationality)) {
      return true;
    } else {
      return false;
    }
  }
}