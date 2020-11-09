package controllers;

import io.javalin.Javalin;
import models.User;

class TriAll {
	
	private static final int PORT_NUMBER = 8080;

	private static Javalin app;
	
	private User user;
	
	public static void main(String[] args) {
		app = Javalin.create(config -> {
			config.addStaticFiles("/public");
		}).start(PORT_NUMBER);
		
		app.get("/", ctx -> {
			//ctx.redirect() homepage
			//this is dashboard if already logged in
		});
		
		app.get("/login-form", ctx -> {
			//ctx.redirect() form
		});
		
		app.post("/login-submit", ctx -> {
			//check username and password
			//if valid, initialize User as appropriate subclass
			// and set username
			//if researcher, get trial ids in array
			//redirect to dashboard
		});
		
		app.get("/new-part-form", ctx -> {
			//ctx.redirect() form
		});
		
		app.get("/new-res-form", ctx -> {
			//ctx.redirect() form
		});
		
		app.post("/new-part-submit", ctx -> {
			//barring issues, add to database
		});
		
		app.post("/new-res-submit", ctx -> {
			//barring issues, add to database
		});
		
        app.get("/new-trial-form", ctx -> {
			//ctx.redirect() form
		});
        
        app.post("/new-trial-submit", ctx -> {
        	//barring issues, add to database
		});
	}
	
	public static void stop() {
	    app.stop();
	}

}