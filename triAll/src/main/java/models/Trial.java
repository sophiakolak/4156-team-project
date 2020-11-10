package models;

public class Trial {
	
	private int id;
	private User researcher;
	private Criteria crit;
	
	public Trial(int id, User r, Criteria crit) {
		this.id = id;
		researcher = r;
		this.crit = crit;
	}
}