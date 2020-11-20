package models;

public class Trial {
	
	private int id;
	//private User researcher;
	private String desc;
	private double lat;
	private double lon;
	private String time;
	private int IRB;
	private int part_needed;
	private int part_confirmed;
	
	private Criteria crit;
	
	public Trial(int id, User r, String d, double lat, double lon, String t, int IRB, int pn, int pc, Criteria crit) {
		this.id = id;
		//this.researcher = r;
		this.desc = d;
		this.lat = lat;
		this.lon = lon;
		this.time = t;
		this.IRB = IRB;
		this.part_needed = pn;
		this.part_confirmed = pc;
		this.crit = crit;		
	}
	
	public int getID() {
		return id;
	}
	
	public Criteria getCriteria() {
		return crit;
	}
	
	
}