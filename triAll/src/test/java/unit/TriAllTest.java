package unit;
import com.google.gson.Gson;
import kong.unirest.GetRequest;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import controllers.TriAll;
import models.Criteria;
import models.Match;
import models.Notification;
import models.User;
import models.Trial;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.LinkedList;

public class TriAllTest {
	
	//test Criteria.matches
	//test Notification.getPartEmail
	//test Notification.getResEmail 
	//test User.update
	
	//test User.addTrial
	//test User.containstrial
	
	//test User.Sortedtrials /compare_date 
	//test User.Sortedmatches /compare_dist
	//test User.distance
	
	
	  
	HashMap<Integer, Trial> trials = new HashMap<Integer, Trial>();
	LinkedList<Match> matches = new LinkedList<Match>();
	
	Criteria d = new Criteria(1, 1, 22, 28, 60, 70, 110, 180, "male", "cool", "cool");
	User u = new User(1, 0, 0, "Columbia", "Shirish","Shirish", "shirishIsCool@gmail.com", false);
	User r = new User(1, 0, 0, "Columbia", "Gail","Kaiser", "gailIsCool@gmail.com", true);
	Trial t = new Trial(1, r, "cool trial", 0, 0, "Siberia", "2020-12-01", "2020-12-02", 12, 123, 100, 0, d);
	
	@Test 
	@Order(1)
	public void testMatches() {
		Criteria c = new Criteria(1, 1, 23, 23, 67, 67, 130, 130, "male", "cool", "cool");
		boolean is_match = d.matches(c);
		assertEquals(true, is_match);		
	}
	
@Test 
@Order(2)
	public void testPartEmail() {
		Notification n = new Notification("shirishIsCool@gmail.com", "res@res.com");
		String part_email = n.getPartEmail();
		assertEquals("shirishIsCool@gmail.com", part_email);
	}
	
	@Test 
	@Order(3)
	public void testResEmail() {
		Notification n = new Notification("shirishIsCool@gmail.com", "res@res.com");
		String res_email = n.getResEmail();
		assertEquals("res@res.com", res_email);
	}
	
	@Test 
	@Order(4)
	public void testUpdate() {
		u.update(0, 0, "Shirish", "Shirish", "shirishIsNotCool@gmail.com");
		assertEquals("shirishIsNotCool@gmail.com", u.getEmail());
	}
	
	@Test
	@Order(5)
	public void addTrial() {
		r.addTrial(1, t);
		assertEquals(r.getTrial(1).getID(),1);
	}
	
	@Test
	@Order(6)
	public void containsTrial() {
		r.addTrial(1, t);
		assertEquals(r.containsTrial(1), true);
	}
	
	@Test 
	@Order(7)
	public void sortedMatches() {
		 Trial t2 = new Trial(2, r, "cooler trial", 1, 1, "Siberia", "2020-12-01", "2020-12-02", 12, 123, 100, 0, d);
		 u.addMatch(1, t);
		 u.addMatch(2, t2);
		 assertEquals(u.sortedMatches().get(0).getID(),1);
	}
	
	@Test
	@Order(8)
	public void sortedTrials() {
		Trial t1 = new Trial(2, r, "even cooler trial", 1, 1, "NYC", "2020-12-01", "2020-12-02", 12, 123, 100, 0, d);
		Trial t2 = new Trial(3, r, "cooler trial", 1, 1, "Siberia", "2020-11-20", "2020-11-21", 12, 123, 100, 0, d);
		r.addTrial(2, t1);
		r.addTrial(3, t2);
		assertEquals(r.sortedTrials().get(0).getID(), 3);
	}
	
	@Test
	@Order(9)
	public void distance() {
		double dist = u.distance(0,0,0,0,"M");
		assertEquals(dist, 0);
	}
	
	@Test 
	@Order(10)
	public void authenticate() {
	  boolean auth = TriAll.authenticate(",", "notRealEmail@fake.com");
	  assertEquals(auth, false);
	}
	
	/**@Test 
 public void userMatches() {
	  
	} **/
	
	@Test
	public void getEmail() {
	  String email = TriAll.getEmail("realemail@aol.com");
	  assertEquals(email, "realemail@aol.com");
	}
	
	/**
	@Test 
	public void trialMatches() {
	  
	}**/
	
	
	
}
