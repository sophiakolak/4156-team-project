package units;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import models.Criteria;
import models.Match;
import models.Notification;
import models.Trial;
import models.User;

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
   * 
   * @param name The name of the database.
   */
  public SqliteDB(String name) {
    conn = null;
    stmt = null;
    try {
      Class.forName("org.sqlite.JDBC");
      conn = DriverManager.getConnection("jdbc:sqlite:" + name + ".db");
      stmt = conn.createStatement();
      create("researchers", 0);
      create("participants", 1);
      create("trials", 2);
      create("trial_criteria", 3);
      create("participant_data", 4);
      create("trial_matches", 5);
      create("email", 6);
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
    }
  }

  /**
   * Creates table in the database if it isn't already there.
   * 
   * @param table Name of the table.
   * @param type  Which table schema is being used.
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
          st.close();
          break;
        case participant:
          create = "CREATE TABLE IF NOT EXISTS %s (ID INTEGER PRIMARY KEY AUTOINCREMENT,"
              + " Lat REAL, Long REAL, Location TEXT, First TEXT, Last TEXT, Email TEXT);";
          create = String.format(create, table);
          st = conn.prepareStatement(create);
          st.executeUpdate();
          st.close();
          break;
        case trial:
          create = "CREATE TABLE IF NOT EXISTS %s (ID INTEGER PRIMARY KEY "
              + "AUTOINCREMENT, researcher_ID INT NOT NULL, name TEXT, description TEXT,"
              + " lat REAL, long REAL, location TEXT, start_date TEXT, end_date TEXT, pay REAL,"
              + " IRB INT, participants_needed INT, participants_confirmed INT);";
          create = String.format(create, table);
          st = conn.prepareStatement(create);
          st.executeUpdate();
          st.close();
          break;
        case trialCriteria:
          create = "CREATE TABLE IF NOT EXISTS %s (ID INTEGER PRIMARY KEY AUTOINCREMENT,"
              + " trial_ID INT NOT NULL, Min_Age INT, Max_Age INT, Min_height REAL, Max_height REAL"
              + ", Min_Weight REAL, Max_weight REAL, Gender TEXT, Race TEXT, Nationality TEXT);";
          create = String.format(create, table);
          st = conn.prepareStatement(create);
          st.executeUpdate();
          st.close();
          break;
        case participantData:
          create = "CREATE TABLE IF NOT EXISTS %s (ID INTEGER PRIMARY KEY AUTOINCREMENT,"
              + " participant_ID INT NOT NULL, minAge INT, maxAge INT, minHeight REAL, MaxHeight"
              + " REAL, MinWeight REAL, maxWeight REAL, Gender TEXT, Race TEXT, Nationality TEXT);";
          create = String.format(create, table);
          st = conn.prepareStatement(create);
          st.executeUpdate();
          st.close();
          break;
        case trialMatch:
          create = "CREATE TABLE IF NOT EXISTS %s (ID INTEGER PRIMARY KEY AUTOINCREMENT,"
              + " trial_ID INT NOT NULL, researcher_ID INT NOT NULL, participant_ID INT NOT NULL, " 
              + "status TEXT);";
          create = String.format(create, table);
          st = conn.prepareStatement(create);
          st.executeUpdate();
          st.close();
          break;
        case email:
          create = "CREATE TABLE IF NOT EXISTS %s (ID INTEGER PRIMARY KEY AUTOINCREMENT,"
              + " trial_ID INT, researcher_ID INT, participant_ID INT, type TEXT, time_sent TEXT,"
              + " message TEXT);";
          create = String.format(create, table);
          st = conn.prepareStatement(create);
          st.executeUpdate();
          st.close();
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
      } catch (SQLException e1) {
        e1.printStackTrace();
      }
      return false;
    }
    return true;
  }
  
  /**
   * Checks whether a table is already present in the database.
   * 
   * @param table Name of the table.
   * @return Whether table is in the database.
   */
  public boolean isTable(String table) {
    
    try (
        PreparedStatement st = conn.prepareStatement("SELECT COUNT(name) AS total FROM "
            + "sqlite_master WHERE type='table' AND name=?;");
    ) {
      st.setString(1, table);
      try (
          ResultSet rs = st.executeQuery();
      ) {
        rs.next();
        if (rs.getInt("total") == 1) {
          return true;
        }
      } catch (Exception e) {
        return false;
      }
    } catch (Exception e) {
      return false;
    }
    return false;
  }
  
  /**
   * Drops table from the database is it exists.
   * 
   * @param table Name of the table.
   * @return Whether the operation was successful.
   */
  public boolean drop(String table) {
    PreparedStatement st = null;
    try {
      String command = "DROP TABLE IF EXISTS %s;";
      command = String.format(command, table);
      st = conn.prepareStatement(command);
      st.close();
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
   * Checks whether an record is in a certain table.
   * @param table Table to search.
   * @param field Where to look for matches.
   * @param id Value to match against.
   * @return Whether such a record is present.
   */
  public boolean inTable(String table, String field, String id) {
    String command = "SELECT COUNT(*) FROM %s WHERE %s = ?;";
    command = String.format(command, table, field);
    try (
        PreparedStatement st = conn.prepareStatement(command);
    ) {
      st.setString(1, id);
      try (
          ResultSet rs = st.executeQuery();
      ) {
        rs.next();
        if (rs.getInt(1) == 1) {
          return true;
        }
      } catch (Exception e) {
        return false;
      }
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return false;
  }
  
  /**
   * Load a particular researcher from the database.
   * @param email Email address of the sought researcher.
   * @return User object for the researcher; null if not found.
   */
  public User loadRes(String email) {
    User u = null;
    String command = "SELECT * FROM researchers WHERE email = ?;";
    try (
        PreparedStatement st = conn.prepareStatement(command);
    ) {
      st.setString(1,  email);
      try (
          ResultSet rs = st.executeQuery();
      ) {
        rs.next();
        u = new User(rs.getInt(1), rs.getDouble(2), rs.getDouble(3), rs.getString(4), 
            rs.getString(5), rs.getString(6), rs.getString(7), true);
      } catch (Exception e) {
        e.printStackTrace();
        return u;
      }
    } catch (Exception e) {
      e.printStackTrace();
      return u;
    } 
    return u;
  }
  
  /**
   * Load a particular researcher from the database.
   * @param id Row number of the researcher sought.
   * @return User object for the researcher; null if not found.
   */
  public User loadRes(int id) {
    User u = null;
    String command = "SELECT * FROM researchers WHERE ID = ?;";
    try (
        PreparedStatement st = conn.prepareStatement(command);
    ) {
      st.setInt(1,  id);
      try (
          ResultSet rs = st.executeQuery();
      ) {
        rs.next();
        u = new User(rs.getInt(1), rs.getDouble(2), rs.getDouble(3), rs.getString(4), 
            rs.getString(5), rs.getString(6), rs.getString(7), true);
      } catch (Exception e) {
        e.printStackTrace();
        return u;
      }
    } catch (Exception e) {
      e.printStackTrace();
      return u;
    } 
    return u;
  }
  
  /**
   * Load a particular participant from the database.
   * @param email Email address of the sought participant.
   * @return User object for the participant; null if not found.
   */
  public User loadPart(String email) {
    User u = null;
    String command = "SELECT * FROM participants WHERE email = ?;";
    try (
        PreparedStatement st = conn.prepareStatement(command);
    ) {
      st.setString(1,  email);
      try (
          ResultSet rs = st.executeQuery();
      ) {
        rs.next();
        u = new User(rs.getInt(1), rs.getDouble(2), rs.getDouble(3), rs.getString(4), rs
            .getString(5), rs.getString(6), rs.getString(7), false);
      } catch (Exception e) {
        return u;
      }
    } catch (Exception e) {
      return u;
    } 
    return u;
  }
  
  /**
   * Load a particular set of trial criteria from the database.
   * @param id Table row of the criteria record.
   * @return Criteria object for this information; null if not found.
   */
  public Criteria loadCrit(int id) {
    Criteria crit = null;
    String command = "SELECT * FROM trial_criteria WHERE trial_ID = ?;";
    try (
        PreparedStatement st = conn.prepareStatement(command);
    ) {
      st.setInt(1,  id);
      try (
          ResultSet rs = st.executeQuery();
      ) {
        rs.next();
        crit = new Criteria(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs
                  .getInt(4), rs.getDouble(5), rs.getDouble(6), rs.getDouble(7), rs
                  .getDouble(8), rs.getString(9), rs.getString(10), rs.getString(11));
      } catch (Exception e) {
        e.printStackTrace();
        return crit;
      }
    } catch (Exception e) {
      e.printStackTrace();
      return crit;
    }
    return crit;
  }
  
  /**
   * Load a particular set of user data from the database.
   * @param id Table row of the data record.
   * @return Criteria object for this data; null if not found.
   */
  public Criteria loadData(int id) {
    Criteria data = null;
    String command = "SELECT * FROM participant_data WHERE participant_ID = ?;";
    try (
        PreparedStatement st = conn.prepareStatement(command);
    ) {
      st.setInt(1,  id);
      try (
          ResultSet rs = st.executeQuery();
      ) {
        rs.next();
        data = new Criteria(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs
                  .getInt(4), rs.getDouble(5), rs.getDouble(6), rs.getDouble(7), rs
                  .getDouble(8), rs.getString(9), rs.getString(10), rs.getString(11));
      } catch (Exception e) {
        return data;
      }
    } catch (Exception e) {
      return data;
    }
    return data;
  }
  
  /**
   * Collects the row numbers of matches for a particular participant.
   * @param id Row ID of the participant.
   * @return LinkedList of the match rows.
   */
  public LinkedList<Integer> matchSet(int id) {
    ResultSet rs = null;
    LinkedList<Integer> matchSet = new LinkedList<>();
    String command = "SELECT ID FROM trial_matches WHERE participant_ID = ?;";
    try (
        PreparedStatement st = conn.prepareStatement(command);
    ) {
      st.setInt(1,  id);
      rs = st.executeQuery();
      
      while (rs.next()) {
        matchSet.add(rs.getInt(1));
      }
      rs.close();
    } catch (Exception e) {
      return matchSet;
    }
    return matchSet;
  }
  
  /**
   * Collects the row numbers of trials for a particular researcher.
   * @param id Row ID of the researcher.
   * @return LinkedList of the trial rows.
   */
  public LinkedList<Integer> trialSet(int id) {
    ResultSet rs = null;
    LinkedList<Integer> trialSet = new LinkedList<>();
    String command = "SELECT ID FROM trials WHERE researcher_ID = ?;";
    try (
        PreparedStatement st = conn.prepareStatement(command);
    ) {
      st.setInt(1,  id);
      rs = st.executeQuery();
      
      while (rs.next()) {
        trialSet.add(rs.getInt(1));
      }
      rs.close();
    } catch (Exception e) {
      return trialSet;
    }
    return trialSet;
  }
  
  /**
   * Collects the row numbers of all participant accounts.
   * @return LinkedList of the participant rows.
   */
  public LinkedList<String> partSet() {
    ResultSet rs = null;
    LinkedList<String> trialSet = new LinkedList<>();
    String command = "SELECT email FROM participants;";
    try (
        PreparedStatement st = conn.prepareStatement(command);
    ) {
      rs = st.executeQuery();
      
      while (rs.next()) {
        trialSet.add(rs.getString(1));
      }
      rs.close();
    } catch (Exception e) {
      return trialSet;
    }
    return trialSet;
  }
  
  /**
   * Collects the row numbers of all trials.
   * @return LinkedList of the trial rows.
   */
  public LinkedList<Integer> openTrials() {
    ResultSet rs = null;
    LinkedList<Integer> trialSet = new LinkedList<>();
    String command = "SELECT ID FROM trials WHERE participants_needed > participants_confirmed;";
    try (
        PreparedStatement st = conn.prepareStatement(command);
    ) {
      rs = st.executeQuery();
      while (rs.next()) {
        trialSet.add(rs.getInt(1));
      }
      rs.close();
    } catch (Exception e) {
      return trialSet;
    }
    return trialSet;
  }
  
  /**
   * Load a particular trial from the database.
   * @param id Table row of the trial.
   * @return Trial object if found; null if not.
   */
  public Trial loadTrial(int id) {
    Trial t = null;
    String command = "SELECT * FROM trials WHERE ID = ?;";
    try (
        PreparedStatement st = conn.prepareStatement(command);
    ) {
      st.setInt(1,  id);
      try (
          ResultSet rs = st.executeQuery();
      ) {
        rs.next();
        Criteria c = loadCrit(id);
        t = new Trial(rs.getInt(1), rs.getString(3), rs
            .getString(4), rs.getDouble(5), rs.getDouble(6), rs
            .getString(7), rs.getString(8), rs.getString(9), rs
            .getDouble(10), rs.getInt(11), rs.getInt(12), rs.getInt(13), c);
        t.setRes(rs.getInt(2));
      } catch (Exception e) {
        e.printStackTrace();
        return t;
      }
    } catch (Exception e) {
      e.printStackTrace();
      return t;
    }
    return t;
  }
  
  /**
   * Load a particular match from the database.
   * @param id Table row of the match.
   * @return Match object if found; null if not.
   */
  public Match loadMatch(int id) {
    Match m = null;
    String command = "SELECT * FROM trial_matches WHERE ID = ?;";
    try (
        PreparedStatement st = conn.prepareStatement(command);
    ) {
      st.setInt(1,  id);
      try (
          ResultSet rs = st.executeQuery();
      ) {
        rs.next();
        Trial t = loadTrial(rs.getInt(2));
        m = new Match(id, t, 0, rs.getString(5));
      } catch (Exception e) {
        return m;
      }
    } catch (Exception e) {
      return m;
    }
    return m;
  }
  
  /**
   * Inserts an entry into an User-style table.
   * 
   * @param table Name of the table.
   * @param u User to be inserted.
   * @return The row into which the new user was inserted - 0 upon failure.
   */
  public int insertUser(String table, User u) {
    int id = 0;
    String command = "INSERT INTO %s (Lat, Long, Location, First, Last, Email) "
        + "VALUES (?, ?, ?, ?, ?, ?);";
    command = String.format(command, table);
    try (
        PreparedStatement st = conn.prepareStatement(command);
    ) {
      st.setDouble(1, u.getLat());
      st.setDouble(2, u.getLon());
      st.setString(3, u.getLocation());
      st.setString(4, u.getFirst());
      st.setString(5, u.getLast());
      st.setString(6, u.getEmail());
      st.executeUpdate();
      String check = "SELECT last_insert_rowid() AS num;";
      ResultSet rs = stmt.executeQuery(check);
      rs.next();
      id = rs.getInt("num");
      rs.close();
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      return 0;
    }
    return id;

  }

  /**
   * Updates entry in an User-style table.
   * 
   * @param table Name of the table.
   * @param u User with updated information.
   * @return The row which was updated - 0 upon failure.
   */
  public int updateUser(String table, User u) {
    int row = 0;
    String command = "REPLACE INTO %s VALUES (?, ?, ?, ?, ?, ?, ?);";
    command = String.format(command, table);
    try (
        PreparedStatement st = conn.prepareStatement(command);
    ) {
      st.setInt(1, u.getID());
      st.setDouble(2, u.getLat());
      st.setDouble(3, u.getLon());
      st.setString(4, u.getLocation());
      st.setString(5, u.getFirst());
      st.setString(6, u.getLast());
      st.setString(7, u.getEmail());
      st.executeUpdate();
      String check = "SELECT last_insert_rowid() AS num;";
      ResultSet rs = stmt.executeQuery(check);
      rs.next();
      row = rs.getInt("num");
      rs.close();
    } catch (Exception e) {
      return 0;
    }
    return row;
  }
  
  /**
   * Inserts a new entry into a Criteria-style table.
   * 
   * @param table Name of the table.
   * @param crit Criteria object to be inserted.
   * @return The row into which the new criteria was inserted - 0 upon failure.
   */
  public int insertCriteria(String table, Criteria crit) {
    int id = 0;
    String command = "INSERT INTO %s VALUES (null, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
    command = String.format(command, table);
    try (
        PreparedStatement st = conn.prepareStatement(command);
    ) {
      st.setInt(1, crit.getParent());
      st.setInt(2, crit.getMinAge());
      st.setInt(3, crit.getMaxAge());
      st.setDouble(4, crit.getMinHeight());
      st.setDouble(5, crit.getMaxHeight());
      st.setDouble(6, crit.getMinWeight());
      st.setDouble(7, crit.getMaxWeight());
      st.setString(8, crit.getGender());
      st.setString(9, crit.getRace());
      st.setString(10, crit.getNationality());
      st.executeUpdate();
      String check = "SELECT last_insert_rowid() AS num;";
      ResultSet rs = stmt.executeQuery(check);
      rs.next();
      id = rs.getInt("num");
      rs.close();
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      return 0;
    }
    return id;
  }

  /**
   * Updates an entry in a Criteria-style table.
   * 
   * @param table       Name of the table.
   * @param crit Criteria object with updated information.
   * @return The row which was updated - 0 upon failure.
   */
  public int updateCriteria(String table, Criteria crit) {
    int id = 0;
    String command = "REPLACE INTO %s VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
    command = String.format(command, table);
    try (
        PreparedStatement st = conn.prepareStatement(command);
    ) {
      st.setInt(1, crit.getID());
      st.setInt(2, crit.getParent());
      st.setInt(3, crit.getMinAge());
      st.setInt(4, crit.getMaxAge());
      st.setDouble(5, crit.getMinHeight());
      st.setDouble(6, crit.getMaxHeight());
      st.setDouble(7, crit.getMinWeight());
      st.setDouble(8, crit.getMaxWeight());
      st.setString(9, crit.getGender());
      st.setString(10, crit.getRace());
      st.setString(11, crit.getNationality());
      st.executeUpdate();
      String check = "SELECT last_insert_rowid() AS num;";
      ResultSet rs = stmt.executeQuery(check);
      rs.next();
      id = rs.getInt("num");
      rs.close();
    } catch (Exception e) {
      return 0;
    }
    return id;
  }
  
  /**
   * Inserts a new trial into the database.
   * @param t Trial to be inserted.
   * @return The row into which the new trial was inserted - 0 upon failure.
   */
  public int insertTrial(Trial t) {
    int id = 0;
    String command = "INSERT INTO trials VALUES (null, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
    try (
        PreparedStatement st = conn.prepareStatement(command);
    ) {
      st.setInt(1, t.getRes());
      st.setString(2, t.getName());
      st.setString(3, t.getDesc());
      st.setDouble(4, t.getLat());
      st.setDouble(5, t.getLong());
      st.setString(6, t.getLocation());
      st.setString(7, t.getStart());
      st.setString(8, t.getEnd());
      st.setDouble(9, t.getPay());
      st.setInt(10, t.getIrb());
      st.setInt(11, t.getPartNeeded());
      st.setInt(12, t.getPartConf());
      st.executeUpdate();
      String check = "SELECT last_insert_rowid() AS num;";
      ResultSet rs = stmt.executeQuery(check);
      rs.next();
      id = rs.getInt("num");
      rs.close();
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      return 0;
    }
    return id;
  }

  /**
   * Updates a trial in the database.
   * 
   * @param t Trial with updated information.
   * @return The row which was updated - 0 upon failure.
   */
  public int updateTrial(Trial t) {
    int id = 0;
    String command = "REPLACE INTO trials VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
    try (
        PreparedStatement st = conn.prepareStatement(command);
    ) {
      st.setInt(1, t.getID());
      st.setInt(2, t.getRes());
      st.setString(3, t.getName());
      st.setString(4, t.getDesc());
      st.setDouble(5, t.getLat());
      st.setDouble(6, t.getLong());
      st.setString(7, t.getLocation());
      st.setString(8, t.getStart());
      st.setString(9, t.getEnd());
      st.setDouble(10, t.getPay());
      st.setInt(11, t.getIrb());
      st.setInt(12, t.getPartNeeded());
      st.setInt(13, t.getPartConf());
      st.executeUpdate();
      String check = "SELECT last_insert_rowid() AS num;";
      ResultSet rs = stmt.executeQuery(check);
      rs.next();
      id = rs.getInt("num");
      rs.close();
    } catch (Exception e) {
      return 0;
    }
    return id;
  }

  /**
   * Records a match as accepted.
   * @param id Table row of the match.
   */
  public void acceptMatch(int id) {
    String command = "UPDATE trial_matches SET status = 'accepted' WHERE trial_ID = ?";
    try (
        PreparedStatement st = conn.prepareStatement(command);
    ) {
      st.setInt(1, id);
      st.executeUpdate();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Records a match as rejected.
   * @param id Table row of the match.
   */
  public void rejectMatch(int id) {
    String command = "UPDATE trial_matches SET status = 'rejected' WHERE trial_ID = ?";
    try (
        PreparedStatement st = conn.prepareStatement(command);
    ) {
      st.setInt(1, id);
      st.executeUpdate();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Checks whether a match exists.
   * @param partID Table row of the participant.
   * @param trialID Table row of the trial.
   * @return Whether the match was present.
   */
  public boolean matchExists(int partID, int trialID) {
    String command = "SELECT COUNT(*) FROM trial_matches WHERE participant_ID = ? AND trial_ID = ?";
    boolean s = false;
    try (
        PreparedStatement st = conn.prepareStatement(command);
    ) {
      st.setInt(1, partID);
      st.setInt(2,  partID);
      try (
          ResultSet rs = st.executeQuery();
      ) {
        s = rs.getInt(1) > 0;
      } catch (Exception e) {
        return s;
      }
    } catch (Exception e) {
      e.printStackTrace();
      return s;
    }
    return s;
  }
  
  /**
   * Inserts a new match entry.
   * @param u Participant associated with the match.
   * @param t Trial associated with the match.
   * @return The row into which the new match was inserted - 0 upon failure.
   */
  public int insertMatch(User u, Trial t) {
    int id = 0;
    String command = "INSERT INTO trial_matches VALUES (null, ?, ?, ?, 'pending');";
    try (
        PreparedStatement st = conn.prepareStatement(command);
    ) {
      st.setInt(1, t.getID());
      st.setInt(2, t.getRes());
      st.setInt(3, u.getID());
      st.executeUpdate();
      String check = "SELECT last_insert_rowid() AS num;";
      ResultSet rs = stmt.executeQuery(check);
      rs.next();
      id = rs.getInt("num");
      rs.close();
    } catch (Exception e) {
      return 0;
    }
    return id;
  }
  
  /**
   * Inserts an notification entry.
   * 
   * @param trial Row number of the associated trial.
   * @param res Row number of the associated researcher, if applicable - otherwise 0.
   * @param part Row number of the associated participant, if applicable - otherwise 0.
   * @param time Time the notification was sent.
   * @param message Message sent to the recipient.
   * @return The row into which the new entry was inserted - 0 upon failure.
   */
  public int insertNotification(int trial, int res, int part, String time, String message) {
    int id = 0;
    String command = "INSERT INTO email VALUES (null, ?, ?, ?, 'email', ?, ?);";
    try (
        PreparedStatement st = conn.prepareStatement(command);
    ) {
      st.setInt(1, trial);
      st.setInt(2, res);
      st.setInt(3, part);
      st.setString(4, time);
      st.setString(5, message);
      st.executeUpdate();
      String check = "SELECT last_insert_rowid() AS num;";
      ResultSet rs = stmt.executeQuery(check);
      rs.next();
      id = rs.getInt("num");
      rs.close();
    } catch (Exception e) {
      return 0;
    }
    return id;
  }
  
  /**
   * Load a particular notification from the database.
   * @param id Table row of the notification.
   * @return Notification object if found; null if not.
   */
  public Notification loadNotification(int id) {
    Notification n = null;
    String command = "SELECT * FROM email WHERE ID = ?;";
    try (
        PreparedStatement st = conn.prepareStatement(command);
    ) {
      st.setInt(1,  id);
      try (
          ResultSet rs = st.executeQuery();
      ) {
        rs.next();
        n = new Notification(this, rs.getInt(2), rs.getString("time"), rs.getString("message"));
      } catch (Exception e) {
        return n;
      }
    } catch (Exception e) {
      return n;
    }
    return n;
  }
  
  /**
   * Collects the row numbers of notifications sent to a particular user.
   * @param id Row number of the user.
   * @param isResearcher Whether the user is in the researcher or participant table.
   * @return LinkedList of the notification rows.
   */
  public LinkedList<Integer> emailSet(int id, boolean isResearcher) {
    ResultSet rs = null;
    LinkedList<Integer> emailSet = new LinkedList<>();
    String field = "participant_ID";
    if (isResearcher) {
      field = "researcher_ID";
    }
    String command = "SELECT ID FROM email WHERE " + field + " = ?;";
    try (
        PreparedStatement st = conn.prepareStatement(command);
    ) {
      st.setInt(1,  id);
      rs = st.executeQuery();
      
      while (rs.next()) {
        emailSet.add(rs.getInt(1));
      }
      rs.close();
    } catch (Exception e) {
      return emailSet;
    }
    return emailSet;
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
   * 
   * @return Class Connection member.
   */
  public Connection getConn() {
    return conn;
  }

}
