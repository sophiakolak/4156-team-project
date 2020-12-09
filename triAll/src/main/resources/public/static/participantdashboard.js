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
            newAlert("Oh no!", "Something went wrong. Please contact clinicaltriall@aol.com for more information.", "/")
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
  if (redirect == "/participantdashboard.html")
    var alert7 = ('<button type="button" onclick=\"location.reload();\" class="btn btn-primary">Continue</button>')
  else
    var alert7 = ('<button type="button" onclick=\"location.href=\''+redirect+'\'\" class="btn btn-primary">Continue</button>')
  alert6.append(alert7)
  alert3.append(alert4)
  alert2.append(alert3, alert5, alert6)
  alert1.append(alert2)
  alert.append(alert1)
  $("#modal").html(alert)
  $('#alert').modal('toggle')
}

function sortByDate(date){  
   return function(a,b){  
      aDate = a[date]
      bDate = b[date]
      aParts = aDate.split('-')
      bParts = bDate.split('-')
      if(parseInt(aParts[0]) > parseInt(bParts[0]))  
         return 1;  
      else if(parseInt(aParts[0]) < parseInt(bParts[0]))  
        return -1
      else if(parseInt(aParts[1]) > parseInt(bParts[1]))  
         return 1;  
      else if(parseInt(aParts[1]) < parseInt(bParts[1]))  
         return -1;  
      else if(parseInt(aParts[2]) > parseInt(bParts[2]))  
         return 1;  
      else if(parseInt(aParts[2]) < parseInt(bParts[2]))  
         return -1;  
      return 0;  
   }  
}

function sortByDistance(param){  
   return function(a,b){  
      aDist = a[param]
      bDist = b[param]
      if(parseInt(aDist) > parseInt(bDist))  
         return 1; 
      else if(parseInt(aDist) < parseInt(bDist)) 
         return -1
      return 0;  
   }  
}

function loadTrialPending(id, name, desc, loc, startDate, endDate, pay, IRB, start, distance){
    var dist = parseFloat(distance)
    var distRounded = Math.round(dist * 10) / 10;
    var monthNamesLong = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"]
    dateParts = startDate.split('-')
    var month_i = dateParts[1]
    var month = monthNamesLong[parseInt(month_i)-1]
    var day = dateParts[2]
    var year = dateParts[0]

    dateParts = endDate.split('-')
    month_i = dateParts[1]
    var monthEnd = monthNamesLong[parseInt(month_i)-1]
    var dayEnd = dateParts[2]
    var yearEnd = dateParts[0]

    var card = $("<div class = 'card_container'>")
    var cardHeader = $('<div class="card-header row row-row striped" id="headingOne">')
    var col1 = $('<div class="col-2">')
    col1.append('<h3 class="display-4"><span class="badge badge-secondary">'+distRounded+' </span></h3>')
    col1.append('<h5>miles away</h5>')
    var col2 = $('<div class="col-6">')
    col2.append('<button class="btn btn-link collapsed" type="button" data-toggle="collapse" data-target="#collapse'+id+'" aria-expanded="false" aria-controls="collapse'+id+'"><h2 class="text-uppercase cardHead"><strong>'+name+'</strong></h2></button>')
    col2.append('<h5> &nbsp &nbsp' + loc + '</h5>')
    cardHeader.append(col1, col2)
    card.append(cardHeader)
    var collapsableDiv = $('<div id="collapse'+id+'" class="collapse" aria-labelledby="heading'+id+'" data-parent="#trialAccordianPending">')
    cardBody = $('<div class="card-body row">')
    var c1 = $('<div class = "col-5">')
    var c2 = $('<div class = "col">')
    c1.append('<div class = "big"> <strong><i class="fa fa-calendar-o" aria-hidden="true"></i> Start Date: </strong>' + month + '&nbsp' + day + ', ' + year + '</div>')
    c1.append('<div class = "big"> <strong><i class="fa fa-calendar-o" aria-hidden="true"></i> End Date: </strong>' + monthEnd + '&nbsp' + dayEnd + ', ' + yearEnd + '</div>')
    c1.append('<div class = "big"> <strong><i class="fa fa-money" aria-hidden="true"></i> Pay:  </strong> $' + pay + ' per hour </div>')
    c1.append('<div class = "big"> <strong><i class="fa fa-check-square" aria-hidden="true"></i> IRB:  </strong>' + IRB + '</div>')
    c1.append('<br>')
    c1.append('<div class = "med"> <strong><i class="fa fa-info-circle" aria-hidden="true"></i> Description:  </strong>' + desc + '</div>')
    c1.append('<br>')
    var acceptBtn = $('<button type="button" onclick="acceptMatch(' + id + ')" class="btn btn-primary acceptTrial">')
    acceptBtn.append("Accept")
    acceptBtn.attr('id', id)

    var rejectBtn = $('<button type="button" onclick="rejectMatch(' + id + ')" class="btn btn-danger rejectTrial">')
    rejectBtn.append("Reject")
    rejectBtn.attr('id', id)

    c1.append(acceptBtn, " ", rejectBtn)
    
    c2.append('<iframe width="650" height="400" frameborder="0" style="border:0" src="https://www.google.com/maps/embed/v1/directions?key=AIzaSyAYSwqY4yLII5q5a-fXGTdWy9uEBdBWPRo&origin='+ start +'&destination='+loc+'" allowfullscreen></iframe>')
    cardBody.append(c1, c2)
    collapsableDiv.append(cardBody)
    card.append(collapsableDiv)
    $("#trialAccordianPending").append(card)

}

