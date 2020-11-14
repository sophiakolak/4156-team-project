package controllers;

import io.javalin.Javalin;
import models.Criteria;
import models.Trial;
import models.User;
import units.SqliteDB;
import java.sql.ResultSet;

class TriAll {
	
//	Sign in with google is currently only authorized to use port 8000 but we can add more
	private static final int PORT_NUMBER = 8000;

	private static Javalin app;
	
	private static User user;
	private static SqliteDB db;
	
	public static void main(String[] args) {
		
		db = new SqliteDB("triall");
		
		app = Javalin.create(config -> {
			config.addStaticFiles("/public");
		}).start(PORT_NUMBER);
		
		app.get("/", ctx -> {
			ctx.redirect("/login.html") ;
			//update to homepage later
			//this is dashboard if already logged in
		});
		
		app.post("/login-submit", ctx -> {
			System.out.println("Signing in User");
			String body = ctx.body();
			System.out.println(body);
			// ctx.body() is json dictionary with email and key
			// authenticate
			
			// if user is participant
			// get trial matches
			// redirect to participant dashboard
			
			// if user is researcher
			// get trials
			// redirect to researcher dashboard
			
			// if user does not exist
			ctx.redirect("/signup");
		});
		
		app.get("/signup", ctx -> {
			ctx.redirect("/signup.html");
		});
		
		app.post("/new-part-submit", ctx -> {
			float lat = ctx.formParam("latitude", Float.class).get();
			float lon = ctx.formParam("longitude", Float.class).get();
			// location will also be passed in as a form param - can we store that too?
			String first = ctx.formParam("first");
			String last = ctx.formParam("last");
			String email = ctx.formParam("email");
			int id = db.insertUser("participants", lat, lon, first,  last, email);
			if(id == 0) {
				ctx.redirect("not_found.html");
			} else {
			  user = new User(id,lat,lon,first,last,email,false);
			  int age = ctx.formParam("age", Integer.class).get();
	          int height = ctx.formParam("height", Integer.class).get();
	          int weight = ctx.formParam("weight", Integer.class).get();
	          String gender = ctx.formParam("gender");
	          String race = ctx.formParam("race");
	          String nationality = ctx.formParam("nationality");
	          int crit_id = db.insertCriteria("participant_data", id, age, height, weight, gender, race, nationality);
	          if(crit_id == 0) {
	            ctx.redirect("not_found.html");	  
	          } else {
	        	user.setData(new Criteria(crit_id, age, height, weight, gender, race, nationality));
	        	ctx.redirect("participantdashboard.html");  
	          }
			}
		});
		
		app.post("/new-res-submit", ctx -> {
			float lat = ctx.formParam("latitude", Float.class).get();
			float lon = ctx.formParam("longitude", Float.class).get();
			String first = ctx.formParam("first");
			String last = ctx.formParam("last");
			String email = ctx.formParam("email");
			int id = db.insertUser("researchers", lat, lon, first,  last, email);
			if(id == 0) {
			    ctx.redirect("not_found.html");
			}
			else {
				user = new User(id,lat,lon,first,last,email,true);
				ctx.redirect("researcherdashboard.html");
			}
		});
		
        app.get("/new-trial-form", ctx -> {
			ctx.redirect("newtrial.html");
		});
        
        app.post("/new-trial-submit", ctx -> {
        	int age = ctx.formParam("age", Integer.class).get();
        	int height = ctx.formParam("height", Integer.class).get();
        	int weight = ctx.formParam("weight", Integer.class).get();
        	String gender = ctx.formParam("gender");
        	String race = ctx.formParam("race");
        	String nationality = ctx.formParam("nationality");
        	int crit_id = db.insertCriteria("trial_criteria", 0, age, height, weight, gender, race, nationality);
        	if(crit_id==0) {
        	    ctx.redirect("not_found.html");
        	} else {
        	  int res_id = user.getID();
        	  String desc = ctx.formParam("description");
        	  float lat = ctx.formParam("latitude", Float.class).get();
        	  float lon = ctx.formParam("longitude", Float.class).get();
        	  String time = ctx.formParam("time");
        	  int IRB = ctx.formParam("IRB", Integer.class).get();
        	  int needed = ctx.formParam("participants_needed", Integer.class).get();
        	  int confirmed = 0;
        	  int id = db.insertTrial("trials", res_id, desc, lat, lon, time, IRB, crit_id, needed, confirmed);
        	  if(id==0) {
        		  ctx.redirect("not_found.html");
        	  } else {
        		user.addTrial(new Trial(id, user, new Criteria(crit_id, age, height, weight, gender, race, nationality)));
        		ctx.redirect("researcherdashboard.html");
        	  }
        	}
		});
        
        app.get("/edit-part-form", ctx -> {
			ctx.redirect("/editparticipantinfo.html"); 
		});
		
		app.get("/edit-res-form", ctx -> {
			ctx.redirect("/editresearcherinfo.html"); 
		});
		
		app.post("/edit-part-submit", ctx -> {
			
			//barring issues, add to database
		});
		
		app.post("/edit-res-submit", ctx -> {
			//barring issues, add to database
		});
		
        app.get("/edit-trial-form/:trialId/", ctx -> {
        	String IDString = ctx.pathParam("trialId");
        	ResultSet s = db.fetchOne("trials", IDString);
        	System.out.println(s);
        	//lookup all data related to that trial from db 
        	//make java object 
        	//convert to json 
        	//send to front-end
        	
			ctx.redirect("edittrial.html");
			
		});
        
        app.post("/edit-trial-submit/:trialId/", ctx -> {
        	
        	//update db
        	
		});
        
        app.post("/logout", ctx -> {
//        	ctx.body() contains email of user to be logged out
        	System.out.println("Logging out user");
        	ctx.redirect("/");        	
		});
	}
	
	public static void stop() {
	    app.stop();
	    db.stop();
	}

}