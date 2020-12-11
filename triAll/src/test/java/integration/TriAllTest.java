package integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import controllers.TriAll;
import database.SqliteDB;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.LinkedList;
import models.Criteria;
import models.Match;
import models.Notification;
import models.Trial;
import models.User;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.json.CDL;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;


public class TriAllTest {

  @BeforeAll
  public static void init() {
    // Start Server
    TriAll.main(null);
    System.out.println("Start server");
  }
  
  @Test
  @Order(1)
  public void landingPage() {
    kong.unirest.HttpResponse<String> response = 
        Unirest.get("http://localhost:8000/").asString();
         int restStatus = response.getStatus();
    assertEquals(restStatus, 200);
     System.out.println("Testing get login page endpoint");
   }
  
  @Test
  @Order(2)
  public void loginSubmit() {
      kong.unirest.HttpResponse<String> response = 
        Unirest.post("http://localhost:8000/login-submit").body("hello@user.com").asString();
      String responseBody = (response).getBody();
            
//      System.out.println("Login-submit response: " + responseBody);
  }
  
  @Test
  @Order(3)
  public void signup() {
    kong.unirest.HttpResponse<String> response = 
        Unirest.get("http://localhost:8000/signup").asString();
         int restStatus = response.getStatus();
    assertEquals(restStatus, 200);
     System.out.println("Testing signup endpoint");
   }
  
  @Test
  @Order(4)
  public void participantSubmit() {
    String partString = "name, value \n" +
        "participant_or_researcher, Participant\n"+
        "firstname, Sophia \n"+
        "lastname, Kolak \n"+
        "email, sophiakolak.sk@gmail.com \n"+
         "location, New York \n"+
         "lat, 40.7127753 \n"+
         "lon, -74.0059728 \n"+
         "age, 21 \n"+
        "gender, Female \n"+
         "metric_or_imperial, Feet \n"+
         "feet, 5 \n"+
         "inches, 4 \n"+
         "centimetersm, \n"+
         "heightInInches, 64 \n"+
         "pounds, 120 \n"+
        "kilograms,  \n"+
         "weightInLbs, 120 \n"+
        "ethnicity, White Other \n"+
        "nationality, american";
    
      Gson gson = new Gson();
      org.json.JSONArray partJson = CDL.toJSONArray(partString);
      JsonArray partForm = gson.fromJson(partJson.toString(), JsonArray.class);
      
      kong.unirest.HttpResponse<String> response = 
        Unirest.post("http://localhost:8000/new-res-submit").body(partForm).asString();
      int restStatus = response.getStatus();
      assertEquals(restStatus, 200);
  }
  
  @Test
  @Order(5)
  public void newTrial() {
    kong.unirest.HttpResponse<String> response = 
        Unirest.get("http://localhost:8000/new-trial-form").asString();
         int restStatus = response.getStatus();
    assertEquals(restStatus, 200);
     System.out.println("Testing new trial form");
   }
  
  @Test
  @Order(6)
  public void editPartForm() {
    kong.unirest.HttpResponse<String> response = 
        Unirest.get("http://localhost:8000/edit-part-form").asString();
         int restStatus = response.getStatus();
    assertEquals(restStatus, 200);
     System.out.println("Testing edit part form");
   }
  
  @Test
  @Order(7)
  public void editResForm() {
    kong.unirest.HttpResponse<String> response = 
        Unirest.get("http://localhost:8000/edit-res-form").asString();
         int restStatus = response.getStatus();
    assertEquals(restStatus, 200);
     System.out.println("Testing edit res form");
   }
  
  @Test
  @Order(8)
  public void partDashboard() {
    kong.unirest.HttpResponse<String> response = 
        Unirest.get("http://localhost:8000/participant-dashboard").asString();
         int restStatus = response.getStatus();
    assertEquals(restStatus, 200);
     System.out.println("Testing getting part dashboard");
   }
  
  @Test
  @Order(9)
  public void resDashboard() {
    kong.unirest.HttpResponse<String> response = 
        Unirest.get("http://localhost:8000/researcher-dashboard").asString();
         int restStatus = response.getStatus();
    assertEquals(restStatus, 200);
     System.out.println("Testing getting res dashboard");
   }
  
  @Test
  @Order(10)
  public void notifications() {
    kong.unirest.HttpResponse<String> response = 
        Unirest.get("http://localhost:8000/notifications").asString();
         int restStatus = response.getStatus();
    assertEquals(restStatus, 200);
     System.out.println("Testing getting notifications");
   }
  
  @Test
  @Order(11)
  public void trialSubmit() {
    String partString = "name, value \n" +
        "name, new trial \n" + 
        "description, desc \n" +
        "location, New York \n" +
        "lat,40.7127753 \n" +
        "lon,-74.0059728 \n"+
        "startdate, 2021-01-01 \n"+
        "enddate, 2021-01-10 \n"+
        "pay, 15 \n"+
        "irb, 1234 \n"+
        "numberofparticipants, 10 \n"+
        "gender, Female \n"+
        "min_age, 20 \n"+
        "max_age, 90 \n"+
        "metric_or_imperial, Feet \n"+
        "feet, 5 \n"+
        "inches, 0 \n"+
        "centimeters, \n"+
        "heightInInchesMin, 60 \n"+
        "feet, 6 \n"+
        "inches, 0 \n"+
        "centimeters \n"+
        "heightInInchesMax, 72 \n"+
        "pounds, 90 \n"+
        "kilograms, \n"+
        "weightInLbs, 90 \n"+
        "pounds, 120 \n"+
        "kilograms, \n"+
        "weightInLbs, 120 \n"+
        "ethnicity, White Other \n"+
        "nationality, american";
    
      Gson gson = new Gson();
      org.json.JSONArray partJson = CDL.toJSONArray(partString);
      JsonArray partForm = gson.fromJson(partJson.toString(), JsonArray.class);
      
//      kong.unirest.HttpResponse<String> response = 
//        Unirest.post("http://localhost:8000/new-trial-submit").body(partForm).asString();
//      int restStatus = response.getStatus();
//      assertEquals(restStatus, 200);
  }
  
  @Test
  @Order(12)
  public void logout() {
    kong.unirest.HttpResponse<String> response = 
        Unirest.post("http://localhost:8000/logout").asString();
         int restStatus = response.getStatus();
         assertEquals(restStatus, 200);
   }
  
  @AfterAll
  public static void close() {
// Stop Server
   TriAll.stop();
   System.out.println("\nStop server");
  }
   
} //end class
