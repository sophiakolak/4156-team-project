package models;

public class Match {

  private int id;
  private Trial trial;
  //private User researcher;
  //private User participant;
  private double distance;
  private String status;

  /**
   * Creates a new Trial-Participant match object with specified properties.
   * @param id The row in database of the match when stored
   * @param t The respective trial object of this match
   * @param d The distance between trial and participant locations
   * @param s The status of the match (e.g. accepted, rejected)
   */
  public Match(int id, Trial t, double d, String s) {
    this.id = id;
    trial = t;
    distance = d;
    status = s;
  }

  public double getDistance() {
    return distance;
  }
  
  public int getID() {
	return this.id;
  }
  
  public Trial getTrial() {
	  return trial;
  }
  
}
