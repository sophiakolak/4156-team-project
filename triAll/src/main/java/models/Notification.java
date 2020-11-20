package models;

public class Notification {
	private String time;
	private User participant;
	private Trial trial;
	
	public Notification(String time, User p, Trial t){
		this.time = time;
		this.participant = p;
		this.trial = t;
	}
	
	public String getPartEmail() {
		return participant.getEmail();
	}
	
	public String getResEmail() {
		return "";
	}
}