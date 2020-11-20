package units;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class SqliteDB {
  private Connection conn;
  private Statement stmt;
  public final int researcher = 0;
  public final int participant = 1;
  public final int trial = 2;
  public final int trialCriteria = 3;
  public final int participantData = 4;
  public final int trialMatch = 5;
  public final int email = 6;

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
  
  /**
   * Fetches entries when an integer condition is met.
   * @param table Name of the table.
   * @param field Field to check for the condition.
   * @param id Integer value compared to the field.
   * @return Set of all entries meeting the condition - null upon failure.
   */
  public ResultSet fetchInt(String table, String field, int id) {
    ResultSet rs = null;
    try {
      rs = stmt.executeQuery("SELECT * FROM " + table + " WHERE " + field + " = " + id + ";");
    } catch (Exception e) {
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
    try {
      rs = stmt.executeQuery("SELECT * FROM " + table + " WHERE " + field + " = '" + id + "';");
    } catch (Exception e) {
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
    try {
      rs = stmt.executeQuery("SELECT COUNT(ID) FROM " + table + " WHERE " + field1 + " = " + id1
          + " AND " + field2 + " = " + id2 + ";");
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
    * Creates table in the database if it isn't already there.
    * @param table Name of the table.
    * @param type Which table schema is being used.
    * @return Whether the operation was successful.
    */
  public boolean create(String table, int type) {
    try {
      String create;
      switch (type) {
        case researcher:
          create = "CREATE TABLE IF NOT EXISTS " + table + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,"
              + " Lat REAL, Long REAL, First TEXT, Last TEXT, Email TEXT);";
          break;
        case participant:
          create = "CREATE TABLE IF NOT EXISTS " + table + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,"
              + " Lat REAL, Long REAL, First TEXT, Last TEXT, Email TEXT);";
          break;
        case trial:
          create = "CREATE TABLE IF NOT EXISTS " + table + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,"
              + " researcher_ID INT NOT NULL, description TEXT, lat REAL, long REAL, time TEXT,"
              + " IRB INT, participants_needed INT, participants_confirmed INT);";
          break;
        case trialCriteria:
          create = "CREATE TABLE IF NOT EXISTS " + table + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,"
              + " trial_ID INT NOT NULL, Age INT, Height_in_inches REAL, Weight_in_lbs REAL, Gender"
              + " TEXT, Race TEXT, Nationality TEXT);";
          break;
        case participantData:
          create = "CREATE TABLE IF NOT EXISTS " + table + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,"
              + " participant_ID INT NOT NULL, Age INT, Height_in_inches REAL, Weight_in_lbs REAL, "
              + "Gender TEXT, Race TEXT, Nationality TEXT);";
          break;
        case trialMatch:
          create = "CREATE TABLE IF NOT EXISTS " + table + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,"
              + " trial_ID INT NOT NULL, researcher_ID INT NOT NULL, participant_ID INT NOT NULL, "
              + "status TEXT);";
          break;
        case email:
          create = "CREATE TABLE IF NOT EXISTS " + table + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,"
              + " trial_ID INT, researcher_ID INT, participant_ID INT, type TEXT, time_sent TEXT,"
              + " delivery_success INT);";
          break;
        default:
          return false;
      }
      stmt.executeUpdate(create);
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
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
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
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
    try {
      String add = "REPLACE INTO " + table + " VALUES (" + id + ", "
          + lat + ", " + lon + ", '" + first + "', '" + last + "', '" + email + "');";
      stmt.executeUpdate(add);
      String check = "SELECT last_insert_rowid() AS num;";
      ResultSet rs = stmt.executeQuery(check);
      rs.next();
      row = rs.getInt("num");
    } catch (Exception e) {
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
   * @param time Time of day.
   * @param irb Institutional Review Board number.
   * @param needed Participants needed for trial.
   * @param confirmed Participants confirmed so far.
   * @return The row into which the new trial was inserted - 0 upon failure.
   */
  public int insertTrial(String table, int resId, String desc, double lat, double lon, 
      String time, int irb, int needed, int confirmed) {
    int id = 0;
    try {
      String add = "INSERT INTO " + table + " VALUES (null, " + resId + ", " + lat + ", " + lon
          + ", '" + time + "', " + irb + ", " + needed + ", " + confirmed + ");";
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
   * Updates a trial in the database.
   * @param table Name of the table
   * @param row Row of the trial to be updated.
   * @param resId Row of researcher in their table.
   * @param desc Description of trial.
   * @param lat Latitude coordinate.
   * @param lon Longitude coordinate.
   * @param time Time of day.
   * @param irb Institutional Review Board number.
   * @param needed Participants needed for trial.
   * @param confirmed Participants confirmed so far.
   * @return The row which was updated - 0 upon failure.
   */
  public int updateTrial(String table, int row, int resId, String desc, double lat, double lon, 
      String time, int irb, int needed, int confirmed) {
    int id = 0;
    try {
      String add = "REPLACE INTO " + table + " VALUES (" + row + ", " + resId + ", " + lat + ", " 
           + lon + ", '" + time + "', " + irb + ", " + needed + ", " + confirmed + ");";
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
    try {
      String add = "INSERT INTO " + table + " VALUES (null, " + parent + ", " + age + ", "
          + height + ", " + weight + ", '" + gender + "', '" + race + "', '" + nationality + "');";
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
    try {
      String add = "REPLACE INTO " + table + " VALUES (" + row + ", " + parent + ", " + age + ", "
          + height + ", " + weight + ", '" + gender + "', '" + race + "', '" + nationality + "');";
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
