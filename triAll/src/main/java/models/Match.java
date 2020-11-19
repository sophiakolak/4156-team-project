package models;

public class Match {

  private int id;
  private Trial trial;
  //private User researcher;
  //private User participant;
  private double distance;
  private String status;

  public Match(int id, Trial t, double d, String s) {
    this.id = id;
    trial = t;
    distance = d;
    status = s;
	}

  public double getDistance() {
    return distance;
  }
}
