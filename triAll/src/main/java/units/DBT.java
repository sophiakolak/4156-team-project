package units;

import java.sql.ResultSet;
import java.sql.SQLException;

class DBT {
	
	public static void main(String args[]) {
		SqliteDB db = new SqliteDB("triall2");
		
		boolean res_created = db.create("researchers", 0);
		boolean part_created = db.create("participants", 1);
		boolean trial_created = db.create("trials", 2);
		boolean trial_crit_created = db.create("trial_criteria", 3);
		boolean part_data_created = db.create("participant_data", 4);
		boolean trial_match_created = db.create("trial_matches", 5);
		boolean email_created = db.create("email", 6);
		
		if (!res_created) {
			System.out.print("researcher not created");
		}
		
		if (!part_created) {
			System.out.print("part not created");
		}
		
		if (!trial_created) {
			System.out.print("part not created");
		}
		
		if (!trial_crit_created) {
			System.out.print("trial criteria not created");
		}
		
		if (!part_data_created) {
			System.out.print("part data not created");
		}
		
		if (!trial_match_created) {
			System.out.print("trial match not created");
		}
		
		if (!email_created) {
			System.out.print("email not created");
		}
		
		int id = db.insertUser("participants", 21.0, 22.3, "Julian", "Goldberg", "jg3796@columbia.edu");
		if(id==0) {
			System.exit(0);
		} else {
			int id2 = db.insertCriteria("participant_data", id, 22, 67, 120, "Male", "White", "American");
			if(id2==0) {
				System.exit(0);
			}
		}
	  try {
		ResultSet rs = db.fetchString("participants", "email", "jg3796@columbia.edu");
		if(!rs.next()) {
			System.exit(0);
		} 
		ResultSet data = db.fetchInt("participant_data", "participant_ID", rs.getInt(1));
		if(!data.next()) {
			System.exit(0);
		}
		System.out.println("Success");
	  } catch(SQLException e) {
		  
	  }
		
		
	}
}