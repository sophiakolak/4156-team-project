package models;

import java.util.LinkedList;

public class User {
<<<<<<< HEAD
=======
	private int id;
	private float lat;
	private float lon;
	private String first;
	private String last;
	private String email;
	private boolean isResearcher;
	private Criteria data;
	private LinkedList<Trial> trials;
	
	public User(int id, float lat, float lon, String first, String last, String email, boolean isR) {
		this.id = id;
		this.lat = lat;
		this.lon = lon;
		this.first = first;
		this.last = last;
		this.email = email;
		isResearcher = isR;
	}
	
	public int getID() {
		return id;
	}
	
	public void setData(Criteria data) {
		if(!isResearcher) {
		  this.data = data;
		}
	}
	
	public void addTrial(Trial t) {
		if(isResearcher) {
		  trials.add(t);
		}
	}
>>>>>>> 49cc80d95b71a5b61c9e06bb17a9234d4399ae2a
}