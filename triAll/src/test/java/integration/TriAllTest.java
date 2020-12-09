package integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import controllers.TriAll;
import database.SqliteDB;

import java.util.HashMap;
import java.util.LinkedList;
import models.Criteria;
import models.Match;
import models.Notification;
import models.Trial;
import models.User;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;


public class TriAllTest {

  /**@BeforeAll
  public static void init() {
    // Start Server
    TriAll.main(null);
    System.out.println("Before All");
  }
  
  @Test
  @Order(1)
  public void MatchTest() {
    HttpResponse<?> response = Unirest.get("http://localhost:8080/newgame").asString();
    int restStatus = response.getStatus();
      
    // Check assert statement (New Game has started)
    assertEquals(restStatus, 200);
     System.out.println("Test New Game");
  }**/
  
  
  
}
