package controllers;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import io.javalin.Javalin;
import java.util.Collections;
import java.util.LinkedList;
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

  /**
   * Main method for the Clinical TriAll web server.
   * @param args Command-line arguments (none used in this program).
   */
  public static void main(String[] args) {

    db = new SqliteDB("triall");
    gson = new Gson();
    user = new User();


    app = Javalin.create(config -> {
      config.addStaticFiles("/public");
    }).start(PORT_NUMBER);

    app.get("/", ctx -> {
      if (user.isLoggedIn()) {
        if (user.isResearcher()) {
          ctx.redirect("/researcherdashboard.html");
        } else {
          ctx.redirect("/participantdashboard.html");
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
        int result = user.logIn(db, email);
        if (result == 0) {
          ctx.result(gson.toJson("/researcherdashboard.html")); 
        } else if (result == 1) {
          ctx.result(gson.toJson("/participantdashboard.html"));
        } else {
          ctx.result(gson.toJson("/not_found.html"));
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
      if (user.signUpPart(db, form)) {
        ctx.result(gson.toJson("/participantdashboard.html"));
      } else {
        ctx.result(gson.toJson("/not_found.html"));
      }
            
    });

    app.post("/new-res-submit", ctx -> {
      System.out.println(ctx.body());
      JsonArray form = gson.fromJson(ctx.body(), JsonArray.class);
      if (!user.signUpRes(db, form)) {
        ctx.redirect("not_found.html");
      } else {
        ctx.result(gson.toJson("/researcherdashboard.html"));
      }
    });

    app.get("/new-trial-form", ctx -> {
      ctx.redirect("newtrial.html");
    });

    app.post("/new-trial-submit", ctx -> {
      if (user.isLoggedIn() && user.isResearcher()) {
        System.out.println(ctx.body());
        JsonArray form = gson.fromJson(ctx.body(), JsonArray.class);
        Trial t = new Trial(db, form, user.getID());
        user.addTrial(t.getID(), t);
        ctx.result(gson.toJson("/researcherdashboard.html"));
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
      System.out.println(ctx.body());
      JsonArray form = gson.fromJson(ctx.body(), JsonArray.class);
      if (user.updatePart(db, form)) {
        ctx.result(gson.toJson("/participantdashboard.html"));
      } else {
        System.out.println(ctx.body());
        System.out.println("Not allowed");
      }
    });

    app.post("/edit-res-submit", ctx -> {
      JsonArray form = gson.fromJson(ctx.body(), JsonArray.class);
      if (user.updateRes(db, form)) {
        ctx.result(gson.toJson("/researcherdashboard.html"));
      } else {
        ctx.result(gson.toJson("/not_found.html"));
      }
    });

    app.get("/edit-trial-form/:trialId/", ctx -> {
      int trialID = ctx.pathParam("trialId", Integer.class).get();
      Trial t = user.getTrial(trialID);
      if (t != null) {
        String trialJson = gson.toJson(t);
        ctx.result(trialJson);  
        ctx.redirect("/edittrial.html");
      } else {
        //not allowed to access this trial
        ctx.redirect("not_found.html");
      }
    });

    app.post("/edit-trial-submit/:trialId/", ctx -> {
      int trialID = ctx.pathParam("trialId", Integer.class).get();
      JsonArray form = gson.fromJson(ctx.body(), JsonArray.class);
      if (user.updateTrial(trialID, db, form)) {
        ctx.result(gson.toJson("/researcherdashboard.html"));
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
      if (user.acceptMatch(trialID, db)) {
        ctx.result(gson.toJson("/participantdashboard.html"));
      } else {
        ctx.redirect("not_found.html");
      }
    });

    app.post("/reject-match/", ctx -> {
      int trialID = Integer.parseInt(ctx.body());
      if (user.rejectMatch(trialID, db)) {
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

  /**
   * Parses email address from request body.
   * @param body Body of a request.
   * @return Email address.
   */
  public static String getEmail(String body) {
    String[] parts = body.split(":");
    if (parts.length > 1) {
      String[] email = parts[1].split("\"");
      return email[1];
    } else {
      return parts[0];
    }
  }

  /**
   * Implements OAuth authentication for the provided token.
   * @param body Request body.
   * @param email Email address.
   * @return Whether or not a valid authenticated token (for the provided email) 
   *     was contained in the request body.
   */
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
