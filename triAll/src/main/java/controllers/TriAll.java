package controllers;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import io.javalin.Javalin;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import models.Criteria;
import models.Notification;
import models.Trial;
import models.User;
import units.SqliteDB;

public class TriAll {

  //Sign in with google is currently only authorized to use port 8000 but we can add more
  private static final int PORT_NUMBER = 8000;

  private static Gson gson;
  private static Javalin app;

  private static User user;
  private static SqliteDB db;

  public static void main(String[] args) {

    db = new SqliteDB("triall");
    gson = new Gson();
    user = new User();

    db.create("researchers", 0);
    db.create("participants", 1);
    db.create("trials", 2);
    db.create("trial_criteria", 3);
    db.create("participant_data", 4);
    db.create("trial_matches", 5);
    db.create("email", 6);


    app = Javalin.create(config -> {
      config.addStaticFiles("/public");
    }).start(PORT_NUMBER);

    app.get("/", ctx -> {
      if (user.isLoggedIn()) {
        if (user.isResearcher()) {
          ctx.result(gson.toJson("/researcherdashboard.html"));
        } else {
          ctx.result(gson.toJson("/participantdashboard.html"));
        }
      } else {
        ctx.redirect("/login.html");
      }
    });

    app.post("/login-submit", ctx -> {
      System.out.println("Signing in User");
      String body = ctx.body();
      System.out.println(body);
      // ctx.body() is json dictionary with email and key
      // authenticate
      String email = getEmail(body);
      if (!authenticate(body, email)) {
        ctx.redirect("not_found.html");
      } else {
        ResultSet rs = db.fetchString("participants", "email", email);
        if (!rs.next()) {
          rs = db.fetchString("researchers", "email", email);
          if (!rs.next()) {
            // user does not exist
            //ctx.redirect("/signup");
            ctx.result(gson.toJson("/signup"));
          } else {
            //is researcher
            user = new User(rs.getInt(1), rs.getDouble(2), rs.getDouble(3), rs.getString(4), 
                rs.getString(5), rs.getString(6), rs.getString(7), true);
            ResultSet trialRS = db.fetchInt("trials", "researcher_ID", user.getID());
            System.out.println(trialRS.getString(7));
            while (trialRS.next()) {
              ResultSet crit = db.fetchInt("trial_criteria", "trial_id", trialRS.getInt(1));
              Criteria c = null;
              if (crit.next()) {
                c = new Criteria(crit.getInt(1), crit.getInt(2), crit.getInt(3), crit.getInt(4),
                    crit.getDouble(5), crit.getDouble(6), crit.getDouble(7), crit.getDouble(8), 
                    crit.getString(9), crit.getString(10), crit.getString(11));
              }
              //System.out.println(crit.getString(8));
              //System.out.println(trialRS.getString(7));
              user.addTrial(trialRS.getInt(1), new Trial(trialRS.getInt(1), user, 
                  trialRS.getString(3), trialRS.getDouble(4), trialRS.getDouble(5), trialRS
                  .getString(6), trialRS.getString(7), trialRS.getString(8), trialRS.getDouble(9), 
                  trialRS.getInt(10), trialRS.getInt(11), trialRS.getInt(12), c));
            }
            ctx.result(gson.toJson("/researcherdashboard.html"));
          }
        } else {
          //is participant
          user = new User(rs.getInt(1), rs.getDouble(2), rs.getDouble(3), rs.getString(4), rs
              .getString(5), rs.getString(6), rs.getString(7), false);
          ResultSet data = db.fetchInt("participant_data", "participant_ID", user.getID());
          if (!data.next()) {
            //there is no data
          } else {
            user.setData(new Criteria(data.getInt(1), data.getInt(2), data.getInt(3), data
                .getInt(4), data.getDouble(5), data.getDouble(6), data.getDouble(7), data
                .getDouble(8), data.getString(9), data.getString(10), data.getString(11)));
            ResultSet matchRS = db.fetchInt("trial_matches", "participant_ID", user.getID());
            while (matchRS.next()) {
              ResultSet trialRS = db.fetchInt("trials", "ID", matchRS.getInt(2));
              ResultSet resRS = db.fetchInt("researchers", "ID", matchRS.getInt(3));
              if (!trialRS.next() || !resRS.next() || matchRS.getString("status")
                  .equals("rejected")) {
                // no such trial
              } else {
                ResultSet critRS = db.fetchInt("trial_criteria", "trial_ID", trialRS.getInt(1));
                Criteria c = null;
                if (critRS.next()) {
                  c = new Criteria(critRS.getInt(1), critRS.getInt(2), critRS.getInt(3), critRS
                      .getInt(4), critRS.getDouble(5), critRS.getDouble(6), critRS
                      .getDouble(7), critRS.getDouble(8), critRS.getString(9), critRS.getString(
                          10), critRS.getString(11));
                }
                user.addMatch(matchRS.getInt(1), new Trial(matchRS.getInt(2), new User(matchRS
                    .getInt(3), resRS.getDouble(2), resRS.getDouble(3), resRS.getString(4), resRS
                    .getString(5), resRS.getString(6), resRS.getString(7), true), trialRS
                    .getString(3), trialRS.getDouble(4), trialRS.getDouble(5), trialRS
                    .getString(6), trialRS.getString(7), trialRS.getString(8), trialRS
                    .getDouble(9), trialRS.getInt(10), trialRS.getInt(11), trialRS.getInt(12), c));
              }
            }
          }
          ctx.result(gson.toJson("/participantdashboard.html"));
        }
      }
    });

    app.get("/signup", ctx -> {
      System.out.println("Redirecting");
      ctx.redirect("/signup.html");
    });

    app.post("/new-part-submit", ctx -> {
      String body = ctx.body();
      System.out.println(body);
      JsonArray form = gson.fromJson(ctx.body(), JsonArray.class);
      double lat = form.get(5).getAsJsonObject().get("value").getAsDouble();
      double lon = form.get(6).getAsJsonObject().get("value").getAsDouble();
      String location = form.get(4).getAsJsonObject().get("value").getAsString();
      String first = form.get(1).getAsJsonObject().get("value").getAsString();
      String last = form.get(2).getAsJsonObject().get("value").getAsString();
      String email = form.get(3).getAsJsonObject().get("value").getAsString();
      int partRow = db.insertUser("participants", lat, lon, location, first, last, email);
      System.out.println(partRow);
      if (partRow == 0) {
        ctx.redirect("not_found.html");
      } else {
        user = new User(partRow, lat, lon, location, first, last, email, false);
        int age = form.get(8).getAsJsonObject().get("value").getAsInt();
        int height = form.get(13).getAsJsonObject().get("value").getAsInt();
        int weight = form.get(16).getAsJsonObject().get("value").getAsInt();
        String gender = form.get(7).getAsJsonObject().get("value").getAsString();
        String race = form.get(17).getAsJsonObject().get("value").getAsString();
        String nationality = form.get(18).getAsJsonObject().get("value").getAsString();
        int critRow = db.insertCriteria("participant_data", partRow, age, age, height, height, 
            weight, weight, gender, race, nationality);
        if (critRow == 0) {
          ctx.redirect("not_found.html");
        } else {
          user.setData(new Criteria(critRow, partRow, age, age, height, height, weight, weight,
              gender, race, nationality));
          checkMatches(user);
          ctx.result(gson.toJson("/participantdashboard.html"));  
        }
      }
    });

    app.post("/new-res-submit", ctx -> {
      System.out.println(ctx.body());
      JsonArray form = gson.fromJson(ctx.body(), JsonArray.class);
      double lat = form.get(5).getAsJsonObject().get("value").getAsDouble();
      double lon = form.get(6).getAsJsonObject().get("value").getAsDouble();
      String location = form.get(4).getAsJsonObject().get("value").getAsString();
      String first = form.get(1).getAsJsonObject().get("value").getAsString();
      String last = form.get(2).getAsJsonObject().get("value").getAsString();
      String email = form.get(3).getAsJsonObject().get("value").getAsString();
      int id = db.insertUser("researchers", lat, lon, location, first,  last, email);
      if (id == 0) {
        ctx.redirect("not_found.html");
      } else {
        user = new User(id, lat, lon, location, first, last, email, true);
        ctx.result(gson.toJson("/researcherdashboard.html"));
      }
    });

    app.get("/new-trial-form", ctx -> {
      ctx.redirect("newtrial.html");
    });

    app.post("/new-trial-submit", ctx -> {
      if (user.isLoggedIn() && user.isResearcher()) {
        int resRow = user.getID();
        System.out.println(ctx.body());
        JsonArray form = gson.fromJson(ctx.body(), JsonArray.class);
        String desc = form.get(0).getAsJsonObject().get("value").getAsString();
        String location = form.get(1).getAsJsonObject().get("value").getAsString();
        double lat = form.get(2).getAsJsonObject().get("value").getAsDouble();
        double lon = form.get(3).getAsJsonObject().get("value").getAsDouble();
        // need start date, end date, pay
        String start = form.get(4).getAsJsonObject().get("value").getAsString();
        String end = form.get(5).getAsJsonObject().get("value").getAsString();
        double pay = form.get(6).getAsJsonObject().get("value").getAsDouble();
        int irb = form.get(7).getAsJsonObject().get("value").getAsInt();
        int needed = form.get(8).getAsJsonObject().get("value").getAsInt();
        int confirmed = 0;
        int trialRow = db.insertTrial("trials", resRow, desc, lat, lon, location, start, end,
            pay, irb, needed, confirmed);
        if (trialRow == 0) {
          ctx.redirect("not_found.html");
        } else {
          int minAge = form.get(10).getAsJsonObject().get("value").getAsInt();
          int maxAge = form.get(11).getAsJsonObject().get("value").getAsInt();
          int minHeight = form.get(16).getAsJsonObject().get("value").getAsInt();
          int maxHeight = form.get(20).getAsJsonObject().get("value").getAsInt();
          int minWeight = form.get(23).getAsJsonObject().get("value").getAsInt();
          int maxWeight = form.get(26).getAsJsonObject().get("value").getAsInt();
          String gender = form.get(9).getAsJsonObject().get("value").getAsString();
          String race = form.get(27).getAsJsonObject().get("value").getAsString();
          String nationality = form.get(28).getAsJsonObject().get("value").getAsString();
          int critRow = db.insertCriteria("trial_criteria", trialRow, minAge, maxAge, minHeight, 
              maxHeight, minWeight, maxWeight, gender, race, nationality);
          if (critRow == 0) {
            ctx.redirect("not_found.html");
          } else {
            Trial t = new Trial(trialRow, user, desc, lat, lon, location, start, end,
                pay, irb, needed, confirmed, new Criteria(critRow, trialRow, minAge, maxAge, 
                    minHeight, maxHeight, minWeight, maxWeight, gender, race, nationality));
            user.addTrial(trialRow, t);
            checkMatches(t);
            ctx.result(gson.toJson("/researcherdashboard.html"));
          }
        }
      } else {
        //not allowed to make a trial
      }
    });

    app.get("/edit-part-form", ctx -> {
      if (!user.isLoggedIn()) {
        ctx.result(gson.toJson("/"));
      } else if (user.isResearcher()) {
        ctx.result(gson.toJson("/"));
      } else {
        String partJson = gson.toJson(user);
        ctx.result(partJson);
      } 
    });

    app.get("/edit-res-form", ctx -> {
      if (!user.isLoggedIn()) {
        ctx.result(gson.toJson("/"));
      } else if (!user.isResearcher()) {
        ctx.result(gson.toJson("/"));
      } else {
        String resJson = gson.toJson(user);
        ctx.result(resJson);
      }
    });

    app.post("/edit-part-submit", ctx -> {
      if (user.isLoggedIn() && !user.isResearcher()) {
        System.out.println(ctx.body());
        JsonArray form = gson.fromJson(ctx.body(), JsonArray.class);
        double lat = form.get(4).getAsJsonObject().get("value").getAsDouble();
        System.out.println(lat);
        double lon = form.get(5).getAsJsonObject().get("value").getAsDouble();
        System.out.println(lon);
        
//        double lat = user.getLat();
//        double lon = user.getLon();
        String location = form.get(3).getAsJsonObject().get("value").getAsString();
        String first = form.get(0).getAsJsonObject().get("value").getAsString();
        String last = form.get(1).getAsJsonObject().get("value").getAsString();
        System.out.println(first + " " + last);
//        String email = user.getEmail();
        String email = form.get(2).getAsJsonObject().get("value").getAsString();
        System.out.println(email);
        int partId = db.updateUser("participants", user.getID(), lat, lon, location, first, last, email);
        if (partId == 0) {
          System.out.println("Not found");
          ctx.redirect("not_found.html");
        } else {
          user.update(lat, lon, first, last, email);
          int age = form.get(7).getAsJsonObject().get("value").getAsInt();
          System.out.println("Age: " + age);
          int height = form.get(12).getAsJsonObject().get("value").getAsInt();
          System.out.println("Height" + height);
          int weight = form.get(15).getAsJsonObject().get("value").getAsInt();
          System.out.println("Weight" + weight);
          String gender = form.get(6).getAsJsonObject().get("value").getAsString();
          String race = form.get(16).getAsJsonObject().get("value").getAsString();
          String nationality = form.get(17).getAsJsonObject().get("value").getAsString();
          int userId = user.getData().getID();
          System.out.println(userId);
          
          // this line does not work (critRow == 0)
          int critRow = db.updateCriteria("participant_data", userId, partId, 
              age, age, height, height, weight, weight, gender, race, nationality);
          
          user.setData(new Criteria(critRow, partId, age, age, height, height, weight, weight, 
              gender, race, nationality));
          checkMatches(user);
          System.out.println("redirect to dashboard");
          ctx.result(gson.toJson("/participantdashboard.html")); 
          
//          if (critRow == 0) {
//            ctx.result(gson.toJson("participantdashboard.html"));
//            System.out.println("Not found c");
////            ctx.redirect("not_found.html");
//          } else {
//            user.setData(new Criteria(critRow, partId, age, age, height, height, weight, weight, 
//                gender, race, nationality));
//            checkMatches(user);
//            System.out.println("redirect to dashboard");
//            ctx.result(gson.toJson("/participantdashboard.html"));  
//          }
        }
      } else { 
        //not allowed
        System.out.println(ctx.body());
        System.out.println("Not allowed");
      }
    });

    app.post("/edit-res-submit", ctx -> {
      if (user.isLoggedIn() && user.isResearcher()) {
        JsonArray form = gson.fromJson(ctx.body(), JsonArray.class);
        double lat = form.get(5).getAsJsonObject().get("value").getAsDouble();
        double lon = form.get(6).getAsJsonObject().get("value").getAsDouble();
        String location = form.get(4).getAsJsonObject().get("value").getAsString();
        String first = form.get(1).getAsJsonObject().get("value").getAsString();
        String last = form.get(2).getAsJsonObject().get("value").getAsString();
        String email = form.get(3).getAsJsonObject().get("value").getAsString();        
        int id = db.updateUser("researchers", user.getID(), lat, lon, location, first, last, email);
        if (id == 0) {
          ctx.redirect("not_found.html");
        } else {
          user = new User(id, lat, lon, location, first, last, email, true);
          ctx.result(gson.toJson("/researcherdashboard.html"));
        }
      } else {
        //not allowed
      }
    });

    app.get("/edit-trial-form/:trialId/", ctx -> {
      int trialID = ctx.pathParam("trialId", Integer.class).get();
      if (user.isResearcher()) {
        Trial t = user.getTrial(trialID);
        if (t != null) {
          String trialJson = gson.toJson(t);
          ctx.result(trialJson);  
          ctx.redirect("/edittrial.html");
        } else {
          //not allowed to access this trial
          ctx.redirect("not_found.html");
        }
      }


    });

    app.post("/edit-trial-submit/:trialId/", ctx -> {
      int trialID = ctx.pathParam("trialId", Integer.class).get();
      if (user.isLoggedIn() && user.containsTrial(trialID)) {
        int resId = user.getID();
        JsonArray form = gson.fromJson(ctx.body(), JsonArray.class);
        String desc = form.get(1).getAsJsonObject().get("value").getAsString();
        String location = form.get(2).getAsJsonObject().get("value").getAsString();
        double lat = form.get(3).getAsJsonObject().get("value").getAsDouble();
        double lon = form.get(4).getAsJsonObject().get("value").getAsDouble();
        // need start date, end date, pay
        String start = form.get(5).getAsJsonObject().get("value").getAsString();
        String end = form.get(6).getAsJsonObject().get("value").getAsString();
        double pay = form.get(7).getAsJsonObject().get("value").getAsDouble();
        int irb = form.get(8).getAsJsonObject().get("value").getAsInt();
        int needed = form.get(9).getAsJsonObject().get("value").getAsInt();
        int confirmed = 0;
        int trialRow = db.updateTrial("trials", trialID, resId, desc, lat, lon, location, start,
            end, pay, irb, needed, confirmed);
        if (trialRow == 0) {
          ctx.redirect("not_found.html");
        } else {
          int minAge = form.get(7).getAsJsonObject().get("value").getAsInt();
          int maxAge = form.get(8).getAsJsonObject().get("value").getAsInt();
          int minHeight = form.get(13).getAsJsonObject().get("value").getAsInt();
          int maxHeight = form.get(17).getAsJsonObject().get("value").getAsInt();
          int minWeight = form.get(20).getAsJsonObject().get("value").getAsInt();
          int maxWeight = form.get(23).getAsJsonObject().get("value").getAsInt();
          String gender = form.get(6).getAsJsonObject().get("value").getAsString();
          String race = form.get(24).getAsJsonObject().get("value").getAsString();
          String nationality = form.get(25).getAsJsonObject().get("value").getAsString();
          int critRow = db.updateCriteria("trial_criteria", user.getTrial(trialID).getCriteria()
              .getID(), trialRow, minAge, maxAge, minHeight, maxHeight, minWeight, maxWeight, 
              gender, race, nationality);
          if (critRow == 0) {
            ctx.redirect("not_found.html");
          } else {
            Trial t = new Trial(trialRow, user, desc, lat, lon, location, start, end,
                pay, irb, needed, confirmed, new Criteria(critRow, trialRow, minAge, maxAge, 
                    minHeight, maxHeight, minWeight, maxWeight, gender, race, nationality));
            checkMatches(t); 
            user.addTrial(trialRow, t);
          }
        }
      } else {
        //does not have permission to access trial
      }

    });        


    app.post("/logout", ctx -> {
      //ctx.body() contains email of user to be logged out
      user.logOut();
      System.out.println("Logging out user");
      ctx.result(gson.toJson("/"));
    });

    //Routes added by sarah that we might need
    //If we don't need them please just delete them
    app.post("/accept-match/", ctx -> {
      int trialID = Integer.parseInt(ctx.body());
      int confs = user.acceptMatch(trialID);
      if (confs >= 0) {
        db.acceptMatch("trial_matches", trialID);
        db.incTrial("trials", trialID, confs);
        ctx.result(gson.toJson("/participantdashboard.html"));
      } else {
        ctx.redirect("not_found.html");
      }
    });

    app.post("/reject-match/", ctx -> {
      int trialID = Integer.parseInt(ctx.body());
      if (user.rejectMatch(trialID)) {
        db.rejectMatch("trial_matches", trialID);
        ctx.result(gson.toJson("/participantdashboard.html"));
      } else {
        ctx.redirect("not_found.html");
      }
    });

    app.get("/researcher-dashboard", ctx -> {
      if (user.isLoggedIn() && user.isResearcher()) {
        LinkedList<Trial> trials = user.sortedTrials();
        //Trial[] array = trials.toArray(new Trial[trials.size()]);
        String trialsJson = gson.toJson(trials);
        System.out.println(trialsJson);
        ctx.result(trialsJson);
      } else {
        ctx.redirect("/");
      }
    });

    app.get("/participant-dashboard", ctx -> {
      if (user.isLoggedIn() && !user.isResearcher()) {
        String matchesJson = gson.toJson(user.sortedMatches());
        System.out.println(matchesJson);
        ctx.result(matchesJson);
      } else {
        ctx.redirect("/");
      }
    });

  }

