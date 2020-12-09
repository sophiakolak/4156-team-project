package models;

import com.google.gson.JsonArray;

import database.SqliteDB;

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
  public Criteria(int id, int extID, int minAge, int maxAge, double minHeight, 
      double maxHeight, double minWeight,
      double maxWeight, String gender, String race, String nationality) {
    if (id <= 0 || extID <= 0) {
      throw new IllegalArgumentException("id and extID must be > 0");
    }
    this.id = id;
    this.extID = extID;
    if (minAge < 18 || maxAge > 120) {
      throw new IllegalArgumentException("min age must be > 18 and < 120");
    }
    this.minAge = minAge;
    if (maxAge > 120 || maxAge < 18) {
      throw new IllegalArgumentException("max age must be < 120 and > 18");
    }
    this.maxAge = maxAge;
    if (minHeight < 0 || maxHeight < 0) {
      throw new IllegalArgumentException("min and max height must be >= 0");
    }
    this.minHeight = minHeight;
    this.maxHeight = maxHeight;
    if (minWeight < 0 || maxWeight < 0) {
      throw new IllegalArgumentException("min and max weight must be >= 0");
    }
    this.minWeight = minWeight;
    this.maxWeight = maxWeight;
    if (gender == "" || !(gender.equals("Male") || gender.equals("Female"))) {
      throw new IllegalArgumentException("gender must not be empty, must be either male of female");
    }
    this.gender = gender;
    if (race == "" || nationality == "") {
      throw new IllegalArgumentException("race and nationality must not be empty");
    }
    this.race = race;
    this.nationality = nationality;
  }
  
  /**
   * Creates a new criteria object.
   * @param db Database.
   * @param form Form information to parse.
   * @param parent Row number of the participant or trial owning this object.
   * @param table Database table.
   */
  public Criteria(SqliteDB db, JsonArray form, int parent, String table) {
    if (table.equals("participant_data")) {
      makeData(db, form, parent);
    } else if (table.equals("trial_criteria")) {
      makeCrit(db, form, parent);
    }
    
  }
  
  /**
   * Makes participant data.
   * @param db Database.
   * @param form Form information to parse.
   * @param parent Row number of the participant or trial owning this object.
   */
  private void makeData(SqliteDB db, JsonArray form, int parent) {
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
  
  /**
   * Makes trial criteria.
   * @param db Database.
   * @param form Form information to parse.
   * @param parent Row number of the participant or trial owning this object.
   */
  private void makeCrit(SqliteDB db, JsonArray form, int parent) {
    minAge = form.get(11).getAsJsonObject().get("value").getAsInt();
    maxAge = form.get(12).getAsJsonObject().get("value").getAsInt();
    minHeight = form.get(17).getAsJsonObject().get("value").getAsInt();
    maxHeight = form.get(21).getAsJsonObject().get("value").getAsInt();
    minWeight = form.get(24).getAsJsonObject().get("value").getAsInt();
    maxWeight = form.get(27).getAsJsonObject().get("value").getAsInt();
    gender = form.get(10).getAsJsonObject().get("value").getAsString();
    race = form.get(28).getAsJsonObject().get("value").getAsString();
    nationality = form.get(29).getAsJsonObject().get("value").getAsString();
    extID = parent;
    id = db.insertCriteria("trial_criteria", this);
    System.out.println(id);
  }
  
  /**
   * Update data after edits to information.
   * @param db Database.
   * @param form Form information to parse.
   * @return Whether the operation was successful.
   */
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
  
  /**
   * Update criteria after edits to information.
   * @param db Database.
   * @param form Form information to parse.
   * @return Whether the operation was successful.
   */
  public boolean updateCrit(SqliteDB db, JsonArray form) {
    minAge = form.get(11).getAsJsonObject().get("value").getAsInt();
    maxAge = form.get(12).getAsJsonObject().get("value").getAsInt();
    minHeight = form.get(17).getAsJsonObject().get("value").getAsInt();
    maxHeight = form.get(21).getAsJsonObject().get("value").getAsInt();
    minWeight = form.get(24).getAsJsonObject().get("value").getAsInt();
    maxWeight = form.get(27).getAsJsonObject().get("value").getAsInt();
    gender = form.get(10).getAsJsonObject().get("value").getAsString();
    race = form.get(28).getAsJsonObject().get("value").getAsString();
    nationality = form.get(29).getAsJsonObject().get("value").getAsString();
    if (db.updateCriteria("trial_criteria", this) == 0) {
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
    if (c.getMinAge() >= minAge && c.getMinAge() <= maxAge && c.getMinHeight() >= minHeight
        && c.getMinHeight() <= maxHeight && c.getMinWeight() >= minWeight && c.getMinWeight()
        <= maxWeight && c.getGender().equals(gender)
        && c.getRace().equals(race) && c.getNationality().equals(nationality)) {
      return true;
    } else {
      return false;
    }
  }
}