function loadTrialUpcoming(id, name, desc, loc, startDate, endDate, pay, IRB, start){
    var monthNames = ["JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"]
    var monthNamesLong = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"]
    dateParts = startDate.split('-')
    var month_i = dateParts[1]
    var month = monthNames[parseInt(month_i)-1]
    var day = dateParts[2]
    var year = dateParts[0]

    dateParts = endDate.split('-')
    month_i = dateParts[1]
    var monthEnd = monthNamesLong[parseInt(month_i)-1]
    var dayEnd = dateParts[2]
    var yearEnd = dateParts[0]

    var card = $("<div class = 'card_container'>")
    var cardHeader = $('<div class="card-header row row-row striped" id="headingOne">')
    var col1 = $('<div class="col-2">')
    col1.append('<h3 class="display-4"><span class="badge badge-secondary">'+day+'</span></h3>')
    col1.append('<h5>'+month+'&nbsp '+ year +'</h5>')
    var col2 = $('<div class="col-6">')
    col2.append('<button class="btn btn-link collapsed" type="button" data-toggle="collapse" data-target="#collapse'+id+'" aria-expanded="false" aria-controls="collapse'+id+'"><h2 class="text-uppercase cardHead"><strong>'+name+'</strong></h2></button>')
    col2.append('<h5> &nbsp &nbsp' + loc + '</h5>')
    cardHeader.append(col1, col2)
    card.append(cardHeader)
    var collapsableDiv = $('<div id="collapse'+id+'" class="collapse" aria-labelledby="heading'+id+'" data-parent="#trialAccordianUpcoming">')
    cardBody = $('<div class="card-body row">')
    var c1 = $('<div class = "col-5">')
    var c2 = $('<div class = "col">')
    c1.append('<div class = "big"> <strong><i class="fa fa-calendar-o" aria-hidden="true"></i> End Date: </strong>' + monthEnd + '&nbsp' + dayEnd + ', ' + yearEnd + '</div>')
    c1.append('<div class = "big"> <strong><i class="fa fa-money" aria-hidden="true"></i> Pay:  </strong> $' + pay + ' per hour </div>')
    c1.append('<div class = "big"> <strong><i class="fa fa-check-square" aria-hidden="true"></i> IRB:  </strong>' + IRB + '</div>')
    c1.append('<br>')
    c1.append('<div class = "med"> <strong><i class="fa fa-info-circle" aria-hidden="true"></i> Description:  </strong>' + desc + '</div>')
    c1.append('<br>')
    var acceptBtn = $('<button type="button" onclick="acceptMatch(' + id + ')" class="btn btn-primary acceptTrial">')
    acceptBtn.append("Accept")
    acceptBtn.attr('id', id)

    var rejectBtn = $('<button type="button" onclick="rejectMatch(' + id + ')" class="btn btn-danger rejectTrial">')
    rejectBtn.append("Reject")
    rejectBtn.attr('id', id)

    c1.append(acceptBtn, rejectBtn)
    
    c2.append('<iframe width="650" height="400" frameborder="0" style="border:0" src="https://www.google.com/maps/embed/v1/directions?key=AIzaSyAYSwqY4yLII5q5a-fXGTdWy9uEBdBWPRo&origin='+ start +'&destination='+loc+'" allowfullscreen></iframe>')
    cardBody.append(c1, c2)
    collapsableDiv.append(cardBody)
    card.append(collapsableDiv)
    $("#trialAccordianUpcoming").append(card)

}

