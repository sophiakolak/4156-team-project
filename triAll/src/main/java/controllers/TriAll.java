package controllers;

import io.javalin.Javalin;
import models.Criteria;
import models.Trial;
import models.User;
import units.SqliteDB;

class TriAll {
	
	private static final int PORT_NUMBER = 8080;

	private static Javalin app;
	
	private static User user;
	private static SqliteDB db;
	
	public static void main(String[] args) {
		
		db = new SqliteDB("triall");
		
		app = Javalin.create(config -> {
			config.addStaticFiles("/public");
		}).start(PORT_NUMBER);
		
		app.get("/", ctx -> {
			//ctx.redirect() homepage
			//this is dashboard if already logged in
		});
		
		app.get("/login-form", ctx -> {
			ctx.redirect("login.html");
		});
		
		app.post("/login-submit", ctx -> {
			//check username and password - OAuth
			//if valid, initialize User
			// and set username
			//if researcher, get trial ids in array
			//redirect to dashboard
		});
		
		app.get("/new-part-form", ctx -> {
			ctx.redirect("participantsignup.html");
		});
		
		app.get("/new-res-form", ctx -> {
			ctx.redirect("researchersignup.html");
		});
		
		app.post("/new-part-submit", ctx -> {
			float lat = ctx.formParam("latitude", Float.class).get();
			float lon = ctx.formParam("longitude", Float.class).get();
			String first = ctx.formParam("first");
			String last = ctx.formParam("last");
			String email = ctx.formParam("email");
			int id = db.insertUser("participants", lat, lon, first,  last, email);
			if(id == 0) {
				
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
        		  
        	  } else {
        		user.addTrial(new Trial(id, user, new Criteria(crit_id, age, height, weight, gender, race, nationality)));
        		ctx.redirect("researcherdashboard.html");
        	  }
        	}
		});
        
        app.get("/edit-part-form", ctx -> {
			//ctx.redirect() form
		});
		
		app.get("/edit-res-form", ctx -> {
			//ctx.redirect() form
		});
		
		app.post("/edit-part-submit", ctx -> {
			//barring issues, add to database
		});
		
		app.post("/edit-res-submit", ctx -> {
			//barring issues, add to database
		});
		
        app.get("/edit-trial-form", ctx -> {
			//ctx.redirect() form
		});
        
        app.post("/edit-trial-submit", ctx -> {
        	//barring issues, add to database
		});
        
        app.post("/logout", ctx -> {
        	
		});
	}
	
	public static void stop() {
	    app.stop();
	    db.stop();
	}

}