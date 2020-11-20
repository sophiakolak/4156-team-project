package units;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SqliteDB {
  private Connection conn;
  private Statement stmt;
  public static final int researcher = 0;
  public static final int participant = 1;
  public static final int trial = 2;
  public static final int trialCriteria = 3;
  public static final int participantData = 4;
  public static final int trialMatch = 5;
  public static final int email = 6;

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
    }
  }

  /**
    * Drops table from the database is it exists.
    * @param table Name of the table.
    * @return Whether the operation was successful.
    */
  public boolean drop(String table) {
	PreparedStatement st = null;
    try {
      st = conn.prepareStatement("DROP TABLE IF EXISTS ? ;");
      st.setString(1, table);
      String drop = "DROP TABLE IF EXISTS " + table + ";";
      stmt.executeUpdate(drop);
    } catch (Exception e) {
    	if (st != null) {
            try {
              st.close();
            } catch (SQLException e1) {
              e1.printStackTrace();
            }
          }
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
	PreparedStatement st = null;
    try {
      st = conn.prepareStatement("SELECT COUNT(name) AS total FROM sqlite_master WHERE type='table' AND name='?';");
      st.setString(1, table);
      ResultSet rs = st.executeQuery();
      rs.next();
      if (rs.getInt("total") == 1) {
        rs.close();
        return true;
      }
    } catch (Exception e) {
    	if (st != null) {
            try {
              st.close();
            } catch (SQLException e1) {
              e1.printStackTrace();
            }
          }
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
    PreparedStatement st = null;
    try {
      st = conn.prepareStatement("SELECT * FROM ?;");
      st.setString(1,  table);
      rs = st.executeQuery("SELECT * FROM " + table + ";");
    } catch (Exception e) {
      if (st != null) {
        try {
          st.close();
        } catch (SQLException e1) {
          e1.printStackTrace();
        }
      }
      return rs;
    }
    return rs;
  }
  
  /**
   * Fetches entries when an integer condition is met.
   * @param table Name of the table.
   * @param field Field to check for the condition.
   * @param id Integer value compared to the field.
   * @return Set of all entries meeting the condition - null upon failure.
   */
  public ResultSet fetchInt(String table, String field, int id) {
    ResultSet rs = null;
    PreparedStatement st = null;
    try {
      st = conn.prepareStatement("SELECT * FROM ? WHERE ? = ?;");
      st.setString(1,  table);
      st.setString(2, field);
      st.setInt(3, id);
      rs = st.executeQuery();
    } catch (Exception e) {
      if (st != null) {
        try {
          st.close();
        } catch (SQLException e1) {
          e1.printStackTrace();
        }
      }
      return rs;
    }
    return rs;
  }
  
  /**
   * Fetches entries when a string condition is met.
   * @param table Name of the table.
   * @param field Field to check for the condition.
   * @param id String to compare to the field.
   * @return Set of all entries meeting the condition - null upon failure.
   */
  public ResultSet fetchString(String table, String field, String id) {
    ResultSet rs = null;
    PreparedStatement st = null;
    try {
      st = conn.prepareStatement("SELECT * FROM ? WHERE ? = '?';");
      st.setString(1,  table);
      st.setString(2,  field);
      st.setString(3,  id);
      rs = st.executeQuery();
    } catch (Exception e) {
      if (st != null) {
        try {
          st.close();
        } catch (SQLException e1) {
          e1.printStackTrace();
        }
      }
      return rs;
    }
    return rs;
  }
  
  /**
   * Checks whether a particular entry exists meeting two conditions.
   * @param table Name of the table.
   * @param field1 First field to check.
   * @param id1 Value to compare to the first field.
   * @param field2 Second Field to check.
   * @param id2 Value to compare to the second field.
   * @return Whether or not such an entry is already present.
   */
  public boolean matchExists(String table, String field1, int id1, String field2, int id2) {
    ResultSet rs = null;
    PreparedStatement st = null;
    try {
      st = conn.prepareStatement("SELECT COUNT(ID) FROM ? WHERE ? = ? AND ? = ?;");
      st.setString(1, table);
      st.setString(2,  field1);
      st.setInt(3, id1);
      st.setString(4, field2);
      st.setInt(5,  id2);
      rs = st.executeQuery();
      rs.next();
      if (rs.getInt("total") == 1) {
        rs.close();
        return true;
      }
    } catch (Exception e) {
    	if (st != null) {
            try {
              st.close();
            } catch (SQLException e1) {
              e1.printStackTrace();
            }
          }
      return false;
    }
    return false;
  }
    
  /**
    * Creates table in the database if it isn't already there.
    * @param table Name of the table.
    * @param type Which table schema is being used.
    * @return Whether the operation was successful.
    */
  public boolean create(String table, int type) {
	PreparedStatement st = null;
    try {
      String create;
      switch (type) {
        case researcher:
          st = conn.prepareStatement("\"CREATE TABLE IF NOT EXISTS ? (ID INTEGER PRIMARY KEY AUTOINCREMENT,"
          		+ "Location TEXT, Lat REAL, Long REAL, First TEXT, Last TEXT, Email TEXT);");
          st.setString(1, table);
          st.executeUpdate();
          break;
        case participant:
          st = conn.prepareStatement("CREATE TABLE IF NOT EXISTS ? (ID INTEGER PRIMARY KEY AUTOINCREMENT,"
              + " Location TEXT, Lat REAL, Long REAL, First TEXT, Last TEXT, Email TEXT);");
          st.setString(1, table);
          st.executeUpdate();
          break;
        case trial:
          st = conn.prepareStatement("CREATE TABLE IF NOT EXISTS ? (ID INTEGER PRIMARY KEY"
              + "AUTOINCREMENT, researcher_ID INT NOT NULL, description TEXT, Location TEXT,"
              + " lat REAL, long REAL, location TEXT, start_date TEXT, end_date TEXT, pay REAL,"
              + " IRB INT, participants_needed INT, participants_confirmed INT);");
          st.setString(1, table);
          st.executeUpdate();
          break;
        case trialCriteria:
          st = conn.prepareStatement("CREATE TABLE IF NOT EXISTS ? (ID INTEGER PRIMARY KEY AUTOINCREMENT,"
              + " trial_ID INT NOT NULL, Age INT, Height_in_inches REAL, Weight_in_lbs REAL, Gender"
              + " TEXT, Race TEXT, Nationality TEXT);");
          st.setString(1, table);
          st.executeUpdate();
          break;
        case participantData:
        	st = conn.prepareStatement("CREATE TABLE IF NOT EXISTS ? (ID INTEGER PRIMARY KEY AUTOINCREMENT,"
              + " participant_ID INT NOT NULL, Age INT, Height_in_inches REAL, Weight_in_lbs REAL, "
              + "Gender TEXT, Race TEXT, Nationality TEXT);");
        	st.setString(1, table);
            st.executeUpdate();
          break;
        case trialMatch:
        	st = conn.prepareStatement("CREATE TABLE IF NOT EXISTS ? (ID INTEGER PRIMARY KEY AUTOINCREMENT,"
              + " trial_ID INT NOT NULL, researcher_ID INT NOT NULL, participant_ID INT NOT NULL, "
              + "status TEXT);");
        	st.setString(1, table);
            st.executeUpdate();
          break;
        case email:
        	st = conn.prepareStatement("CREATE TABLE IF NOT EXISTS ? (ID INTEGER PRIMARY KEY AUTOINCREMENT,"
              + " trial_ID INT, researcher_ID INT, participant_ID INT, type TEXT, time_sent TEXT,"
              + " delivery_success INT);");
        	st.setString(1, table);
            st.executeUpdate();
          break;
        default:
          return false;
      }
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      try {
        if (st != null) {
          st.close();
        }
      } catch(SQLException e1) {
    	  e1.printStackTrace();
      }
      return false;
    }
    return true;
  }
    
  /**
   * Inserts an entry into an User-style table.
   * @param table Name of the table.
   * @param lat Latitude coordinate.
   * @param lon Longitude coordinate.
   * @param first First name.
   * @param last Last name.
   * @param email Email address.
   * @return The row into which the new user was inserted - 0 upon failure.
   */
  public int insertUser(String table, double lat, double lon, String first, String last, 
      String email) {
	PreparedStatement st = null;
    int id = 0;
    try {
      st = conn.prepareStatement("INSERT INTO ? (Lat, Long, First, Last, Email) VALUES ("
    		  + "?, ?, '?', '?', '?');");
      st.setString(1, table);
      st.setDouble(2, lat);
      st.setDouble(3, lon);
      st.setString(4, first);
      st.setString(5, last);
      st.setString(6, email);
      st.executeUpdate();
      String check = "SELECT last_insert_rowid() AS num;";
      ResultSet rs = stmt.executeQuery(check);
      rs.next();
      id = rs.getInt("num");
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      try {
          if (st != null) {
            st.close();
          }
        } catch(SQLException e1) {
      	  e1.printStackTrace();
        }
      return 0;
    }
    return id;

  }
    
  /**
   * Updates entry in an User-style table.
   * @param table Name of the table.
   * @param id Row of the entry to be updated.
   * @param lat Latitude coordinate.
   * @param lon Longitude coordinate.
   * @param first First name.
   * @param last Last name.
   * @param email Email address.
   * @return The row which was updated - 0 upon failure.
   */
  public int updateUser(String table, int id, double lat, double lon, String first, String last, 
      String email) {
    int row = 0;
    PreparedStatement st = null;
    try {
    	st = conn.prepareStatement("REPLACE INTO ? VALUES ( ?"
      		  + "?, ?, '?', '?', '?');");
        st.setString(1, table);
        st.setInt(2, id);
        st.setDouble(3, lat);
        st.setDouble(4, lon);
        st.setString(5, first);
        st.setString(6, last);
        st.setString(7, email);
        st.executeUpdate();
      String check = "SELECT last_insert_rowid() AS num;";
      ResultSet rs = stmt.executeQuery(check);
      rs.next();
      row = rs.getInt("num");
    } catch (Exception e) {
    	try {
            if (st != null) {
              st.close();
            }
          } catch(SQLException e1) {
        	  e1.printStackTrace();
          }
      return 0;
    }
    return row;

  }
    
  /**
   * Inserts a new trial into the database.
   * @param table Name of the table
   * @param resId Row of researcher in their table.
   * @param desc Description of trial.
   * @param lat Latitude coordinate.
   * @param lon Longitude coordinate.
   * @param start Start date.
   * @param end End date.
   * @param pay Hourly payment in dollars.
   * @param irb Institutional Review Board number.
   * @param needed Participants needed for trial.
   * @param confirmed Participants confirmed so far.
   * @return The row into which the new trial was inserted - 0 upon failure.
   */
  public int insertTrial(String table, int resId, String desc, double lat, double lon,
      String location, String start, String end, double pay, int irb, int needed, int confirmed) {
    int id = 0;
    PreparedStatement st = null;
    try {
      st = conn.prepareStatement("INSERT INTO ? VALUES (null, ?, ' ? ', ?"
          + ", ?, '?', '?', '?', ?, ?, ?, ?);");
      st.setString(1, table);
      st.setInt(2, resId);
      st.setString(3, desc);
      st.setDouble(4, lat);
      st.setDouble(5, lon);
      st.setString(6, location);
      st.setString(7, start);
      st.setString(8, end);
      st.setDouble(9, pay);
      st.setInt(10, irb);
      st.setInt(11, needed);
      st.setInt(12, confirmed);
      st.executeUpdate();
      String check = "SELECT last_insert_rowid() AS num;";
      ResultSet rs = stmt.executeQuery(check);
      rs.next();
      id = rs.getInt("num");
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      try {
          if (st != null) {
            st.close();
          }
        } catch(SQLException e1) {
      	  e1.printStackTrace();
        }
      return 0;
    }
    return id;
  }
    
  /**
   * Updates a trial in the database.
   * @param table Name of the table
   * @param row Row of the trial to be updated.
   * @param resId Row of researcher in their table.
   * @param desc Description of trial.
   * @param lat Latitude coordinate.
   * @param lon Longitude coordinate.
   * @param start Start date.
   * @param end End date.
   * @param pay Hourly payment in dollars.
   * @param irb Institutional Review Board number.
   * @param needed Participants needed for trial.
   * @param confirmed Participants confirmed so far.
   * @return The row which was updated - 0 upon failure.
   */
  public int updateTrial(String table, int row, int resId, String desc, double lat, double lon,
      String location, String start, String end, double pay, int irb, int needed, int confirmed) {
    int id = 0;
    PreparedStatement st = null;
    try {
      st = conn.prepareStatement("REPLACE INTO ? VALUES (?, ?, ' ? ', ?"
             + ", ?, '?', '?', '?', ?, ?, ?, ?);");
      st.setString(1, table);
      st.setInt(2, row);
      st.setInt(3, resId);
      st.setString(4, desc);
      st.setDouble(5, lat);
      st.setDouble(6, lon);
      st.setString(7, location);
      st.setString(8, start);
      st.setString(9, end);
      st.setDouble(10, pay);
      st.setInt(11, irb);
      st.setInt(12, needed);
      st.setInt(13, confirmed);
      st.executeUpdate();
      String check = "SELECT last_insert_rowid() AS num;";
      ResultSet rs = stmt.executeQuery(check);
      rs.next();
      id = rs.getInt("num");
    } catch (Exception e) {
    	try {
            if (st != null) {
              st.close();
            }
          } catch(SQLException e1) {
        	  e1.printStackTrace();
          }
      return 0;
    }
    return id;
  }
    
  /**
   * Inserts a new entry into a Criteria-style table.
   * @param table Name of the table.
   * @param parent Row of parent object in its table.
   * @param age Age to be matched.
   * @param height Height to be matched.
   * @param weight Weight to be matched.
   * @param gender Gender to be matched.
   * @param race Ethnicity to be matched.
   * @param nationality Nationality to be matched.
   * @return The row into which the new criteria was inserted - 0 upon failure.
   */
  public int insertCriteria(String table, int parent, int age, int height, int weight, 
      String gender, String race, String nationality) {
    int id = 0;
    PreparedStatement st  = null;
    try {
      st = conn.prepareStatement("INSERT INTO ? VALUES (null, ?, ?, ?, ?, ?, '?', '?', '?';");
      st.setString(1, table);
      st.setInt(2, parent);
      st.setInt(3,  age);
      st.setInt(4, height);
      st.setInt(5, weight);
      st.setString(6, gender);
      st.setString(7, race);
      st.setString(8, nationality);
      st.executeUpdate();
      String check = "SELECT last_insert_rowid() AS num;";
      ResultSet rs = stmt.executeQuery(check);
      rs.next();
      id = rs.getInt("num");
    } catch (Exception e) {
    	try {
            if (st != null) {
              st.close();
            }
          } catch(SQLException e1) {
        	  e1.printStackTrace();
          }
      return 0;
    }
    return id;
  }
    
  /**
   * Updates an entry in a Criteria-style table.
   * @param table Name of the table.
   * @param row Row of the entry to update.
   * @param parent Row of parent object in its table.
   * @param age Age to be matched.
   * @param height Height to be matched.
   * @param weight Weight to be matched.
   * @param gender Gender to be matched.
   * @param race Ethnicity to be matched.
   * @param nationality Nationality to be matched.
   * @return The row which was updated - 0 upon failure.
   */
  public int updateCriteria(String table, int row, int parent, int age, int height, int weight,
      String gender, String race, String nationality) {
    int id = 0;
    PreparedStatement st = null;
    try {
    	st = conn.prepareStatement("INSERT INTO ? VALUES (?, ?, ?, ?, ?, ?, '?', '?', '?';");
        st.setString(1, table);
        st.setInt(2, row);
        st.setInt(3, parent);
        st.setInt(4,  age);
        st.setInt(5, height);
        st.setInt(6, weight);
        st.setString(7, gender);
        st.setString(8, race);
        st.setString(9, nationality);
        st.executeUpdate();
      String check = "SELECT last_insert_rowid() AS num;";
      ResultSet rs = stmt.executeQuery(check);
      rs.next();
      id = rs.getInt("num");
    } catch (Exception e) {
    	try {
            if (st != null) {
              st.close();
            }
          } catch(SQLException e1) {
        	  e1.printStackTrace();
          }
      return 0;
    }
    return id;
  }
  
  /**
   * Inserts a new entry into a Criteria-style table.
   * @param table Name of the table.
   * @param trialID Row of trial.
   * @param resID Row of researcher.
   * @param partID Row of participant.
   * @param status Status of the match.
   * @return The row into which the new criteria was inserted - 0 upon failure.
   */
  public int insertMatch(String table, int trialID, int resID, int partID, String status) {
    int id = 0;
    PreparedStatement st = null;
    try {
    	st = conn.prepareStatement("INSERT INTO ? VALUES (null, ?, ?, ?, '?';");
      st.setString(1,  table);
      st.setInt(2,  trialID);
      st.setInt(3, resID);
      st.setInt(4,  partID);
      st.setString(5, status);
      st.executeUpdate();
      String check = "SELECT last_insert_rowid() AS num;";
      ResultSet rs = stmt.executeQuery(check);
      rs.next();
      id = rs.getInt("num");
    } catch (Exception e) {
    	try {
            if (st != null) {
              st.close();
            }
          } catch(SQLException e1) {
        	  e1.printStackTrace();
          }
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
