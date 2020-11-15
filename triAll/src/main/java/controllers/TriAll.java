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

	private static Gson gson;
	private static Javalin app;
	
	private static User user;
	private static SqliteDB db;
	
	public static void main(String[] args) {
		
		db = new SqliteDB("triall");
		gson = new Gson();
		user = new User();
		
		app = Javalin.create(config -> {
			config.addStaticFiles("/public");
		}).start(PORT_NUMBER);
		
		app.get("/", ctx -> {
			if(user.isLoggedIn()) {
			  if(user.isResearcher()) {
				ctx.redirect("/researcher-dashboard");
			  } else {
				ctx.redirect("/participant-dashboard");
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
			String email = "";
			ResultSet rs = db.fetchString("participants", "email", email);
			if(!rs.next()) {
				rs = db.fetchString("researchers", "email", email);
				if(!rs.next()) {
					// user does not exist
					ctx.redirect("/signup");
				} else {
					//is researcher
					user = new User(rs.getInt(1), rs.getDouble(2), rs.getDouble(3), rs.getString(4), rs.getString(5), rs.getString(6), true);
					ResultSet trial_rs = db.fetchInt("trials", "researcher_ID", user.getID());
					while(trial_rs.next()) {
						ResultSet crit = db.fetchInt("trial_criteria", "trial_id", trial_rs.getInt(1));
						Criteria c = null;
						if(crit.next()) {
							c = new Criteria(crit.getInt(1), crit.getInt(2), crit.getInt(3), crit.getDouble(4), crit.getDouble(5), crit.getString(6), crit.getString(7), crit.getString(8));
						}
						user.addTrial(trial_rs.getInt(1), new Trial(trial_rs.getInt(1), user, trial_rs.getString(3), trial_rs.getDouble(4), trial_rs.getDouble(5), trial_rs.getString(6), trial_rs.getInt(7), trial_rs.getInt(8), trial_rs.getInt(9), c));
					}
					ctx.redirect("/researcher-dashboard");
				}
			} else {
				//is participant
				user = new User(rs.getInt(1), rs.getDouble(2), rs.getDouble(3), rs.getString(4), rs.getString(5), rs.getString(6), false);
				ResultSet data = db.fetchInt("participant_data", "participant_ID", user.getID());
				if(!data.next()) {
					//there is no data
				} else {
					user.setData(new Criteria(data.getInt(1), data.getInt(2), data.getInt(3), data.getDouble(4), data.getDouble(5), data.getString(6), data.getString(7), data.getString(8)));
					ResultSet match_rs = db.fetchInt("matches", "participant_ID", user.getID());
					while(match_rs.next()) {
						user.addMatch();
					}
				}
				ctx.redirect("/participant-dashboard");
			}
		});
		
		app.get("/signup", ctx -> {
			ctx.redirect("/signup.html");
		});
		
		app.post("/new-part-submit", ctx -> {
			double lat = ctx.formParam("latitude", Double.class).get();
			double lon = ctx.formParam("longitude", Double.class).get();
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
	        	ctx.redirect("/participantdashboard.html");  
	          }
			}
		});
		
		app.post("/new-res-submit", ctx -> {
			double lat = ctx.formParam("latitude", Double.class).get();
			double lon = ctx.formParam("longitude", Double.class).get();
			String first = ctx.formParam("first");
			String last = ctx.formParam("last");
			String email = ctx.formParam("email");
			int id = db.insertUser("researchers", lat, lon, first,  last, email);
			if(id == 0) {
			    ctx.redirect("not_found.html");
			} else {
				user = new User(id,lat,lon,first,last,email,true);
				ctx.redirect("/researcherdashboard.html");
			}
		});
		
        app.get("/new-trial-form", ctx -> {
			ctx.redirect("newtrial.html");
		});
        
        app.post("/new-trial-submit", ctx -> {
          if(user.isLoggedIn() && user.isResearcher()) {
        	int res_id = user.getID();
      	  	String desc = ctx.formParam("description");
      	  	double lat = ctx.formParam("latitude", Double.class).get();
      	  	double lon = ctx.formParam("longitude", Double.class).get();
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
      	  		} else {
      	  			user.addTrial(trial_id, new Trial(trial_id, user, desc, lat, lon, time, IRB, needed, confirmed,
      	  					new Criteria(crit_id, trial_id, age, height, weight, gender, race, nationality)));
      	  			ctx.redirect("/researcherdashboard.html");
      	  		}
      	  	}
          } else {
        	//not allowed to make a trial
          }
		});
        
        app.get("/edit-part-form", ctx -> {
        	if(!user.isLoggedIn()) {
				
			} else if(user.isResearcher()) {
				
			} else {
				String partJson = gson.toJson(user);
				ctx.result(partJson);
				ctx.redirect("/editparticipantinfo.html");
			} 
		});
		
		app.get("/edit-res-form", ctx -> {
			if(!user.isLoggedIn()) {
				
			} else if(!user.isResearcher()) {
				
			} else {
				String resJson = gson.toJson(user);
				ctx.result(resJson);
				ctx.redirect("/editresearcherinfo.html"); 
			}
		});
		
		app.post("/edit-part-submit", ctx -> {
		  if(user.isLoggedIn() && !user.isResearcher()) {
			double lat = ctx.formParam("latitude", Double.class).get();
			double lon = ctx.formParam("longitude", Double.class).get();
			// location will also be passed in as a form param - can we store that too?
			String first = ctx.formParam("first");
			String last = ctx.formParam("last");
			String email = ctx.formParam("email");
			int part_id = db.updateUser("participants", user.getID(), lat, lon, first,  last, email);
			if(part_id == 0) {
				ctx.redirect("not_found.html");
			} else {
			  user.update(lat,lon,first,last,email);
			  int age = ctx.formParam("age", Integer.class).get();
	          int height = ctx.formParam("height", Integer.class).get();
	          int weight = ctx.formParam("weight", Integer.class).get();
	          String gender = ctx.formParam("gender");
	          String race = ctx.formParam("race");
	          String nationality = ctx.formParam("nationality");
	          int crit_id = db.updateCriteria("participant_data", user.getData().getID(), part_id, age, height, weight, gender, race, nationality);
	          if(crit_id == 0) {
	            ctx.redirect("not_found.html");	  
	          } else {
	        	user.setData(new Criteria(crit_id, part_id, age, height, weight, gender, race, nationality));
	        	ctx.redirect("/participantdashboard.html");  
	          }
			}
		  } else {
			//not allowed
		  }
		});
		
		app.post("/edit-res-submit", ctx -> {
		  if(user.isLoggedIn() && user.isResearcher()) {
			double lat = ctx.formParam("latitude", Double.class).get();
			double lon = ctx.formParam("longitude", Double.class).get();
			String first = ctx.formParam("first");
			String last = ctx.formParam("last");
			String email = ctx.formParam("email");
			int id = db.updateUser("researchers", user.getID(), lat, lon, first,  last, email);
			if(id == 0) {
			    ctx.redirect("not_found.html");
			} else {
				user = new User(id,lat,lon,first,last,email,true);
				ctx.redirect("/researcherdashboard.html");
			}
		  } else {
			//not allowed
		  }
		});
		
        app.get("/edit-trial-form/:trialId/", ctx -> {
        	int trial_ID = ctx.pathParam("trialId", Integer.class).get();
        	/*ResultSet trial_rs = db.fetchOne("trials", "ID", IDString);
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
	  					new Criteria(crit_id, trial_id, age, height, weight, gender, race, nationality)); */
        	if(user.isResearcher()) {
        		Trial t = user.getTrial(trial_ID);
        		if(t != null) {
        			String trialJson = gson.toJson(t);
            	    ctx.result(trialJson);  
            		ctx.redirect("/edittrial.html");
        		} else {
        		  //not allowed to access this trial
        		}
        	}
        	
			
		});
        
        app.post("/edit-trial-submit/:trialId/", ctx -> {
        	int trial_ID = ctx.pathParam("trialId", Integer.class).get();
        	if(user.isLoggedIn() && user.containsTrial(trial_ID)) {
        		int res_id = user.getID();
          	  	String desc = ctx.formParam("description");
          	  	double lat = ctx.formParam("latitude", Double.class).get();
          	  	double lon = ctx.formParam("longitude", Double.class).get();
          	  	String time = ctx.formParam("time");
          	  	int IRB = ctx.formParam("IRB", Integer.class).get();
          	  	int needed = ctx.formParam("participants_needed", Integer.class).get();
          	  	int confirmed = 0;
          	  	int trial_id = db.updateTrial("trials", trial_ID, res_id, desc, lat, lon, time, IRB, needed, confirmed);
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
          	  		int crit_id = db.updateCriteria("trial_criteria", user.getTrial(trial_ID).getCriteria().getID(), trial_id, age, height, weight, gender, race, nationality);
          	  		if(crit_id==0) {
          	  		    ctx.redirect("not_found.html");
          	  		}
          	  		else {
          	  			user.addTrial(trial_id, new Trial(trial_id, user, desc, lat, lon, time, IRB, needed, confirmed,
          	  					new Criteria(crit_id, trial_id, age, height, weight, gender, race, nationality)));
          	  			ctx.redirect("researcherdashboard.html");
          	  		}
          	  	}
        	} else {
        		//does not have permission to access trial
        	}
        	
		});        
        
        
        app.post("/logout", ctx -> {
//        	ctx.body() contains email of user to be logged out
        	user.logOut();
        	System.out.println("Logging out user");
        	ctx.redirect("/");        	
		});
        
        //Routes added by sarah that we might need
        //If we don't need them please just delete them
        app.post("/accept-match/:trialId/", ctx -> {
        	// change trial status to accepted
		});
        
        app.post("/reject-match/:trialId/", ctx -> {
        	// change trial status to rejected
		});
        
        app.get("/researcher-dashboard", ctx -> {
        	if(user.isLoggedIn() && user.isResearcher()) {
        		//call sort on trials with date comparator, then send to frontend
        		ctx.redirect("researcherdashboard.html");
        	} else {
        	  ctx.redirect("/");
        	}
		});
        
        app.get("/participant-dashboard", ctx -> {
        	if(user.isLoggedIn() && !user.isResearcher()) {
        		// call sort on matches with distance comparator, then send to frontend
        		ctx.redirect("participantdashboard.html");
        	} else {
        	  ctx.redirect("/");
        	}
		});
        
	}
	
	public static void stop() {
	    app.stop();
	    db.stop();
	}

}
