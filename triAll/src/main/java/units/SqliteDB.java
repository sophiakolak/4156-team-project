package units;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class SqliteDB {
	private Connection conn;
	private Statement stmt;
	public final int RESEARCHER = 0;
	public final int PARTICIPANT  = 1;
	public final int TRIAL = 2;
	public final int TRIAL_CRITERIA = 3;
	public final int PARTICIPANT_DATA = 4;
	public final int TRIAL_MATCH = 5;
	public final int EMAIL = 6;
	
	/**
	 * Initializes connection to database.
	 * @param name The name of the database.
	 */
	public SqliteDB(String name) {
	  conn = null;
	  stmt = null;
	  try {
	    Class.forName("org.sqlite.JDBC");
	    conn = DriverManager.getConnection("jdbc:sqlite:" + name + ".db");
        stmt = conn.createStatement();
      } catch (Exception e) {
	    System.err.println(e.getClass().getName() + ": " + e.getMessage());
	    System.exit(0);
      }
    }
	
	/**
	 * Drops table from the database is it exists.
	 * @param table Name of the table.
	 * @return Whether the operation was successful.
	 */
    public boolean drop(String table) {
	  try {
	    String drop = "DROP TABLE IF EXISTS " + table + ";";
	    stmt.executeUpdate(drop);
	  } catch (Exception e) {
	    return false;
	  }
	  return true;
	}
    
    /**
     * Checks whether a table is already present in the database.
     * @param table Name of the table.
     * @return Whether table is in the database.
     */
    public boolean isTable(String table) {
      try {
        String query = "SELECT COUNT(name) AS total FROM sqlite_master"
                     + " WHERE type='table' AND name='" + table + "';";
        ResultSet rs = stmt.executeQuery(query);
        rs.next();
        if (rs.getInt("total") == 1) {
          rs.close();
          return true;
        }
      } catch (Exception e) {
        return false;
      }
      return false;
    }
    
    /**
     * Fetches all entries in the table.
     * @param table Name of the table.
     * @return Set of all entries in the table - null upon failure.
     */
    public ResultSet fetchAll(String table) {
      ResultSet rs = null;
      try {
        rs = stmt.executeQuery("SELECT * FROM " + table + ";");
      } catch (Exception e) {
        return rs;
      }
      return rs;
    }
    
    public ResultSet fetchInt(String table, String field, int ID) {
        ResultSet rs = null;
        try {
          rs = stmt.executeQuery("SELECT * FROM " + table + " WHERE "+ field +" = "+ ID +";");
        } catch (Exception e) {
          return rs;
        }
        return rs;
      }
    
    public ResultSet fetchString(String table, String field, String ID) {
    	ResultSet rs = null;
        try {
          rs = stmt.executeQuery("SELECT * FROM " + table + " WHERE "+ field +" = '"+ ID +"';");
        } catch (Exception e) {
          return rs;
        }
        return rs;
    }
    
    /**
     * Creates table in the database if it isn't already there.
     * @param table Name of the table.
     * @param type Which table schema is being used.
     * @return Whether the operation was successful.
     */
    public boolean create(String table, int type) {
      try {
    	String create;
    	switch(type) {
    	  case RESEARCHER:
    		  create = "CREATE TABLE IF NOT EXISTS " + table + " (ID INT PRIMARY KEY AUTOINCREMENT, Lat REAL,"
    		  				+ " Long REAL, First TEXT, Last TEXT, Email TEXT);";
    		  break;
    	  case PARTICIPANT:
    		  create = "CREATE TABLE IF NOT EXISTS " + table + " (ID INT PRIMARY KEY AUTOINCREMENT, Lat REAL,"
    		  				+ " Long REAL, First TEXT, Last TEXT, Email TEXT);";
    		  break;
    	  case TRIAL:
    		  create = "CREATE TABLE IF NOT EXISTS " + table + " (ID INT PRIMARY KEY AUTOINCREMENT, researcher_ID INT NOT NULL,"
    		  				+ " description TEXT, lat REAL, long REAL, time TEXT, IRB INT,"
    		  				+ " participants_needed INT, participants_confirmed INT);";
    		  break;
    	  case TRIAL_CRITERIA:
    		  create = "CREATE TABLE IF NOT EXISTS " + table + " (ID INT PRIMARY KEY AUTOINCREMENT, trial_ID INT NOT NULL,"
    		  				+ " Age INT, Height_in_inches REAL, Weight_in_lbs REAL, Gender TEXT, Race TEXT,"
    		  				+ " Nationality TEXT);";
    		  break;
    	  case PARTICIPANT_DATA:
    		  create = "CREATE TABLE IF NOT EXISTS " + table + " (ID INT PRIMARY KEY AUTOINCREMENT, participant_ID INT NOT NULL,"
    		  				+ " Age INT, Height_in_inches REAL, Weight_in_lbs REAL, Gender TEXT, Race TEXT,"
    		  				+ " Nationality TEXT);";
    		  break;
    	  case TRIAL_MATCH:
    		  create = "CREATE TABLE IF NOT EXISTS " + table + " (ID INT PRIMARY KEY AUTOINCREMENT, trial_ID INT NOT NULL,"
    		  				+ " researcher_ID INT NOT NULL, participant_ID INT NOT NULL, status TEXT);";
    		  break;
    	  case EMAIL:
    		  create = "CREATE TABLE IF NOT EXISTS " + table + " (ID INT PRIMARY KEY AUTOINCREMENT, trial_ID INT,"
    		  				+ " researcher_ID INT, participant_ID INT, type TEXT, time_sent TEXT, delivery_success INT);";
    		  break;
    	  default:
    		  return false;
    	}
        stmt.executeUpdate(create);
      } catch (Exception e) {
        return false;
      }
      return true;
    }
    
    public int insertUser(String table, double lat, double lon, String first, String last, String email) {
    	int id = 0;
    	try {
    	  String add = "INSERT INTO " + table + " (Lat, Long, First, Last, Email) VALUES ("
    					+ lat + ", " + lon + ", '" + first + "', '" + last + "', '" + email + "');";
    	  stmt.executeUpdate(add);
    	  String check = "SELECT last_insert_rowid() AS num;";
    	  ResultSet rs = stmt.executeQuery(check);
    	  rs.next();
    	  id = rs.getInt("num");
    	} catch (Exception e) {
    		return 0;
    	}
    	return id;
    	
    }
    
    public int updateUser(String table, int ID, double lat, double lon, String first, String last, String email) {
    	int id = 0;
    	try {
    	  String add = "INSERT INTO " + table + " VALUES (" + ID + ", "
    					+ lat + ", " + lon + ", '" + first + "', '" + last + "', '" + email + "');";
    	  stmt.executeUpdate(add);
    	  String check = "SELECT last_insert_rowid() AS num;";
    	  ResultSet rs = stmt.executeQuery(check);
    	  rs.next();
    	  id = rs.getInt("num");
    	} catch (Exception e) {
    		return 0;
    	}
    	return id;
    	
    }
    
    public int insertTrial(String table, int res_id, String desc, double lat, double lon, String time, int IRB, int needed, int confirmed) {
    	int id = 0;
    	try {
    	  String add = "INSERT INTO " + table + " VALUES (null, " + res_id +", " + lat + ", " + lon
    			      + ", '" + time + "', " + IRB + ", " + needed + ", " + confirmed + ");";
    	  stmt.executeUpdate(add);
    	  String check = "SELECT last_insert_rowid() AS num;";
    	  ResultSet rs = stmt.executeQuery(check);
    	  rs.next();
    	  id = rs.getInt("num");
    	} catch (Exception e) {
    		return 0;
    	}
    	return id;
    }
    
    public int updateTrial(String table, int ID, int res_id, String desc, double lat, double lon, String time, int IRB, int needed, int confirmed) {
    	int id = 0;
    	try {
    	  String add = "REPLACE INTO " + table + " VALUES (" + ID + ", " + res_id +", " + lat + ", " + lon
    			      + ", '" + time + "', " + IRB + ", " + needed + ", " + confirmed + ");";
    	  stmt.executeUpdate(add);
    	  String check = "SELECT last_insert_rowid() AS num;";
    	  ResultSet rs = stmt.executeQuery(check);
    	  rs.next();
    	  id = rs.getInt("num");
    	} catch (Exception e) {
    		return 0;
    	}
    	return id;
    }
    
    public int insertCriteria(String table, int parent, int age, int height, int weight, String gender, String race, String nationality) {
    	int id = 0;
    	try {
    	  String add = "INSERT INTO " + table + " VALUES (null, " + parent + ", " + age +", " + height + ", " + weight
    			       + ", '" + gender + "', '" + race + "', '" + nationality + "');";
    	  stmt.executeUpdate(add);
    	  String check = "SELECT last_insert_rowid() AS num;";
    	  ResultSet rs = stmt.executeQuery(check);
    	  rs.next();
    	  id = rs.getInt("num");
    	} catch (Exception e) {
    		return 0;
    	}
    	return id;
    }
    
    public int updateCriteria(String table, int ID, int parent, int age, int height, int weight, String gender, String race, String nationality) {
    	int id = 0;
    	try {
    	  String add = "REPLACE INTO " + table + " VALUES (" + ID + ", " + parent + ", " + age +", " + height + ", " + weight
    			       + ", '" + gender + "', '" + race + "', '" + nationality + "');";
    	  stmt.executeUpdate(add);
    	  String check = "SELECT last_insert_rowid() AS num;";
    	  ResultSet rs = stmt.executeQuery(check);
    	  rs.next();
    	  id = rs.getInt("num");
    	} catch (Exception e) {
    		return 0;
    	}
    	return id;
    }
    
    /**
     * Closes connection to the database.
     */
    public void stop() {
      try {
        stmt.close();
        conn.close();
      } catch (Exception e) {
        System.err.println(e.getClass().getName() + ": " + e.getMessage());
      }
    }
    
    /**
     * gets the Connection object.
     * @return Class Connection member.
     */
    public Connection getConn() {
      return conn;
    }
    
    
}