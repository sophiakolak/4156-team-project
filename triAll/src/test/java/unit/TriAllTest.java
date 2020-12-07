package unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import controllers.TriAll;
import java.util.HashMap;
import java.util.LinkedList;
import models.Criteria;
import models.Match;
import models.Notification;
import models.Trial;
import models.User;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import units.SqliteDB;

public class TriAllTest {

  // test Criteria.matches
  // test Notification.getPartEmail
  // test Notification.getResEmail
  // test User.update

  // test User.addTrial
  // test User.containstrial

  // test User.Sortedtrials /compare_date
  // test User.Sortedmatches /compare_dist
  // test User.distance

  HashMap<Integer, Trial> trials = new HashMap<Integer, Trial>();
  LinkedList<Match> matches = new LinkedList<Match>();
  Criteria crit = new Criteria(1, 1, 22, 28, 60, 70, 110, 180, "male", "cool", "cool");
  User user1 = new User(1, 0, 0, "Columbia", "Shirish", "Shirish", "shirishIsCool@gmail.com", 
      false);
  User user2 = new User(1, 0, 0, "Columbia", "Gail", "Kaiser", "gailIsCool@gmail.com", true);
  Trial tr = new Trial(1, "Cool Trial", "cool trial", 0, 0, "Siberia", "2020-12-01", "2020-12-02", 
      12, 1234, 100, 0, crit); 
  
  
  //User equiv. class 1
  @Test 
  public void testGoodUser() {
    User goodPart = new User(0, 80.0, 80.0, "kansas", "Jane", "Doe", "JaneDoe@gmail.com", false);
    double lat = goodPart.getLat();
    assertEquals(80.0, lat);
  }
  
