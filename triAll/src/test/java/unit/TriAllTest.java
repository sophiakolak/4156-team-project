package unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import controllers.TriAll;
import java.util.HashMap;
import java.util.LinkedList;
import models.Criteria;
import models.Match;
import models.Trial;
import models.User;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

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
      12, 123, 100, 0, crit);

  @Test
  @Order(1)
  public void testMatches() {
    Criteria c = new Criteria(1, 1, 23, 23, 67, 67, 130, 130, "male", "cool", "cool");
    boolean isMatch = crit.matches(c);
    assertEquals(true, isMatch);
  }

  /*@Test
  @Order(2)
  public void testPartEmail() {
    Notification n = new Notification("shirishIsCool@gmail.com", "res@res.com");
    String partEmail = n.getPartEmail();
    assertEquals("shirishIsCool@gmail.com", partEmail);
  }

  @Test
  @Order(3)
  public void testResEmail() {
    Notification n = new Notification("shirishIsCool@gmail.com", "res@res.com");
    String resEmail = n.getResEmail();
    assertEquals("res@res.com", resEmail);
  } */

  @Test
  @Order(4)
  public void testUpdate() {
    user1.update(0, 0, "Shirish", "Shirish", "shirishIsNotCool@gmail.com");
    assertEquals("shirishIsNotCool@gmail.com", user1.getEmail());
  }

  @Test
  @Order(5)
  public void addTrial() {
    user2.addTrial(1, tr);
    assertEquals(user2.getTrial(1).getID(), 1);
  }

  @Test
  @Order(6)
  public void containsTrial() {
    user2.addTrial(1, tr);
    assertEquals(user2.containsTrial(1), true);
  }

  @Test
  @Order(7)
  public void sortedMatches() {
    Trial t2 = new Trial(2, "", "cooler trial", 1, 1, "Siberia", "2020-12-01", "2020-12-02", 
        12, 123, 100, 0, crit);
    user1.addMatch(1, tr);
    user1.addMatch(2, t2);
    assertEquals(user1.sortedMatches().get(0).getID(), 1);
  }

  @Test
  @Order(8)
  public void sortedTrials() {
    Trial t1 = new Trial(2, "", "even cooler trial", 1, 1, "NYC", "2020-12-01", "2020-12-02", 
        12, 123, 100, 0, crit);
    Trial t2 = new Trial(3, "", "cooler trial", 1, 1, "Siberia", "2020-11-20", "2020-11-21", 
        12, 123, 100, 0, crit);
    user2.addTrial(2, t1);
    user2.addTrial(3, t2);
    assertEquals(user2.sortedTrials().get(0).getID(), 3);
  }

  @Test
  @Order(9)
  public void distance() {
    double dist = user1.distance(0, 0, 0, 0, "M");
    assertEquals(dist, 0);
  }

  @Test
  @Order(10)
  public void authenticate() {
    boolean auth = TriAll.authenticate(",", "notRealEmail@fake.com");
    assertEquals(auth, false);
  }

  @Test
  public void getEmail() {
    String email = TriAll.getEmail("realemail@aol.com");
    assertEquals(email, "realemail@aol.com");
  }


}
