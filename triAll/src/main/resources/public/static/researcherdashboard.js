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
          newAlert("Success!", "You have succesfully signed out of TriAll.", result)
        },
        error: function(request, status, error){
            newAlert("Oh no!", "Something went wrong. Please contact clinicaltriall@aol.com for more information.", "/participantdashboard.html")
            console.log("Error");
            console.log(request)
            console.log(status)
            console.log(error)
        }
    });
  });
}

function newAlert(title, text, redirect){
  var alert = $('<div class="modal fade" id="alert" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">')
  var alert1 = $('<div class="modal-dialog modal-dialog-centered" role="document">')
  var alert2 = $('<div class="modal-content">')
  var alert3 = $('<div class="modal-header">')
  var alert4 = $('<h5 class="modal-title" id="exampleModalLongTitle">')
  alert4.append(title)
  var alert5 = $('<div class="modal-body">')
  alert5.append(text)
  var alert6 = $('<div class="modal-footer">')
  var alert7 = ('<button type="button" onclick=\"location.href=\''+redirect+'\'\" class="btn btn-primary">Continue</button>')
  alert6.append(alert7)
  alert3.append(alert4)
  alert2.append(alert3, alert5, alert6)
  alert1.append(alert2)
  alert.append(alert1)
  $("#modal").html(alert)
  $('#alert').modal('toggle')
}

function loadTrial(id, name, desc, location, startDate, endDate, pay, IRB, partNeeded, partConfirmed){
    var totalPart = partNeeded + partConfirmed
    var progressWidth = partConfirmed/partNeeded
    var card = $("<div class = 'card_container'>")
    var cardHeader = $('<div class="card-header" id="headingOne">')
    var h2 = $('<h2 class="mb-0">')
    var expandBtn = $('<button class="btn btn-link collapsed" type="button" data-toggle="collapse" data-target="#collapse'+id+'" aria-expanded="false" aria-controls="collapse'+id+'">')
    expandBtn.append("Start Date: ", startDate, ", ", "End Date: ", endDate, ", ", "Location: ", location)
    h2.append(expandBtn)
    cardHeader.append(h2)
    card.append(cardHeader)

    var collapsableDiv = $('<div id="collapse'+id+'" class="collapse" aria-labelledby="heading'+id+'" data-parent="#trialAccordian">')
    cardBody = $('<div class="card-body">')
    cardBody.append("<div class = 'description'>Description: " +desc + "</div>", "<div class = 'irb'>IRB: "+  IRB,+ "</div>", "<div class = 'pay'>Hourly Pay in USD: "+ pay+"</div>")
    var progressContainer = $('<div class="progressContainer">')
    progressContainer.append("Participants Confirmed: "+partConfirmed+"<br>")
    progressContainer.append("Participants Needed: "+partNeeded+"<br>")
    var progress = $('<div class="progress">')
    var progressBody = $('<div class="progress-bar progress-bar-info" role="progressbar" aria-valuenow='+partConfirmed+' aria-valuemin="0" aria-valuemax='+totalPart+' style="width:'+progressWidth+'%">')
    progress.append(progressBody)
    progressContainer.append(progress)
    cardBody.append(progressContainer)
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
    cardBody.append("<br>", editBtn)
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
        var desc = trial.desc
        var location = trial.location
        var startDate = trial.start
        var endDate = trial.end
        var pay = trial.pay
        var IRB = trial.irb
        var part_needed = trial.partNeeded
        var part_confirmed = trial.partConfirmed
        var criteria = trial.crit
        loadTrial(id, name, desc, location, startDate, endDate, pay, IRB, part_needed, part_confirmed)
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