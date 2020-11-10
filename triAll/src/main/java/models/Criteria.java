package models;

public class Criteria {
	private int id;
	private int age;
	private int height;
	private int weight;
	private String gender;
	private String race;
	private String nationality;
	
	public Criteria(int id, int age, int height, int weight, String gender, String race, String nationality) {
		this.id = id;
		this.age = age;
		this.height = height;
		this.weight = weight;
		this.gender = gender;
		this.race = race;
		this.nationality = nationality;
	}
}