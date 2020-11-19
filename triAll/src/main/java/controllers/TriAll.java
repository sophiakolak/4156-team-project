package controllers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import io.javalin.Javalin;
import java.sql.ResultSet;
import java.sql.SQLException;
import models.Criteria;
import models.Trial;
import models.User;
import units.SqliteDB;

class TriAll {

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
          ctx.result(gson.toJson("/researcher-dashboard"));
        } else {
          ctx.result(gson.toJson("/participant-dashboard"));
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
            rs.getString(5), rs.getString(6), true);
          ResultSet trial_rs = db.fetchInt("trials", "researcher_ID", user.getID());
          while (trial_rs.next()) {
            ResultSet crit = db.fetchInt("trial_criteria", "trial_id", trial_rs.getInt(1));
            Criteria c = null;
            if (crit.next()) {
              c = new Criteria(crit.getInt(1), crit.getInt(2), crit.getInt(3), crit.getDouble(4), 
                crit.getDouble(5), crit.getString(6), crit.getString(7), crit.getString(8));
            }
            user.addTrial(trial_rs.getInt(1), new Trial(trial_rs.getInt(1), user, 
                trial_rs.getString(3), trial_rs.getDouble(4), trial_rs.getDouble(5), trial_rs
                .getString(6), trial_rs.getInt(7), trial_rs.getInt(8), trial_rs.getInt(9), c));
          }
          ctx.result(gson.toJson("/researcher-dashboard"));
        }
      } else {
        //is participant
        user = new User(rs.getInt(1), rs.getDouble(2), rs.getDouble(3), rs.getString(4), rs
          .getString(5), rs.getString(6), false);
        ResultSet data = db.fetchInt("participant_data", "participant_ID", user.getID());
        if (!data.next()) {
          //there is no data
        } else {
          user.setData(new Criteria(data.getInt(1), data.getInt(2), data.getInt(3), data.getDouble(4), data.getDouble(5), data.getString(6), data.getString(7), data.getString(8)));
          ResultSet match_rs = db.fetchInt("matches", "participant_ID", user.getID());
          while (match_rs.next()) {
            ResultSet trial_rs = db.fetchInt("trials", "ID", match_rs.getInt(2));
            ResultSet res_rs = db.fetchInt("researchers", "ID", match_rs.getInt(3));
            if(!trial_rs.next() || !res_rs.next() || trial_rs.getString("status").equals("rejected")) {
              // no such trial
            } else {
              ResultSet crit_rs = db.fetchInt("trial_criteria", "trial_ID", trial_rs.getInt(1));
              Criteria c = null;
              if (crit_rs.next()) {
                c = new Criteria(crit_rs.getInt(1), crit_rs.getInt(2), crit_rs.getInt(3), crit_rs.getDouble(4), crit_rs.getDouble(5), crit_rs.getString(6), crit_rs.getString(7), crit_rs.getString(8));
              }
              user.addMatch(new Trial(match_rs.getInt(2), new User(match_rs.getInt(3), res_rs.getDouble(2), res_rs.getDouble(4), res_rs.getString(5), res_rs.getString(6), res_rs.getString(7), true), trial_rs.getString(3), trial_rs.getDouble(4), trial_rs.getDouble(5), trial_rs.getString(6), trial_rs.getInt(7), trial_rs.getInt(8), trial_rs.getInt(9), c));
            }
          }
        }
        ctx.result(gson.toJson("/participant-dashboard"));
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
      // location will also be passed in as a form param - can we store that too?
      String first = form.get(1).getAsJsonObject().get("value").getAsString();
      String last = form.get(2).getAsJsonObject().get("value").getAsString();
      String email = form.get(3).getAsJsonObject().get("value").getAsString();
      int partRow = db.insertUser("participants", lat, lon, first, last, email);
      System.out.println(partRow);
      if (partRow == 0) {
        ctx.redirect("not_found.html");
      } else {
        user = new User(partRow, lat, lon, first, last, email, false);
        int age = 0;
        int height = form.get(16).getAsJsonObject().get("value").getAsInt();
        int weight = form.get(13).getAsJsonObject().get("value").getAsInt();
        String gender = form.get(7).getAsJsonObject().get("value").getAsString();
        String race = form.get(17).getAsJsonObject().get("value").getAsString();
        String nationality = form.get(18).getAsJsonObject().get("value").getAsString();
        int critRow = db.insertCriteria("participant_data", partRow, age, height, weight, gender, race, nationality);
        if (critRow == 0) {
          ctx.redirect("not_found.html");	  
        } else {
          user.setData(new Criteria(critRow, partRow, age, height, weight, gender, race, nationality));
          ctx.result(gson.toJson("/participantdashboard"));  
        }
      }
    });

    app.post("/new-res-submit", ctx -> {
      System.out.println("Submit endpoint");
      JsonArray form = gson.fromJson(ctx.body(), JsonArray.class);
      double lat = form.get(5).getAsJsonObject().get("value").getAsDouble();
      double lon = form.get(6).getAsJsonObject().get("value").getAsDouble();
      // location will also be passed in as a form param - can we store that too?
      String first = form.get(1).getAsJsonObject().get("value").getAsString();
      String last = form.get(2).getAsJsonObject().get("value").getAsString();
      String email = form.get(3).getAsJsonObject().get("value").getAsString();
      int id = db.insertUser("researchers", lat, lon, first,  last, email);
      if (id == 0) {
        ctx.redirect("not_found.html");
      } else {
        user = new User(id, lat, lon, first, last, email, true);
        ctx.result(gson.toJson("/researcherdashboard"));
      }
    });

    app.get("/new-trial-form", ctx -> {
      ctx.redirect("newtrial.html");
    });
        
    app.post("/new-trial-submit", ctx -> {
      if (user.isLoggedIn() && user.isResearcher()) {
        int resRow = user.getID();
        JsonArray form = gson.fromJson(ctx.body(), JsonArray.class);
        String desc = form.get(1).getAsJsonObject().get("value").getAsString();
        double lat = 0;
        double lon = 0;
        String time = form.get(3).getAsJsonObject().get("value").getAsString();
        int irb = form.get(4).getAsJsonObject().get("value").getAsInt();
        int needed = form.get(5).getAsJsonObject().get("value").getAsInt();
        int confirmed = 0;
        int trialRow = db.insertTrial("trials", resRow, desc, lat, lon, time, irb, needed, confirmed);
        if (trialRow == 0) {
          ctx.redirect("not_found.html");
        } else {
          int age = form.get(7).getAsJsonObject().get("value").getAsInt();
          int height = form.get(13).getAsJsonObject().get("value").getAsInt();
          int weight = form.get(20).getAsJsonObject().get("value").getAsInt();
          String gender = form.get(6).getAsJsonObject().get("value").getAsString();
          String race = form.get(24).getAsJsonObject().get("value").getAsString();
          String nationality = form.get(25).getAsJsonObject().get("value").getAsString();
          int critRow = db.insertCriteria("trial_criteria", trialRow, age, height, weight, gender, race, nationality);
          if (critRow == 0) {
            ctx.redirect("not_found.html");
          } else {
            user.addTrial(trialRow, new Trial(trialRow, user, desc, lat, lon, time, irb, needed, confirmed,
              new Criteria(critRow, trialRow, age, height, weight, gender, race, nationality)));
            ctx.result(gson.toJson("/researcherdashboard"));
          }
        }
      } else {
        //not allowed to make a trial
      }
    });
        
    app.get("/edit-part-form", ctx -> {
      if (!user.isLoggedIn()) {

      } else if (user.isResearcher()) {

      } else {
        String partJson = gson.toJson(user);
        ctx.result(partJson);
        ctx.redirect("/editparticipantinfo.html");
      } 
    });

    app.get("/edit-res-form", ctx -> {
      if (!user.isLoggedIn()) {

      } else if (!user.isResearcher()) {

      } else {
        String resJson = gson.toJson(user);
        ctx.result(resJson);
        ctx.redirect("/editresearcherinfo.html"); 
      }
    });

    app.post("/edit-part-submit", ctx -> {
      if (user.isLoggedIn() && !user.isResearcher()) {
        JsonArray form = gson.fromJson(ctx.body(), JsonArray.class);
        double lat = form.get(5).getAsJsonObject().get("value").getAsDouble();
        double lon = form.get(6).getAsJsonObject().get("value").getAsDouble();
        // location will also be passed in as a form param - can we store that too?
        String first = form.get(1).getAsJsonObject().get("value").getAsString();
        String last = form.get(2).getAsJsonObject().get("value").getAsString();
        String email = form.get(3).getAsJsonObject().get("value").getAsString(); 
        int partId = db.updateUser("participants", user.getID(), lat, lon, first,  last, email);
        if (partId == 0) {
          ctx.redirect("not_found.html");
        } else {
          user.update(lat, lon, first, last, email);
          int age = 0;
          int height = form.get(16).getAsJsonObject().get("value").getAsInt();
          int weight = form.get(13).getAsJsonObject().get("value").getAsInt();
          String gender = form.get(7).getAsJsonObject().get("value").getAsString();
          String race = form.get(17).getAsJsonObject().get("value").getAsString();
          String nationality = form.get(18).getAsJsonObject().get("value").getAsString();
          int critRow = db.updateCriteria("participant_data", user.getData().getID(), partId, age, height, weight, gender, race, nationality);
          if (critRow == 0) {
            ctx.redirect("not_found.html");	  
          } else {
            user.setData(new Criteria(critRow, partId, age, height, weight, gender, race, nationality));
            ctx.result(gson.toJson("/participantdashboard"));  
          }
        }
      } else {
        //not allowed
      }
    });

    app.post("/edit-res-submit", ctx -> {
      if (user.isLoggedIn() && user.isResearcher()) {
        JsonArray form = gson.fromJson(ctx.body(), JsonArray.class);
        double lat = form.get(5).getAsJsonObject().get("value").getAsDouble();
        double lon = form.get(6).getAsJsonObject().get("value").getAsDouble();
        // location will also be passed in as a form param - can we store that too?
        String first = form.get(1).getAsJsonObject().get("value").getAsString();
        String last = form.get(2).getAsJsonObject().get("value").getAsString();
        String email = form.get(3).getAsJsonObject().get("value").getAsString();        
        int id = db.updateUser("researchers", user.getID(), lat, lon, first,  last, email);
        if (id == 0) {
          ctx.redirect("not_found.html");
        } else {
          user = new User(id, lat, lon, first, last, email, true);
          ctx.result(gson.toJson("/researcherdashboard"));
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
        }
      }
        	

    });
        
    app.post("/edit-trial-submit/:trialId/", ctx -> {
      int trialID = ctx.pathParam("trialId", Integer.class).get();
      if (user.isLoggedIn() && user.containsTrial(trialID)) {
        int resId = user.getID();
        JsonArray form = gson.fromJson(ctx.body(), JsonArray.class);
        String desc = form.get(1).getAsJsonObject().get("value").getAsString();
        double lat = 0;
        double lon = 0;
        String time = form.get(3).getAsJsonObject().get("value").getAsString();
        int irb = form.get(4).getAsJsonObject().get("value").getAsInt();
        int needed = form.get(5).getAsJsonObject().get("value").getAsInt();
        int confirmed = 0;
        int trialRow = db.updateTrial("trials", trialID, resId, desc, lat, lon, time, irb, needed, confirmed);
        if (trialRow == 0) {
          ctx.redirect("not_found.html");
        } else {
          int age = form.get(7).getAsJsonObject().get("value").getAsInt();
          int height = form.get(13).getAsJsonObject().get("value").getAsInt();
          int weight = form.get(20).getAsJsonObject().get("value").getAsInt();
          String gender = form.get(6).getAsJsonObject().get("value").getAsString();
          String race = form.get(24).getAsJsonObject().get("value").getAsString();
          String nationality = form.get(25).getAsJsonObject().get("value").getAsString();
          int critRow = db.updateCriteria("trial_criteria", user.getTrial(trialID).getCriteria().getID(), trialRow, age, height, weight, gender, race, nationality);
          if (critRow == 0) {
            ctx.redirect("not_found.html");
          } else {
            user.addTrial(trialRow, new Trial(trialRow, user, desc, lat, lon, time, irb, needed, confirmed,
          	  					new Criteria(critRow, trialRow, age, height, weight, gender, race, nationality)));
            ctx.result(gson.toJson("/researcherdashboard"));
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
      ctx.redirect("/");        	
    });
        
    //Routes added by sarah that we might need
    //If we don't need them please just delete them
    app.post("/accept-match/:trialId/", ctx -> {
      // change trial status to accepted
      // increment participants_confirmed 
    });
        
    app.post("/reject-match/:trialId/", ctx -> {
      // change trial status to rejected
    });
        
    app.get("/researcher-dashboard", ctx -> {
      if (user.isLoggedIn() && user.isResearcher()) {
        String trialsJson = gson.toJson(user.sortedTrials());
        ctx.result(trialsJson);
        ctx.redirect("researcherdashboard.html");
      } else {
        ctx.redirect("/");
      }
    });
 
    app.get("/participant-dashboard", ctx -> {
      if (user.isLoggedIn() && !user.isResearcher()) {
        String matchesJson = gson.toJson(user.sortedMatches());
        ctx.result(matchesJson);
        ctx.redirect("participantdashboard.html");
      } else {
        ctx.redirect("/");
      }
    });

  }
	
  public static void checkMatches(User user) {
    try {
      ResultSet trials = db.fetchAll("trial_criteria");
      while (trials.next()) {
        Criteria c = new Criteria(trials.getInt(1), trials.getInt(2), trials.getInt(3), trials.getDouble(4), trials.getDouble(5), trials.getString(6), trials.getString(7), trials.getString(8));
        if (user.getData().matches(c)) {
          if (!db.matchExists("trial_matches", "trial_id", trials.getInt(2), "participant_id", user.getID())) {
            //add this match
            //notify
           }
        }
      }
    } catch (SQLException e) {

    }

    //check the extant matches, and remove any that no longer match
  }
	
  public static void checkMatches(Trial trial) {
    try {
      ResultSet data = db.fetchAll("participant_data");
      while (data.next()) {
        Criteria d = new Criteria(data.getInt(1), data.getInt(2), data.getInt(3), data.getDouble(4), data.getDouble(5), data.getString(6), data.getString(7), data.getString(8));
        if (trial.getCriteria().matches(d)) {
          if (!db.matchExists("trial_matches", "trial_id", trial.getID(), "participant_id", data.getInt(2))) {
            //add this match
            //notify
          }
        }
      }
    } catch (SQLException e) {

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

  public static void stop() {
    app.stop();
    db.stop();
  }

}
