package models;

import java.util.HashMap;

public class User {
	private int id;
	private double lat;
	private double lon;
	private String first;
	private String last;
	private String email;
	private boolean isResearcher;
	private Criteria data;
	private boolean loggedIn = false;
	private HashMap<Integer, Trial> trials;
	
	public User(int id, double lat, double lon, String first, String last, String email, boolean isR) {
		this.id = id;
		this.lat = lat;
		this.lon = lon;
		this.first = first;
		this.last = last;
		this.email = email;
		isResearcher = isR;
		loggedIn = true;
		trials = new HashMap<>();
	}
	
	public int getID() {
		return id;
	}
	
	public void setData(Criteria data) {
		if(!isResearcher) {
		  this.data = data;
		}
	}
	
	public void addTrial(int id, Trial t) {
		if(isResearcher) {
		  trials.put(id, t);
		}
	}
	
	public Trial getTrial(int trial_ID) {
		return trials.get(trial_ID);
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
}