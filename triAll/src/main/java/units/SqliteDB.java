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
      String command = "DROP TABLE IF EXISTS %s;";
      command = String.format(command, table);
      st = conn.prepareStatement(command);
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
      st = conn.prepareStatement("SELECT COUNT(name) AS total FROM sqlite_master WHERE type='table' AND name=?;");
      st.setString(1, table);
      ResultSet rs = st.executeQuery();
      rs.next();
      if (rs.getInt("total") == 1) {
        rs.close();
        st.close();
        return true;
      } else {
    	rs.close();
    	st.close();
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
      String command = "SELECT * FROM %s;";
      command  = String.format(command, table);
      st = conn.prepareStatement(command);
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
      String command = "SELECT * FROM %s WHERE %s = ?;";
      command = String.format(command, table, field);
      st = conn.prepareStatement(command);
      st.setInt(1, id);
      rs = st.executeQuery();
    } catch (Exception e) {
      e.printStackTrace();
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
      String command = "SELECT * FROM %s WHERE %s = ?;";
      command = String.format(command, table, field);
      st = conn.prepareStatement(command);
      st.setString(1, id);
      rs = st.executeQuery();
    } catch (Exception e) {
      e.printStackTrace();
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
  
  public String fetchEmail(String table, String field, int id) {
    PreparedStatement st = null;
    String email = "";
    try {
      String command = "SELECT email FROM %s WHERE %s = ?;";
      command = String.format(command, table, field);
      st = conn.prepareStatement(command);
      st.setInt(1, id);
      ResultSet rs = st.executeQuery();
      rs.next();
      email = rs.getString(1);
      st.close();
    } catch (Exception e) {
      e.printStackTrace();
      if (st != null) {
        try {
          st.close();
        } catch (SQLException e1) {
          e1.printStackTrace();
        }
      }
      return email;
    }
    return email;
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
      String command = "SELECT COUNT(ID) FROM %s WHERE %s = ? AND %s = ?;";
      command  = String.format(command, table, field1, field2);
      st = conn.prepareStatement(command);
      st.setInt(1, id1);
      st.setInt(2,  id2);
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
          create = "CREATE TABLE IF NOT EXISTS %s (ID INTEGER PRIMARY KEY AUTOINCREMENT,"
            		+ " Lat REAL, Long REAL, Location TEXT, First TEXT, Last TEXT, Email TEXT);";
          create = String.format(create, table);
          st = conn.prepareStatement(create);
          st.executeUpdate();
          break;
        case participant:
          create = "CREATE TABLE IF NOT EXISTS %s (ID INTEGER PRIMARY KEY AUTOINCREMENT,"
              + " Lat REAL, Long REAL, Location TEXT, First TEXT, Last TEXT, Email TEXT);";
          create = String.format(create, table);
          st = conn.prepareStatement(create);
          st.executeUpdate();
          break;
        case trial:
         create = "CREATE TABLE IF NOT EXISTS %s (ID INTEGER PRIMARY KEY "
              + "AUTOINCREMENT, researcher_ID INT NOT NULL, description TEXT,"
              + " lat REAL, long REAL, location TEXT, start_date TEXT, end_date TEXT, pay REAL,"
              + " IRB INT, participants_needed INT, participants_confirmed INT);";
          create = String.format(create, table);
          st = conn.prepareStatement(create);
          st.executeUpdate();
          break;
        case trialCriteria:
          create = "CREATE TABLE IF NOT EXISTS %s (ID INTEGER PRIMARY KEY AUTOINCREMENT,"
              + " trial_ID INT NOT NULL, Min_Age INT, Max_Age INT, Min_height REAL, Max_height REAL, Min_Weight REAL, Max_weight REAL, Gender"
              + " TEXT, Race TEXT, Nationality TEXT);";
          create = String.format(create, table);
          st = conn.prepareStatement(create);
          st.executeUpdate();
          break;
        case participantData:
        	create = "CREATE TABLE IF NOT EXISTS %s (ID INTEGER PRIMARY KEY AUTOINCREMENT,"
              + " participant_ID INT NOT NULL, minAge INT, maxAge INT, minHeight REAL, MaxHeight"
              + " REAL, MinWeight REAL, maxWeight REAL, Gender TEXT, Race TEXT, Nationality TEXT);";
        	create = String.format(create, table);
            st = conn.prepareStatement(create);
            st.executeUpdate();
          break;
        case trialMatch:
        	create = "CREATE TABLE IF NOT EXISTS %s (ID INTEGER PRIMARY KEY AUTOINCREMENT,"
              + " trial_ID INT NOT NULL, researcher_ID INT NOT NULL, participant_ID INT NOT NULL, "
              + "status TEXT);";
        	create = String.format(create, table);
            st = conn.prepareStatement(create);
            st.executeUpdate();
          break;
        case email:
        	create = "CREATE TABLE IF NOT EXISTS %s (ID INTEGER PRIMARY KEY AUTOINCREMENT,"
              + " trial_ID INT, researcher_ID INT, participant_ID INT, type TEXT, time_sent TEXT,"
              + " delivery_success INT);";
        	create = String.format(create, table);
            st = conn.prepareStatement(create);
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
      String command = "INSERT INTO %s (Lat, Long, First, Last, Email) VALUES ("
    		  + "?, ?, ?, ?, ?);";
      command = String.format(command, table);
      st = conn.prepareStatement(command);
      st.setDouble(1, lat);
      st.setDouble(2, lon);
      st.setString(3, first);
      st.setString(4, last);
      st.setString(5, email);
      st.executeUpdate();
      String check = "SELECT last_insert_rowid() AS num;";
      ResultSet rs = stmt.executeQuery(check);
      rs.next();
      id = rs.getInt("num");
      rs.close();
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
    	String command = "REPLACE INTO %s VALUES ( ?"
        		  + "?, ?, ?, ?, ?);";
        command = String.format(command, table)	;  
    	st = conn.prepareStatement(command);
        st.setInt(1, id);
        st.setDouble(2, lat);
        st.setDouble(3, lon);
        st.setString(4, first);
        st.setString(5, last);
        st.setString(6, email);
        st.executeUpdate();
      String check = "SELECT last_insert_rowid() AS num;";
      ResultSet rs = stmt.executeQuery(check);
      rs.next();
      row = rs.getInt("num");
      rs.close();
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
      String command = "INSERT INTO %s VALUES (null, ?, ?, ?"
              + ", ?, ?, ?, ?, ?, ?, ?, ?);";
      command = String.format(command, table);
      st = conn.prepareStatement(command);
      st.setInt(1, resId);
      st.setString(2, desc);
      st.setDouble(3, lat);
      st.setDouble(4, lon);
      st.setString(5, location);
      st.setString(6, start);
      st.setString(7, end);
      st.setDouble(8, pay);
      st.setInt(9, irb);
      st.setInt(10, needed);
      st.setInt(11, confirmed);
      st.executeUpdate();
      String check = "SELECT last_insert_rowid() AS num;";
      ResultSet rs = stmt.executeQuery(check);
      rs.next();
      id = rs.getInt("num");
      rs.close();
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
      String command = "REPLACE INTO %s VALUES (?, ?, ?, ?"
              + ", ?, ?, ?, ?, ?, ?, ?, ?);";
      command = String.format(command, table);
      st = conn.prepareStatement(command);
      st.setInt(1, row);
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
      rs.close();
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
   * @param minAge Minimum age to be matched.
   * @param maxAge Maximum age to be matched.
   * @param minHeight Minimum height to be matched.
   * @param maxHeight maximum height to be matched.
   * @param minWeight Minimum weight to be matched.
   * @param maxWeight maximum weight to be matched.
   * @param gender Gender to be matched.
   * @param race Ethnicity to be matched.
   * @param nationality Nationality to be matched.
   * @return The row into which the new criteria was inserted - 0 upon failure.
   */
  public int insertCriteria(String table, int parent, int minAge, int maxAge, int minHeight,
      int maxHeight, int minWeight, int maxWeight, String gender, String race, String nationality) {
    int id = 0;
    PreparedStatement st  = null;
    try {
      String command = "INSERT INTO %s VALUES (null, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
      command = String.format(command, table);
      st = conn.prepareStatement(command);
      st.setInt(1, parent);
      st.setInt(2,  minAge);
      st.setInt(3,  maxAge);
      st.setInt(4, minHeight);
      st.setInt(5, maxHeight);
      st.setInt(6, minWeight);
      st.setInt(7, maxWeight);
      st.setString(8, gender);
      st.setString(9, race);
      st.setString(10, nationality);
      st.executeUpdate();
      String check = "SELECT last_insert_rowid() AS num;";
      ResultSet rs = stmt.executeQuery(check);
      rs.next();
      id = rs.getInt("num");
      rs.close();
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
   * Updates an entry in a Criteria-style table.
   * @param table Name of the table.
   * @param row Row of the entry to update.
   * @param parent Row of parent object in its table.
   * @param minAge Minimum age to be matched.
   * @param maxAge Maximum age to be matched.
   * @param minHeight Minimum height to be matched.
   * @param maxHeight maximum height to be matched.
   * @param minWeight Minimum weight to be matched.
   * @param maxWeight maximum weight to be matched.
   * @param gender Gender to be matched.
   * @param race Ethnicity to be matched.
   * @param nationality Nationality to be matched.
   * @return The row which was updated - 0 upon failure.
   */
  public int updateCriteria(String table, int row, int parent, int minAge, int maxAge, int minHeight,
      int maxHeight, int minWeight, int maxWeight, String gender, String race, String nationality) {
    int id = 0;
    PreparedStatement st = null;
    try {
    	String command = "INSERT INTO %s VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
    	command = String.format(command, table);
    	st = conn.prepareStatement(command);
        st.setInt(1, row);
        st.setInt(2, parent);
        st.setInt(3, minAge);
        st.setInt(4, maxAge);
        st.setInt(5, minHeight);
        st.setInt(6, maxHeight);
        st.setInt(7, minWeight);
        st.setInt(8, maxWeight);
        st.setString(9, gender);
        st.setString(10, race);
        st.setString(11, nationality);
        st.executeUpdate();
      String check = "SELECT last_insert_rowid() AS num;";
      ResultSet rs = stmt.executeQuery(check);
      rs.next();
      id = rs.getInt("num");
      rs.close();
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
      String command = "INSERT INTO %s VALUES (null, ?, ?, ?, ?);";
      command = String.format(command, table);
      st = conn.prepareStatement(command);
      st.setInt(1,  trialID);
      st.setInt(2, resID);
      st.setInt(3,  partID);
      st.setString(4, status);
      st.executeUpdate();
      String check = "SELECT last_insert_rowid() AS num;";
      ResultSet rs = stmt.executeQuery(check);
      rs.next();
      id = rs.getInt("num");
      rs.close();
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
  
  public void acceptMatch(String table, int id) {
    PreparedStatement st = null;
    try {
      String command = "UPDATE %s SET status = 'accepted' WHERE trial_ID = ?";
      command = String.format(command, table);
      st = conn.prepareStatement(command);
      st.setInt(1,  id);
      st.executeUpdate();
      st.close();
    } catch (Exception e) {
      try {
        if (st != null) {
          st.close();
        }
      } catch (SQLException e1) {
        e1.printStackTrace();
      }
    }
  }
  
  public void rejectMatch(String table, int id) {
    PreparedStatement st = null;
    try {
      String command = "UPDATE %s SET status = 'rejected' WHERE trial_ID = ?";
      command = String.format(command, table);
      st = conn.prepareStatement(command);
      st.setInt(1,  id);
      st.executeUpdate();
      st.close();
    } catch (Exception e) {
      try {
        if (st != null) {
          st.close();
        }
      } catch (SQLException e1) {
        e1.printStackTrace();
      }
    }
  }
  
  public void incTrial(String table, int id, int confs) {
    PreparedStatement st = null;
    try {
      String command = "UPDATE %s SET status = 'accepted' WHERE trial_ID = ?";
      command = String.format(command, table);
      st = conn.prepareStatement(command);
      st.setInt(1, id);
      st.executeUpdate();
      st.close();
    } catch (Exception e) {
      try {
        if (st != null) {
          st.close();          
        }
      } catch (SQLException e1) {
        e1.printStackTrace();
      }
    }
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
