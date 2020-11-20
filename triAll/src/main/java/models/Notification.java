package models;

public class Notification {
	private String time;
	private User participant;
	private Trial trial;
	
	
	public String getPartEmail() {
		return participant.getEmail();
	}
	
	public String getResEmail() {
		return "";
	}
}