function noUpcomingTrials() {
    // update upcoming trials view
    var card = $("<div class = 'card_container'>")
    var cardHeader = $('<div class="card-header" id="headingOne">')
    var expandBtn = $('<button class="btn btn-link collapsed" type="button" data-toggle="collapse" data-target="#collapseOne" aria-expanded="false" aria-controls="collapseOne">')
    expandBtn.append('<h5 class="cardHead"> You have no upcoming trials. Get more matches by updating your information.</h5>')
    cardHeader.append(expandBtn)
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
    var cardHeader = $('<div class="card-header" id="headingTwo">')
    var expandBtn = $('<button class="btn btn-link collapsed" type="button" data-toggle="collapse" data-target="#collapseTwo" aria-expanded="false" aria-controls="collapseTwo">')
    expandBtn.append('<h5 class="cardHead"> You have no pending trials. Get more matches by updating your information.</h5>')
    cardHeader.append(expandBtn)
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
function loadTrials(matchList, start) {

  if (matchList == "") {
    noUpcomingTrials()
    noPendingTrials()
  } else {
    var matches = matchList.sort(sortByDate("start"))
    matches
    var nPending = 0
    var pendingMatches = []
    var nAccepted = 0

    for (index = 0; index < matches.length; index++) { 
        match = matches[index]
        var trial = match.trial
        var status = match.status
        var id = trial.id
        var name = trial.name
        var desc = trial.desc
        var loc = trial.location
        var startDate = trial.start
        var endDate = trial.end
        var pay = trial.pay
        var IRB = trial.irb

        var criteria = trial.crit
        if (status == "accepted") {
          loadTrialUpcoming(id, name, desc, loc, startDate, endDate, pay, IRB, start)
          nAccepted++
        }
        else if (status == "rejected"){
          console.log("rejected trial " + id + " " + name)
        }
        else {
          pendingMatches.push(match)
          nPending++
        }
        
    }
    if (nAccepted == 0) {
      noUpcomingTrials()
    }
    if (nPending == 0) {
      noPendingTrials()
    }
    else{
      // sort pending arrays by distance
      pendingMatches = pendingMatches.sort(sortByDistance("distance"))
      for (index = 0; index < pendingMatches.length; index++){
        match = pendingMatches[index]
        var distance = match.distance
        var trial = match.trial
        var status = match.status
        var id = trial.id
        var name = trial.name
        var desc = trial.desc
        var loc = trial.location
        var startDate = trial.start
        var endDate = trial.end
        var pay = trial.pay
        var IRB = trial.irb
        var criteria = trial.crit
        loadTrialPending(id, name, desc, loc, startDate, endDate, pay, IRB, start, distance)
      }
    }
  }
}

function acceptMatch(trialId){
  $.ajax({
        type: "POST",
        url: "/accept-match",                
        dataType : "json",
        contentType: "application/json; charset=utf-8",
        data : JSON.stringify(trialId),
        success: function(result){
          newAlert("Success!", "You have accepted a trial match. A researcher will be in contact shortly.", "/participantdashboard.html")
        },
        error: function(request, status, error){
            newAlert("Oh no!", "Something went wrong. Please contact clinicaltriall@aol.com for more information.", "/participantdashboard.html")
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
          newAlert("Success!", "You have rejected a trial match. This trial will be removed from your pending matches the next time you log in.", "/participantdashboard.html")
        },
        error: function(request, status, error){
            newAlert("Oh no!", "Something went wrong. Please contact clinicaltriall@aol.com for more information.", "/participantdashboard.html")
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
      success: function(trials){
        $.ajax({
          type: "GET",
          url: "/edit-part-form",                
          dataType : "json",
          contentType: "application/json; charset=utf-8",
          success: function(result){
            $("#name").html(result.first)
            // loadTrialPending("3", "trial name", "dfasjklfads", "Ghent, NY", "2020-11-11", "2020-12-11", "10", "4444", "New York, NY", "3.6548")
            // loadTrialPending("4", "trial123 name", "dfasjklfads", "Ghent, NY", "2020-11-11", "2020-12-11", "10", "4444", "New York, NY", "5")
            loadTrials(trials, result.location)
          },
          error: function(request, status, error){
              newAlert("Oh no!", "Something went wrong. Please contact clinicaltriall@aol.com for more information.", "/")
              console.log("Error");
              console.log(request)
              console.log(status)
              console.log(error)
          }
        });
      },
      error: function(request, status, error){
          newAlert("Oh no!", "Something went wrong. Please contact clinicaltriall@aol.com for more information.", "/")
          console.log("Error");
          console.log(request)
          console.log(status)
          console.log(error)
      }
    });
})