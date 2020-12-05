package models;

import com.google.gson.JsonArray;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import units.SqliteDB;

public class User {
  private int id;
  private double lat;
  private double lon;
  private String location;
  private String first;
  private String last;
  private String email;
  private boolean isResearcher;
  private Criteria data;
  private boolean loggedIn;
  private HashMap<Integer, Trial> trials;
  private LinkedList<Match> matches;

  private final Comparator<Trial> dateCompare = new Comparator<Trial>() {
    @Override
    public int compare(Trial t1, Trial t2) {
      try {
        Date d1 = new SimpleDateFormat("yyyy-MM-dd").parse(t1.getStart());
        Date d2 = new SimpleDateFormat("yyyy-MM-dd").parse(t2.getStart());
        return d1.compareTo(d2);
      } catch (ParseException e) {
        return 0;
      }
    }
  };

  private final Comparator<Match> distCompare = new Comparator<Match>() {
    @Override
    public int compare(Match m1, Match m2) {
      return Double.compare(m1.getDistance(), m2.getDistance());
    }
  };

  public User() {
    loggedIn = false;
  }
  

  /**
   * Creating user.
   */
  public User(int id, double lat, double lon, String location, String first, 
      String last, String email, boolean isR) {
    this.id = id;
    this.lat = lat;
    this.lon = lon;
    this.location = location;
    this.first = first;
    this.last = last;
    this.email = email;
    isResearcher = isR;
    loggedIn = true;
    if (isR) {
      trials = new HashMap<>();
    } else {
      matches = new LinkedList<>();
    }
  }
  
  public void restart(User u) {
    this.id = u.id;
    this.lat = u.lat;
    this.lon = u.lon;
    this.location = u.location;
    this.first = u.first;
    this.last = u.last;
    this.email = u.email;
    isResearcher = u.isResearcher;
    loggedIn = true;
    if (isResearcher) {
      trials = new HashMap<>();
    } else {
      matches = new LinkedList<>();
    }
  }

  /**
   * Update info.
   */
  public void update(double lat, double lon, String first, String last, String email) {
    this.lat = lat;
    this.lon = lon;
    this.first = first;
    this.last = last;
    this.email = email;
  }
  
  public boolean updatePart(SqliteDB db, JsonArray form) {
    if (!loggedIn || isResearcher) {
      return false;
    }
    lat = form.get(4).getAsJsonObject().get("value").getAsDouble();
    lon = form.get(5).getAsJsonObject().get("value").getAsDouble();
    location = form.get(3).getAsJsonObject().get("value").getAsString();
    first = form.get(0).getAsJsonObject().get("value").getAsString();
    last = form.get(1).getAsJsonObject().get("value").getAsString();
    email = form.get(2).getAsJsonObject().get("value").getAsString();
    if (db.updateUser("participants", this) == 0) {
      return false;
    }
    data.updateData(db, form);
    return true;
  }
  
  public boolean updateRes(SqliteDB db, JsonArray form) {
    if(!loggedIn || !isResearcher) {
      return false;
    }
    lat = form.get(5).getAsJsonObject().get("value").getAsDouble();
    lon = form.get(6).getAsJsonObject().get("value").getAsDouble();
    location = form.get(4).getAsJsonObject().get("value").getAsString();
    first = form.get(1).getAsJsonObject().get("value").getAsString();
    last = form.get(2).getAsJsonObject().get("value").getAsString();
    email = form.get(3).getAsJsonObject().get("value").getAsString();
    if(db.updateUser("researchers", this) == 0) {
      return false;
    }
    return true;
  }

  public int getID() {
    return id;
  }

  /**
   * Set data.
   */
  public void setData(Criteria data) {
    if (!isResearcher) {
      this.data = data;
    }
  }

  /**
   * Add trial.
   */
  public void addTrial(int id, Trial t) {
    if (loggedIn && isResearcher) {
      trials.put(id, t);
    }
  }

  /**
   * Get trial.
   */
  public Trial getTrial(int trialID) {
    if (loggedIn && isResearcher) {
      return trials.get(trialID);
    } else {
      return null;
    }
  }
  
  public boolean updateTrial(int trialID, SqliteDB db, JsonArray form) {
    if(!loggedIn || !isResearcher) {
      return false;
    }
    return trials.get(trialID).update(db, form);
  }

  /**
   * Check if contains trial.
   */
  public boolean containsTrial(int trialID) {
    if (isResearcher && trials.containsKey(trialID)) {
      return true;
    }
    return false;
  }

  public Criteria getData() {
    return data;
  }
  
  public double getLat() {
    return lat;
  }
  
  public double getLon() {
    return lon;
  }
  
  public String getLocation() {
    return location;
  }
  
  public String getFirst() {
    return first;
  }
  
  public String getLast() {
    return last;
  }
  
  public int logIn(SqliteDB db, String email) {
    int type = checkType(db, email);
    switch (type) {
      case 1:
        retrievePart(db, email);
        break;
      case 0:
        retrieveRes(db, email);
        break;
      case -1:
        break;
      default:
        break;
    }
    return type;
  }
  
