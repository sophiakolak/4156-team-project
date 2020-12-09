package controllers;

import java.sql.PreparedStatement;

import com.google.gson.Gson;
import models.*;
import units.SqliteDB;

public class Bed {
  
  public static void main(String[] args) {
    /*SqliteDB db = new SqliteDB("triall2");
    User u = new User(1, 1.0, 1.0, "Larchmont", "Julian", "Goldberg", "jules.aaron.g@gmail.com", false);
    db.insertUser("participants", u);
    Trial t = new Trial(1, "Blue", "Blue", 1.0, 1.0, "larchmont", "2022-12-06", "2022-12-09", 30.2, 5757, 10, 0, null);
    db.insertTrial(t);
    u.addMatch(new Match(u, t, db));
    
    Gson gson = new Gson();
    System.out.println(gson.toJson(db.matchSet(u.getID())));
    for (int id : db.matchSet(u.getID())) {
      System.out.println(gson.toJson(db.loadMatch(id)));
    }
    System.out.println(gson.toJson(u.sortedMatches()));*/
    
    SqliteDB db = new SqliteDB("triall");
    String command = "UPDATE trial_matches SET status = 'pending' WHERE trial_ID = ?";
    try (
        PreparedStatement st = db.getConn().prepareStatement(command);
    ) {
      st.setInt(1, 1);
      st.executeUpdate();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}