  public static void checkMatches(User user) {
    try {
      ResultSet trials = db.fetchAll("trial_criteria");
      while (trials.next()) {
        Criteria c = new Criteria(trials.getInt(1), trials.getInt(2), trials.getInt(3), trials
            .getInt(4), trials.getDouble(5), trials.getDouble(6), trials.getDouble(7), trials
            .getDouble(8), trials.getString(9), trials.getString(10), trials.getString(11));
        if (c.matches(user.getData())) {
          if (!db.matchExists("trial_matches", "trial_id", trials.getInt(2), "participant_id", 
              user.getID())) {
            ResultSet trial = db.fetchInt("trials", "ID", trials.getInt(2));
            if (trial.next()) {
              int row = db.insertMatch("trial_matches", trial.getInt(1), trial.getInt(2), 
                  user.getID(), "pending");
              user.addMatch(row, new Trial(trial.getInt(1), null, trial.getString(3), trial
                  .getDouble(4), trial.getDouble(5), trial.getString(6), trial.getString(7), 
                  trial.getString(8), trial.getDouble(9), trial.getInt(10), trial.getInt(11),
                  trial.getInt(12), c));
              String email = db.fetchEmail("researchers", "ID", trials.getInt(2));
              Notification n = new Notification(user.getEmail(), email);
              n.notifyUsers();
            }
          }
        }
      }
    } catch (SQLException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(0);
    }

    //check the extant matches, and remove any that no longer match
  }

