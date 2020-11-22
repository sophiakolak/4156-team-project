package models;

public class Trial {
	
	private int id;
	//private User researcher;
	private String desc;
	private double lat;
	private double lon;
	private String location;
	private String start;
	private String end;
	private double pay;
	private int irb;
	private int partNeeded;
	private int partConfirmed;
	
	private Criteria crit;
	
	  /**
	   * Creates trial object.
	   */
	public Trial(int id, User r, String d, double lat, double lon, String location, String s, 
			String e, double p, int IRB, int pn, int pc, Criteria crit) {

		this.id = id;
		//this.researcher = r;
		this.desc = d;
		this.lat = lat;
		this.lon = lon;
		this.location = location;
		this.start = s;
		this.end = e;
		this.pay = p;
		this.irb = IRB;
		this.partNeeded = pn;
		this.partConfirmed = pc;
		this.crit = crit;		
	}
	
	public int getID() {
		return id;
	}
	
	public Criteria getCriteria() {
		return crit;
	}
	
	public double getLat() {
		return this.lat;
	}
	
	public double getLong() {
		return this.lon;
	}
	
	public String getStart() {
		return start;
	}
	
	public int getPartConf() {
		return partConfirmed;
	}
	
	public void confirmOne() {
		partConfirmed++;
	}
	
	
}
