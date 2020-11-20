package models;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;

public class User {
  private int id;
  private double lat;
  private double lon;
  private String first;
  private String last;
  private String email;
  private boolean isResearcher;
  private Criteria data;
  private boolean loggedIn;
  private HashMap<Integer, Trial> trials;
  private LinkedList<Match> matches;

  private final Comparator<Trial> DATE_COMPARE = new Comparator<Trial>() {
    @Override
    public int compare(Trial t1, Trial t2) {
      return 0;
    }
  };

  private final Comparator<Match> DIST_COMPARE = new Comparator<Match>() {
    @Override
    public int compare(Match m1, Match m2) {
      if (m1.getDistance() < m2.getDistance()) {
        return -1;
      } else if (m1.getDistance() > m2.getDistance()) {
        return 1;
      } else {
        return 0;
      }
    }
  };

  public User() {
    loggedIn = false;
  }

  public User(int id, double lat, double lon, String first, String last, String email, boolean isR) {
    this.id = id;
    this.lat = lat;
    this.lon = lon;
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

  public void update(double lat, double lon, String first, String last, String email) {
    this.lat = lat;
    this.lon = lon;
    this.first = first;
    this.last = last;
    this.email = email;
  }

  public int getID() {
    return id;
  }

  public void setData(Criteria data) {
    if (!isResearcher) {
      this.data = data;
    }
  }

  public void addTrial(int id, Trial t) {
    if (isResearcher) {
      trials.put(id, t);
    }
  }

  public Trial getTrial(int trial_ID) {
    if (isResearcher) {
      return trials.get(trial_ID);
    } else {
      return null;
    }
  }

  public boolean containsTrial(int trial_ID) {
    if (isResearcher && trials.containsKey(trial_ID)) {
      return true;
    }
    return false;
  }

  public Criteria getData() {
    return data;
  }

  public void addMatch(int id, Trial t) {
    //calculate distance to this User
	
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

  public LinkedList<Trial> sortedTrials(){
    if (!isResearcher) {
      return null;
	}
    LinkedList<Trial> list = new LinkedList<>(trials.values());
    list.sort(DATE_COMPARE);
    return list;
  }

  public LinkedList<Match> sortedMatches(){
    if (isResearcher) {
      return null;
	}
    matches.sort(DIST_COMPARE);
    return matches;
  }
}
