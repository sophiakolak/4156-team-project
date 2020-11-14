package controllers;

import com.google.gson.Gson; 
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
			int part_id = db.insertUser("participants", lat, lon, first,  last, email);
			if(part_id == 0) {
				ctx.redirect("not_found.html");
			} else {
			  user = new User(part_id,lat,lon,first,last,email,false);
			  int age = ctx.formParam("age", Integer.class).get();
	          int height = ctx.formParam("height", Integer.class).get();
	          int weight = ctx.formParam("weight", Integer.class).get();
	          String gender = ctx.formParam("gender");
	          String race = ctx.formParam("race");
	          String nationality = ctx.formParam("nationality");
	          int crit_id = db.insertCriteria("participant_data", part_id, age, height, weight, gender, race, nationality);
	          if(crit_id == 0) {
	            ctx.redirect("not_found.html");	  
	          } else {
	        	user.setData(new Criteria(crit_id, part_id, age, height, weight, gender, race, nationality));
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
        	try {
        	int res_id = user.getID();
      	  	String desc = ctx.formParam("description");
      	  	float lat = ctx.formParam("latitude", Float.class).get();
      	  	float lon = ctx.formParam("longitude", Float.class).get();
      	  	String time = ctx.formParam("time");
      	  	int IRB = ctx.formParam("IRB", Integer.class).get();
      	  	int needed = ctx.formParam("participants_needed", Integer.class).get();
      	  	int confirmed = 0;
      	  	int trial_id = db.insertTrial("trials", res_id, desc, lat, lon, time, IRB, needed, confirmed);
      	  	if(trial_id==0) {
      	  		ctx.redirect("not_found.html");
      	  	} 
      	  	else {
      	  		int age = ctx.formParam("age", Integer.class).get();
      	  		int height = ctx.formParam("height", Integer.class).get();
      	  		int weight = ctx.formParam("weight", Integer.class).get();
      	  		String gender = ctx.formParam("gender");
      	  		String race = ctx.formParam("race");
      	  		String nationality = ctx.formParam("nationality");
      	  		int crit_id = db.insertCriteria("trial_criteria", trial_id, age, height, weight, gender, race, nationality);
      	  		if(crit_id==0) {
      	  		    ctx.redirect("not_found.html");
      	  		}
      	  		else {
      	  			user.addTrial(new Trial(trial_id, user, desc, lat, lon, time, IRB, needed, confirmed,
      	  					new Criteria(crit_id, trial_id, age, height, weight, gender, race, nationality)));
      	  			ctx.redirect("researcherdashboard.html");
      	  		}
      	  	}
      	  	
        	} catch (Exception e) {
        		System.out.println("user does not exist");
        		ctx.redirect("not_found.html");
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
        	ResultSet trial_rs = db.fetchOne("trials", "ID", IDString);
        	int trial_id = trial_rs.getInt(1);
        	int res_id = trial_rs.getInt(2);
        	String desc = trial_rs.getString(3);
        	float lat = trial_rs.getFloat(4);
        	float lon = trial_rs.getFloat(5);
        	String time = trial_rs.getString(6);
        	int IRB = trial_rs.getInt(7);
        	int needed = trial_rs.getInt(8);
        	int confirmed = trial_rs.getInt(9);
        	ResultSet crit_rs = db.fetchOne("trials", "trial_ID", IDString);
        	int crit_id = crit_rs.getInt(1);
        	int age = crit_rs.getInt(3);
        	int height = crit_rs.getInt(4);
        	int weight = crit_rs.getInt(5);
        	String gender = crit_rs.getString(6);
        	String race = crit_rs.getString(7);
        	String nationality = crit_rs.getString(8);
        	
        	Trial t = new Trial(trial_id, user, desc, lat, lon, time, IRB, needed, confirmed,
	  					new Criteria(crit_id, trial_id, age, height, weight, gender, race, nationality));
        	Gson gson = new Gson(); 
        	String trialJson = gson.toJson(t);
        	
            ctx.result(trialJson);  
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