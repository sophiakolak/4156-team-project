package models;

public class Criteria {
	private int id;
	private int ext_id;
	private int age;
	private double height;
	private double weight;
	private String gender;
	private String race;
	private String nationality;
	
	public Criteria(int id, int ext_id, int age, double height, double weight, String gender, String race, String nationality) {
		this.id = id;
		this.ext_id = ext_id;
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
}