  //User equiv. class 2
  @Test 
  public void testJustBelowUser() {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      User justBelow = new User(-1, -90.1, -180.1, "", "", "", "", false);
    });
  }
  
  //User equiv. class 3
  @Test 
  public void testJustAboveUser() {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      User justAbove = new User(0, 90.1, 180.1, "", "", "", "", false);
    });
  }
  
  //User equiv. class 4
  @Test 
  public void badStringsUser() {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      User badStrings = new User(0, 80.0, 80.0, "", "432", "123", "JaneDoe.com", false);
    });
  }
  
  /**Notification equiv. class 1
  @Test
  public void goodTimeNotification() {
    SqliteDB db = new SqliteDB("triall");
    Notification goodTime = new Notification(db, 0, "10:15:10", "new trial");
    String message = goodTime.getMessage();
    assertEquals("new trial", message);
  }**/
  
  //Notification equiv. class 2
  @Test
  public void badIdMessageNotification() {
    SqliteDB db = new SqliteDB("triall");
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      Notification badIdMessage = new Notification(db, -1, "10:15:30", "");
    });
  }
  
  //Notification equiv. class 3
  @Test
  public void badTimeNotification() {
    SqliteDB db = new SqliteDB("triall");
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      Notification badTime = new Notification(db, 0, "10:1h:30", "new trial");
    });
  }
  
  //Trial equiv class 0 (correct behavior)
  @Test 
  @Order(1)
  public void goodTrial() {
    String location = tr.getLocation();
    assertEquals("Siberia", location);
  }
  
  //Trial equiv class 1 (wrong input, just below)
  @Test 
  public void testJustBelowTrial() {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      Trial t = new Trial(-1, "", "", -90.1, -180.1, "", "2020-12-01", "2020-12-02", -1.0, 123, -1, -1, crit);
    });
  }
  
  //Trial equiv class 2 (wrong input, just above)
  @Test
  public void testAboveTrial() {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      Trial t = new Trial(3, "name", "desc", 90.1, 180.1, "loc", "2020-12-40", "2020-12-50", 10, 123456, 0, 0, crit);
    });
  }
  
  //Trial equiv class 3 (wrong input dates, at boundary)
  @Test
  public void wrongDateTrial() {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      Trial t = new Trial(3, "name", "desc", 90, 180, "loc", "2020-14-12", "2020-15-12", 10, 1234, 0, 0, crit);
    });
  }
  
  //Criteria equiv class 0 (correct behavior)
  @Test
  public void goodCriteria() {
    String gender = crit.getGender();
    assertEquals("male", gender);
  }
  
  //Criteria equiv class 1 
  @Test 
  public void testJustBelowCriteria() {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      Criteria c = new Criteria(-1, -1, 17, 110, -10.0, -12.0, -100.0, -120.0, "female", "white", "american");
    });
  }
  
  ///Criteria equiv class 2 
  @Test 
  public void testJustAboveCriteria() {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      Criteria c = new Criteria(0, 0, 18, 121, 10.0, 12.0, 100.0, 120.0, "male", "white", "american");
    });
  }
  
  //Criteria equiv class 3
  @Test 
  public void badStringCriteria() {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      Criteria c = new Criteria(0, 0, 18, 120, 10.0, 12.0, 100.0, 120.0, "", "", "");
    });
  }
  
  
  //Match equiv. class 0 (correct behavior)
  @Test 
  @Order(5)
  public void goodMatch() {
    Match goodMatch = new Match(0, tr, 0.5, "accepted");
    double dist = goodMatch.getDistance();
    assertEquals(0.5, dist);
  }
  
  //Match equiv. class 1 (wrong input, just below + empty string)
  @Test
  public void badDistMatch() {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      Match badDist = new Match(-1, tr, -1.1, "");
    });
  }
  
  //Match equiv. class 2 (wrong input, just below + correct string)
  @Test
  public void badDistCorrectStatusMatch() {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      Match badDist = new Match(-1, tr, -1.1, "accepted");
    });
  }
  
  //Match equiv. class 3 (wrong input, at boundary + incorrect string)
  @Test
  public void badStatusMatch() {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      Match badStatus = new Match(0, tr, 1.1, "uhIdk");
    });
  }
 
  @Test
  public void testMatches() {
    Criteria c = new Criteria(1, 1, 23, 23, 67, 67, 130, 130, "male", "cool", "cool");
    boolean isMatch = crit.matches(c);
    assertEquals(true, isMatch);
  }

  @Test
  public void testUpdate() {
    user1.update(0, 0, "Shirish", "Shirish", "shirishIsNotCool@gmail.com");
    assertEquals("shirishIsNotCool@gmail.com", user1.getEmail());
  }

  @Test
  @Order(2)
  public void addTrial() {
    user2.addTrial(1, tr);
    assertEquals(user2.getTrial(1).getID(), 1);
  }

  @Test
  public void containsTrial() {
    user2.addTrial(1, tr);
    assertEquals(user2.containsTrial(1), true);
  }

  @Test
  @Order(3)
  public void sortedMatches() {
    Trial t2 = new Trial(2, "name", "cooler trial", 1, 1, "Siberia", "2020-12-01", "2020-12-02", 
        12, 1234, 100, 0, crit);
    user1.addMatch(1, tr);
    user1.addMatch(2, t2);
    assertEquals(user1.sortedMatches().get(0).getID(), 1);
  }

  @Test
  @Order(4)
  public void sortedTrials() {
    Trial t1 = new Trial(2, "name", "even cooler trial", 1, 1, "NYC", "2020-12-01", "2020-12-02", 
        12, 1234, 100, 0, crit);
    Trial t2 = new Trial(3, "name", "cooler trial", 1, 1, "Siberia", "2020-11-20", "2020-11-21", 
        12, 1234, 100, 0, crit);
    user2.addTrial(2, t1);
    user2.addTrial(3, t2);
    assertEquals(user2.sortedTrials().get(0).getID(), 3);
  }

  @Test
  public void distance() {
    double dist = user1.distance(0, 0, 0, 0, "M");
    assertEquals(dist, 0);
  }

  @Test
  public void authenticate() {
    boolean auth = TriAll.authenticate(",", "notRealEmail@fake.com");
    assertEquals(auth, false);
  }
  
 


}
