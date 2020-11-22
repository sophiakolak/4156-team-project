package models;

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
	public Criteria(int id, int extID, int minAge, int maxAge, double minHeight, double maxHeight,
			double minWeight, double maxWeight, String gender, String race, String nationality) {
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
	
	public int getID() {
		return id;
	}
	
	public int getAge() {
		return minAge;
	}
	
	public double getHeight() {
		return minHeight;
	}
	
	public double getWeight() {
		return minWeight;
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
    if (c.getAge() >= minAge && c.getAge() <= maxAge && c.getHeight() >= minHeight 
        && c.getHeight() <= maxHeight && c.getWeight() >= minWeight && c.getWeight()
        <= maxWeight && c.getGender().equals(gender) && c.getRace().equals(race) 
        && c.getNationality().equals(nationality)) {
      return true;
    } else {
      return false;
    }
  }
}