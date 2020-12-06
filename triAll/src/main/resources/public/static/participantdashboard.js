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
  auth2.signOut().then(function () {
    $.ajax({
        type: "POST",
        url: "/logout",                
        dataType : "json",
        contentType: "application/json; charset=utf-8",
        data : JSON.stringify(email),
        success: function(result){
          window.location.href = result
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

function loadTrialUpcoming(id, name, desc, location, startDate, endDate, pay, IRB, partNeeded, partConfirmed, status, origin, destination){
    var origin = origin.split(' ').join('+')
    var destination = destination.split(' ').join('+')
    var map = $("<iframe width='100%' height='100%'' frameborder='0' style='border:0' src='https://www.google.com/maps/embed/v1/directions?key=AIzaSyAYSwqY4yLII5q5a-fXGTdWy9uEBdBWPRo&origin="+origin+"&destination="+destination+">")
    var card = $("<div class = 'card_container'>")
    var cardHeader = $('<div class="card-header" id="headingOne">')
    var h2 = $('<h2 class="mb-0">')
    var expandBtn = $('<button class="btn btn-link collapsed" type="button" data-toggle="collapse" data-target="#collapse'+id+'" aria-expanded="false" aria-controls="collapse'+id+'">')
    expandBtn.append("Start Date: ", startDate, ", ", "End Date: ", endDate, ", ", "Location: ", location)
    h2.append(expandBtn)
    cardHeader.append(h2)
    card.append(cardHeader)

    var collapsableDiv = $('<div id="collapse'+id+'" class="collapse" aria-labelledby="heading'+id+'" data-parent="#trialAccordianUpcoming">')
    cardBody = $('<div class="card-body">')
    cardBody.append("<div class = 'description'>Description: ", desc, "</div>", "<div class = 'irb'>IRB: ", IRB, "</div><br>", "<div class = 'pay'>Hourly Pay in USD: ", pay, "</div>")

    var acceptBtn = $('<button type="button" onclick="acceptMatch(' + id + ')" class="btn btn-primary acceptTrial">')
    acceptBtn.append("Accept")
    acceptBtn.attr('id', id)

    var rejectBtn = $('<button type="button" onclick="rejectMatch(' + id + ')" class="btn btn-danger rejectTrial">')
    rejectBtn.append("Reject")
    rejectBtn.attr('id', id)

    cardBody.append("<br>", acceptBtn, "   ", rejectBtn) 
    collapsableDiv.append(cardBody)
    card.append(collapsableDiv)
    $("#trialAccordianUpcoming").append(card)

}

function noUpcomingTrials() {
    // update upcoming trials view
    var card = $("<div class = 'card_container'>")
    var cardHeader = $('<div class="card-header" id="headingOne">')
    var h2 = $('<h2 class="mb-0">')
    var expandBtn = $('<button class="btn btn-link collapsed" type="button" data-toggle="collapse" data-target="#collapseOne" aria-expanded="false" aria-controls="collapseOne">')
    expandBtn.append("You have no upcoming trials. Get more matches by updating your information.")
    h2.append(expandBtn)
    cardHeader.append(h2)
    card.append(cardHeader)
    var collapsableDiv = $('<div id="collapseOne" class="collapse" aria-labelledby="headingOne" data-parent="#trialAccordianUpcoming">')
    cardBody = $('<div class="card-body">')
    cardBody.append("What are you waiting for? Update your information now. <br><br>")
    var editBtn = $('<button type="button" class="btn btn-primary" onclick="window.location.href=\'editparticipantinfo.html\'">')
    editBtn.append("Edit Information")
    cardBody.append(editBtn)
    collapsableDiv.append(cardBody)
    card.append(collapsableDiv)
    $("#trialAccordianUpcoming").append(card)
}

function noPendingTrials() {
    // update pending trials view
    var card = $("<div class = 'card_container'>")
    var cardHeader = $('<div class="card-header" id="headingOne">')
    var h2 = $('<h2 class="mb-0">')
    var expandBtn = $('<button class="btn btn-link collapsed" type="button" data-toggle="collapse" data-target="#collapseOne" aria-expanded="false" aria-controls="collapseOne">')
    expandBtn.append("You have no pending trials. Get more matches by updating your information.")
    h2.append(expandBtn)
    cardHeader.append(h2)
    card.append(cardHeader)
    var collapsableDiv = $('<div id="collapseOne" class="collapse" aria-labelledby="headingOne" data-parent="#trialAccordianPending">')
    cardBody = $('<div class="card-body">')
    cardBody.append("What are you waiting for? Update your information now. <br><br>")
    var editBtn = $('<button type="button" class="btn btn-primary" onclick="window.location.href=\'editparticipantinfo.html\'">')
    editBtn.append("Edit Information")
    cardBody.append(editBtn)
    collapsableDiv.append(cardBody)
    card.append(collapsableDiv)
    $("#trialAccordianPending").append(card)
}

function noUpcomingTrials() {
      // update upcoming trials view
    var card = $("<div class = 'card_container'>")
    var cardHeader = $('<div class="card-header" id="headingOne">')
    var h2 = $('<h2 class="mb-0">')
    var expandBtn = $('<button class="btn btn-link collapsed" type="button" data-toggle="collapse" data-target="#collapseOne" aria-expanded="false" aria-controls="collapseOne">')
    expandBtn.append("You have no upcoming trials. Scroll down to see your pending matches!")
    h2.append(expandBtn)
    cardHeader.append(h2)
    card.append(cardHeader)
    var collapsableDiv = $('<div id="collapseOne" class="collapse" aria-labelledby="headingOne" data-parent="#trialAccordianUpcoming">')
    cardBody = $('<div class="card-body">')
    cardBody.append("What are you waiting for? Update your information now. <br><br>")
    var editBtn = $('<button type="button" class="btn btn-primary" onclick="window.location.href=\'editparticipantinfo.html\'">')
    editBtn.append("Edit Information")
    cardBody.append(editBtn)
    collapsableDiv.append(cardBody)
    card.append(collapsableDiv)
    $("#trialAccordianUpcoming").append(card)
}

// Load html for when user has no matches 
function noTrials() {
    // update upcoming trials view
    var card = $("<div class = 'card_container'>")
    var cardHeader = $('<div class="card-header" id="headingOne">')
    var h2 = $('<h2 class="mb-0">')
    var expandBtn = $('<button class="btn btn-link collapsed" type="button" data-toggle="collapse" data-target="#collapseOne" aria-expanded="false" aria-controls="collapseOne">')
    expandBtn.append("You have no upcoming trials. Get more matches by updating your information.")
    h2.append(expandBtn)
    cardHeader.append(h2)
    card.append(cardHeader)
    var collapsableDiv = $('<div id="collapseOne" class="collapse" aria-labelledby="headingOne" data-parent="#trialAccordianUpcoming">')
    cardBody = $('<div class="card-body">')
    cardBody.append("What are you waiting for? Update your information now. <br><br>")
    var editBtn = $('<button type="button" class="btn btn-primary" onclick="window.location.href=\'editparticipantinfo.html\'">')
    editBtn.append("Edit Information")
    cardBody.append(editBtn)
    collapsableDiv.append(cardBody)
    card.append(collapsableDiv)
    $("#trialAccordianUpcoming").append(card)

    // update pending trials view
    var card = $("<div class = 'card_container'>")
    var cardHeader = $('<div class="card-header" id="headingOne">')
    var h2 = $('<h2 class="mb-0">')
    var expandBtn = $('<button class="btn btn-link collapsed" type="button" data-toggle="collapse" data-target="#collapseTwo" aria-expanded="false" aria-controls="collapseTwo">')
    expandBtn.append("You have no pending trials. Get more matches by updating your information.")
    h2.append(expandBtn)
    cardHeader.append(h2)
    card.append(cardHeader)
    var collapsableDiv = $('<div id="collapseTwo" class="collapse" aria-labelledby="headingTwo" data-parent="#trialAccordianPending">')
    cardBody = $('<div class="card-body">')
    cardBody.append("What are you waiting for? Update your information now. <br><br>")
    var editBtn = $('<button type="button" class="btn btn-primary" onclick="window.location.href=\'editparticipantinfo.html\'">')
    editBtn.append("Edit Information")
    cardBody.append(editBtn)
    collapsableDiv.append(cardBody)
    card.append(collapsableDiv)
    $("#trialAccordianPending").append(card)
}

// function to dynamically load in trials
function loadTrials(matchList) {
  if (matchList == "") {
    noTrials()
  } else {
    var matches = matchList
    var nPending = 0
    var nAccepted = 0
    // MAKE POST REQUEST TO GET ORIGIN
    $.ajax({
      type: "GET",
      url: "/edit-part-form",                
      dataType : "json",
      contentType: "application/json; charset=utf-8",
      success: function(result){
        var origin = result.location
        console.log(origin)
      },
      error: function(request, status, error){
          console.log("Error");
          console.log(request)
          console.log(status)
          console.log(error)
      }
    });

    for (index = 0; index < matches.length; index++) { 
        match = matches[index]
        var trial = match.trial
        var status = match.status
        var id = trial.id
        var desc = trial.desc
        var location = trial.location
        var startDate = trial.start
        var endDate = trial.end
        var pay = trial.pay
        var IRB = trial.irb
        var part_needed = trial.partNeeded
        var part_confirmed = trial.partConfirmed
        var criteria = trial.crit
        if (status == "accepted") {
          loadTrialUpcoming(id, name, desc, location, startDate, endDate, pay, IRB, part_needed, part_confirmed, status, origin, location)
          nAccepted++
        }
        else if (status == "rejected"){
          console.log("rejected trial " + id + " " + name)
        }
        else {
          // loadTrialPending(id, name, desc, location, startDate, endDate, pay, IRB, part_needed, part_confirmed, status, origin, location)
          loadTrialUpcoming(id, name, desc, location, startDate, endDate, pay, IRB, part_needed, part_confirmed, status, origin, location)
          nPending++
        }
        
    }
    if (nPending == 0) {
      noPendingTrials()
    }
  }
}

function acceptMatch(trialId){
  alert(trialId)
  $.ajax({
        type: "POST",
        url: "/accept-match",                
        dataType : "json",
        contentType: "application/json; charset=utf-8",
        data : JSON.stringify(trialId),
        success: function(result){
          location.reload();
        },
        error: function(request, status, error){
            console.log("Error");
            console.log(request)
            console.log(status)
            console.log(error)
        }
  });
}

function rejectMatch(trialId){
  $.ajax({
        type: "POST",
        url: "/reject-match",                
        dataType : "json",
        contentType: "application/json; charset=utf-8",
        data : JSON.stringify(trialId),
        success: function(result){
          location.reload();
        },
        error: function(request, status, error){
            console.log("Error");
            console.log(request)
            console.log(status)
            console.log(error)
        }
  });
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