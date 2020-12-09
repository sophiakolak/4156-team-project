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


function loadTrial(id, name, desc, location, startDate, endDate, pay, IRB, partNeeded, partConfirmed, start){
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
    col2.append('<h5> &nbsp &nbsp' + location + '</h5>')
    cardHeader.append(col1, col2)
    card.append(cardHeader)
    var collapsableDiv = $('<div id="collapse'+id+'" class="collapse" aria-labelledby="heading'+id+'" data-parent="#trialAccordian">')
    cardBody = $('<div class="card-body row">')
    var c1 = $('<div class = "col-5">')
    var c2 = $('<div class = "col">')
    c1.append('<div class = "big"> <strong><i class="fa fa-calendar-o" aria-hidden="true"></i> End Date: </strong>' + monthEnd + '&nbsp' + dayEnd + ', ' + yearEnd + '</div>')
    c1.append('<div class = "big"> <strong><i class="fa fa-money" aria-hidden="true"></i> Pay:  </strong> $' + pay + ' per hour </div>')
    c1.append('<div class = "big"> <strong><i class="fa fa-check-square" aria-hidden="true"></i> IRB:  </strong>' + IRB + '</div>')
    c1.append('<br>')
    c1.append('<div class = "med"> <strong><i class="fa fa-info-circle" aria-hidden="true"></i> Description:  </strong>' + desc + '</div>')
    c1.append('<br>')
    c1.append('<div class = "small"> <strong><i class="fa fa-user" aria-hidden="true"></i> Participants Confirmed:  </strong>' + partConfirmed + '</div>')
    c1.append('<div class = "small"> <strong><i class="fa fa-users" aria-hidden="true"></i> Participants Needed:  </strong>' + partNeeded + '</div>')
    c2.append('<iframe width="650" height="400" frameborder="0" style="border:0" src="https://www.google.com/maps/embed/v1/directions?key=AIzaSyAYSwqY4yLII5q5a-fXGTdWy9uEBdBWPRo&origin='+ start +'&destination='+location+'" allowfullscreen></iframe>')
    cardBody.append(c1, c2)
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

function loadTrials(trialList, start) {
  if (trialList == "") {
    noTrials()
  } else {
    var trials = trialList.sort(sortByDate("start"))
    for (index = 0; index < trials.length; index++) { 
        trial = trials[index]
        var id = trial.id
        var name = trial.name
        var desc = trial.desc
        var location = trial.location
        var startDate = trial.start
        var endDate = trial.end
        var pay = trial.pay
        var IRB = trial.irb
        var part_needed = trial.partNeeded
        var part_confirmed = trial.partConfirmed
        var criteria = trial.crit
        loadTrial(id, name, desc, location, startDate, endDate, pay, IRB, part_needed, part_confirmed, start)
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
        $.ajax({
          type: "GET",
          url: "/edit-res-form",                
          dataType : "json",
          contentType: "application/json; charset=utf-8",
          success: function(result){
            $("#name").html(result.first)
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