  public static void checkMatches(Trial trial) {
    try {
      ResultSet data = db.fetchAll("participant_data");
      while (data.next()) {
        Criteria d = new Criteria(data.getInt(1), data.getInt(2), data.getInt(3), data
            .getInt(4), data.getDouble(5), data.getDouble(6), data.getDouble(7), data
            .getDouble(8), data.getString(9), data.getString(10), data.getString(11));
        if (trial.getCriteria().matches(d)) {
          if (!db.matchExists("trial_matches", "trial_id", trial.getID(), "participant_id", 
              data.getInt(2))) {
            ResultSet trialRS = db.fetchInt("trials", "ID", data.getInt(2));
            if (trialRS.next()) {
              db.insertMatch("trial_matches", trialRS.getInt(1), trialRS.getInt(2), 
                  data.getInt(2), "pending");
              String email = db.fetchEmail("participants", "ID", data.getInt(2));
              Notification n = new Notification(email, user.getEmail());
              n.notifyUsers();
            }
          }
        }
      }
    } catch (SQLException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(0);
    }

    //check the extant matches and remove any that no longer match
  }

  public static String getEmail(String body) {
    String[] parts = body.split(":");
    if (parts.length > 1) {
      String[] email = parts[1].split("\"");
      return email[1];
    } else {
      return parts[0];
    }
  }

  public static boolean authenticate(String body, String email) {
    String[] parts = body.split(":");
    if (parts.length > 2) {
      String[] token = parts[2].split("\"");
      System.out.println(token[1]);
      GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(),
          new JacksonFactory())
          // Specify the CLIENT_ID of the app that accesses the backend:
          .setAudience(Collections.singletonList(
              "46819195782-rhbp0ull70okmgsid0rrd2p8cdub7fpn.apps.googleusercontent.com"))
          // Or, if multiple clients access the backend:
          //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
          .build();
      try {
        GoogleIdToken idToken = verifier.verify(token[1]);
        if (idToken != null) {
          System.out.println("Checking Email");
          Payload payload = idToken.getPayload();
          String email2 = payload.getEmail();
          if (email.equals(email2)) {
            return true;
          } else {
            return false;
          }
        } else {
          System.out.println("This is not avalid token");
          return false;
        }
      } catch (Exception e) {
        return false;
      }
    }
    return false;
  }

  public static void stop() {
    app.stop();
    db.stop();
  }

}
