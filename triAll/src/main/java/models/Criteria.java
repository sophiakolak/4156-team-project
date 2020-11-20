package models;

public class Criteria {
	private int id;
	private int extID;
	private int age;
	private double height;
	private double weight;
	private String gender;
	private String race;
	private String nationality;
	
	  /**
	   * Creates criteria object.
	   */
	public Criteria(int id, int extID, int age, double height, 
			double weight, String gender, String race, String nationality) {
		this.id = id;
		this.extID = extID;
		this.age = age;
		this.height = height;
		this.weight = weight;
		this.gender = gender;
		this.race = race;
		this.nationality = nationality;
	}
	
	public int getID() {
		return id;
	}
	
	public int getAge() {
		return age;
	}
	
	public double getHeight() {
		return height;
	}
	
	public double getWeight() {
		return weight;
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
		if (c.getAge() == age && c.getHeight() == height && c.getWeight() == weight 
				&& c.getGender().equals(gender) && c.getRace().equals(race) 
				&& c.getNationality().equals(nationality)) {
			return true;
		} else {
			return false;
		}
	}
}