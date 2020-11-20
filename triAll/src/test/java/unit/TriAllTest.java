package unit;
import com.google.gson.Gson;
import kong.unirest.GetRequest;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
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
	//test User.compare trials 
	//test User.compare matches
	//test User.update
	//test User.addTrial
	//test User.containstrial
	//test User.Sortedtrials 
	//test User.Sortedmatches
	//test User.distance
	  
	HashMap<Integer, Trial> trials = new HashMap<Integer, Trial>();
	LinkedList<Match> matches = new LinkedList<Match>();
	Criteria d = new Criteria(1, 1, 25, 1.2, 1.2, "male", "cool", "cool");
	User u = new User(1, 0, 0, "Shirish","Shirish", "shirishIsCool@gmail.com", false);
	
	@Test 
	public void testMatches() {
		Criteria c = new Criteria(1, 1, 25, 1.2, 1.2, "male", "cool", "cool");
		boolean is_match = d.matches(c);
		assertEquals(true, is_match);		
	}
}