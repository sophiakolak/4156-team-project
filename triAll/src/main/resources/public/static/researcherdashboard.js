// Check if user is signed in 
gapi.load('auth2', function() {
  gapi.auth2.init({
    client_id: '46819195782-rhbp0ull70okmgsid0rrd2p8cdub7fpn.apps.googleusercontent.com',
  }).then(function(){
    auth2 = gapi.auth2.getAuthInstance();
    var signedIn = auth2.isSignedIn.get()
    if (!signedIn) {window.location.href = "/"}   
  });
});


// sign out finction
function signOut() {
    var auth2 = gapi.auth2.getAuthInstance();
    var profile = auth2.currentUser.get().getBasicProfile();
    var email = profile.getEmail();
    alert(email)
    auth2.signOut().then(function () {
      $.ajax({
          type: "POST",
          url: "/logout",                
          dataType : "json",
          contentType: "application/json; charset=utf-8",
          data : JSON.stringify(email),
          success: function(result){
            alert( "Logged out!" );
          },
          error: function(request, status, error){
              console.log("Error");
              console.log(request)
              console.log(status)
              console.log(error)
          }
      });
    });
  }

// Add function to load in trials
              // <div class="card">
              //   <div class="card-header" id="headingOne">
              //     <h2 class="mb-0">
              //       <button class="btn btn-link collapsed" type="button" data-toggle="collapse" data-target="#collapseOne" aria-expanded="false" aria-controls="collapseOne">
              //         1. Death Trial - Tuesday, September 58th, 2020 - 2 miles away
              //       </button>
              //     </h2>
              //   </div>

              //   <div id="collapseOne" class="collapse" aria-labelledby="headingOne" data-parent="#trialAccordian">
              //     <div class="card-body">
              //       University of Something <br>
              //       Trial Description <br>
              //       OTher stuff <br>
              //       text teest <br>
              //       i love code <br>

              //       <button type="button" class="btn btn-primary editTrial">Edit</button>
              //     </div>
              //   </div>
              // </div>

function editTrial(id){
  route = "/edit-trial-form/" + id
  console.log("calling editTrial")
$.ajax({
      type: "GET",
      url: route,                
      dataType : "json",
      contentType: "application/json; charset=utf-8",
      success: function(trial){
        // ??
        console.log(trial)
        sessionStorage.setItem('trial', trial);
        window.location.href = "edittrial.html"
      },
      error: function(request, status, error){
          console.log("Error");
          console.log(request)
          console.log(status)
          console.log(error)
      }
    });
}

function loadTrial(id, researcher, desc, lat, lon, start_date, end_date, pay, IRB, part_needed, part_confirmed){
    var card = $("<div class = 'card_container'>")
    var cardHeader = $('<div class="card-header" id="headingOne">')
    var h2 = $('<h2 class="mb-0">')
    var expandBtn = $('<button class="btn btn-link collapsed" type="button" data-toggle="collapse" data-target="#collapseOne" aria-expanded="false" aria-controls="collapseOne">')
    expandBtn.append("TEST TEST", start_date, "<br>", end_date, "<br>", location)
    h2.append(expandBtn)
    cardHeader.append(h2)
    card.append(cardHeader)

    var collapsableDiv = $('<div id="collapseOne" class="collapse" aria-labelledby="headingOne" data-parent="#trialAccordian">')
    cardBody = $('<div class="card-body">')
    cardBody.append("Description: ", desc, "<br>", "IRB: ", IRB, "<br>", "Participants Needed: ", part_needed, "<br>", "Participants Confirmed: ", part_confirmed, "<br>", "Hourly Pay in USD: ", pay)
    var editBtn = $('<button type="button" class="btn btn-primary editTrial" onclick=editTrial(' + id +')>')
    editBtn.append("Edit")
    editBtn.attr('id', id)
    cardBody.append(editBtn)
    collapsableDiv.append(cardBody)
    card.append(collapsableDiv)
    $("#trialAccordian").append(card)
}

function noTrials() {
    var card = $("<div class = 'card_container'>")
    var cardHeader = $('<div class="card-header" id="headingOne">')
    var h2 = $('<h2 class="mb-0">')
    var expandBtn = $('<button class="btn btn-link collapsed" type="button" data-toggle="collapse" data-target="#collapseOne" aria-expanded="false" aria-controls="collapseOne">')
    expandBtn.append("You have no upcoming trials. Create one by clicking \"New Trial\".")
    h2.append(expandBtn)
    cardHeader.append(h2)
    card.append(cardHeader)
    var collapsableDiv = $('<div id="collapseOne" class="collapse" aria-labelledby="headingOne" data-parent="#trialAccordian">')
    cardBody = $('<div class="card-body">')
    cardBody.append("What are you waiting for? Create a trial now!")
    var editBtn = $('<button type="button" class="btn btn-primary" onclick="window.location.href=\'newtrial.html\'">')
    editBtn.append("New Trial")
    cardBody.append(editBtn)
    collapsableDiv.append(cardBody)
    card.append(collapsableDiv)
    $("#trialAccordian").append(card)
}

function loadTrials(trialList) {
  if (trialList == "") {
    noTrials()
  } else {
    var trials = trialList
    console.log("TRIALS: " + trials)

    for (index = 0; index < trials.length; index++) { 
        trial = trials[index]
        console.log("trial: " + trial)
        var id = trial.id
        var researcher = trial.researcher
        var desc = trial.desc
        var location = trial.location
        var startDate = trial.start_date
        var endDate = trial.end_date
        var pay = trial.pay
        var IRB = trial.IRB
        var part_needed = trial.part_needed
        var part_confirmed = trial.part_confirmed
        loadTrial(id, researcher, desc, location, startDate, endDate, pay, IRB, part_needed, part_confirmed)
    } 
  }
}


$(document).ready(function(){

  // Get request
  $.ajax({
      type: "GET",
      url: "/researcher-dashboard",                
      dataType : "json",
      contentType: "application/json; charset=utf-8",
      success: function(trials){
        loadTrials(trials)
      },
      error: function(request, status, error){
          console.log("Error");
          console.log(request)
          console.log(status)
          console.log(error)
      }
    });
})