  /**
   * Determine what kind of user account exists for this email, if any.
   * @param db Database containing the user tables.
   * @param email Email address to check.
   * @return Whether or not the user is a participant; or -1 if no user exists.
   */
  public int checkType(SqliteDB db, String email) {
    if (db.inTable("participants", "email", email)) {
      return 1;
    } else if (db.inTable("researchers", "email", email)) {
      return 0;
    } else {
      return -1;
    }
  }
  
  private void retrieveRes(SqliteDB db, String email) {
    restart(db.loadRes(email));
    for (int trial : db.trialSet(id)) {
      Trial t = db.loadTrial(trial);
      addTrial(t.getID(), t);
    }
    
  }
  
  private void retrievePart(SqliteDB db, String email) {
    restart(db.loadPart(email));
    setData(db.loadData(id));
    for (int match : db.matchSet(id)) {
      addMatch(db.loadMatch(match));
    }
  }

  /**
   * Add match.
   */
  public void addMatch(int id, Trial t) {
    //calculate distance to this User
    double distance = distance(t.getLat(), t.getLong(), this.lat, this.lon, "M");
    Match m = new Match(id, t, distance, "pending");
    this.matches.add(m);
  }
  
  public void addMatch(Match m) {
    Trial t = m.getTrial();
    double distance = distance(t.getLat(), t.getLong(), this.lat, this.lon, "M");
    m.setDistance(distance);
    this.matches.add(m);
  }

  public boolean acceptMatch(int id, SqliteDB db) {
    for (Match m : matches) {
      if (m.getTrial().getID() == id) {
        return m.accept(db);
      }
    }
    return false;
  }

  public boolean rejectMatch(int id, SqliteDB db) {
    for (Match m : matches) {
      if (m.getTrial().getID() == id) {
        m.reject(db);
        return true;
      }
    }
    return false;
  }
  
  public boolean signUpPart(SqliteDB db, JsonArray form) {
    lat = form.get(5).getAsJsonObject().get("value").getAsDouble();
    lon = form.get(6).getAsJsonObject().get("value").getAsDouble();
    location = form.get(4).getAsJsonObject().get("value").getAsString();
    first = form.get(1).getAsJsonObject().get("value").getAsString();
    last = form.get(2).getAsJsonObject().get("value").getAsString();
    email = form.get(3).getAsJsonObject().get("value").getAsString();
    isResearcher = false;
    id = db.insertUser("participants", this);
    if (id == 0) {
      return false;
    }
    matches = new LinkedList<Match>();
    loggedIn = true;
    setData(new Criteria(db, form, id, "participant_data"));
    return true;
  }
  
  public boolean signUpRes(SqliteDB db, JsonArray form) {
    lat = form.get(5).getAsJsonObject().get("value").getAsDouble();
    lon = form.get(6).getAsJsonObject().get("value").getAsDouble();
    location = form.get(4).getAsJsonObject().get("value").getAsString();
    first = form.get(1).getAsJsonObject().get("value").getAsString();
    last = form.get(2).getAsJsonObject().get("value").getAsString();
    email = form.get(3).getAsJsonObject().get("value").getAsString();
    isResearcher = true;
    id = db.insertUser("participants", this);
    if (id == 0) {
      return false;
    }
    trials = new HashMap<Integer, Trial>();
    loggedIn = true;
    return true;
  }
  
  private void checkMatches(SqliteDB db) {
    for (int t : db.openTrials()) {
      Trial trial = db.loadTrial(t);
      if (trial.getCriteria().matches(data) && !db.matchExists(id, trial.getID())) {
        Match m = new Match(this, trial, db);
        addMatch(m);
        //send emails
      }
    }
  }

  public boolean isResearcher() {
    return isResearcher;
  }

  public void logOut() {
    loggedIn = false;
  }

  public boolean isLoggedIn() {
    return loggedIn;
  }

  public String getEmail() {
    return email;
  }

  /**
   * Sort trials.
   */
  public LinkedList<Trial> sortedTrials() {
    if (!isResearcher || trials == null) {
      return null;
    }
    LinkedList<Trial> list = new LinkedList<>(trials.values());
    list.sort(dateCompare);
    return list;
  }

  /**
   * Sort matches.
   */
  public LinkedList<Match> sortedMatches() {
    if (isResearcher) {
      return null;
    }
    matches.sort(distCompare);
    return matches;
  }

  /**
   * Calculate distance.
   */
  public static double distance(double lat1, double lon1, 
      double lat2, double lon2, String unit) {
    if ((lat1 == lat2) && (lon1 == lon2)) {
      return 0;
    } else {
      double theta = lon1 - lon2;
      double dist = Math.sin(Math.toRadians(lat1)) 
          * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) 
          * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
      dist = Math.acos(dist);
      dist = Math.toDegrees(dist);
      dist = dist * 60 * 1.1515;
      if (unit.equals("K")) {
        dist = dist * 1.609344;
      } else if (unit.equals("N")) {
        dist = dist * 0.8684;
      }
      return (dist);
    }
  }

}
