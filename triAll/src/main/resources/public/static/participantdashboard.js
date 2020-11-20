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

// Load html for a single trial
function loadTrial(id, researcher, desc, lat, lon, start_date, end_date, pay, IRB){
    var card = $("<div class = 'card_container'>")
    var cardHeader = $('<div class="card-header" id="headingOne">')
    var h2 = $('<h2 class="mb-0">')
    var expandBtn = $('<button class="btn btn-link collapsed" type="button" data-toggle="collapse" data-target="#collapseOne" aria-expanded="false" aria-controls="collapseOne">')
    expandBtn.append(start_date, "<br>", end_date, "<br>", location)
    h2.append(expandBtn)
    cardHeader.append(h2)
    card.append(cardHeader)

    var collapsableDiv = $('<div id="collapseOne" class="collapse" aria-labelledby="headingOne" data-parent="#trialAccordian">')
    cardBody = $('<div class="card-body">')
    cardBody.append("Description: ", desc, "<br>", IRB, part_needed, part_confirmed, pay)
    var acceptBtn = $('<button type="button" onclick="window.location.href=\'/accept-match:\'' + id + '" class="btn btn-primary acceptTrial">')
    acceptBtn.append("Accept")
    acceptBtn.attr('id', id)
    var rejectBtn = $('<button type="button" onclick="window.location.href=\'/reject-match:\'' + id + '" class="btn btn-primary rejectTrial">')
    rejectBtn.append("Reject")
    rejectBtn.attr('id', id)
    cardBody.append(acceptBtn, rejectBtn)
    collapsableDiv.append(cardBody)
    card.append(collapsableDiv)
    $("#trialAccordian").append(card)
}

// Load html for when user has no matches
function noTrials() {
    var card = $("<div class = 'card_container'>")
    var cardHeader = $('<div class="card-header" id="headingOne">')
    var h2 = $('<h2 class="mb-0">')
    var expandBtn = $('<button class="btn btn-link collapsed" type="button" data-toggle="collapse" data-target="#collapseOne" aria-expanded="false" aria-controls="collapseOne">')
    expandBtn.append("You have no matched trials. Get more matches by updating your information.")
    h2.append(expandBtn)
    cardHeader.append(h2)
    card.append(cardHeader)
    var collapsableDiv = $('<div id="collapseOne" class="collapse" aria-labelledby="headingOne" data-parent="#trialAccordian">')
    cardBody = $('<div class="card-body">')
    cardBody.append("What are you waiting for? Update your information now.")
    var editBtn = $('<button type="button" class="btn btn-primary" onclick="window.location.href=\'editparticipantinfo.html\'">')
    editBtn.append("Edit Information")
    cardBody.append(editBtn)
    collapsableDiv.append(cardBody)
    card.append(collapsableDiv)
    $("#trialAccordian").append(card)
}

// function to dynamically load in trials
function loadTrials(trialList) {
  if (trialList == "") {
    noTrials()
  } else {
    var trials = JSON.parse(trialList)
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
        loadTrial(id, researcher, desc, lat, lon, start_date, end_date, pay, IRB)
    } 
  }
}

$(document).ready(function(){

  // Get request
  $.ajax({
      type: "GET",
      url: "/participant-dashboard",                
      dataType : "json",
      contentType: "application/json; charset=utf-8",
      success: function(result){
        loadTrials(result)
      },
      error: function(request, status, error){
          console.log("Error");
          console.log(request)
          console.log(status)
          console.log(error)
      }
    });
})