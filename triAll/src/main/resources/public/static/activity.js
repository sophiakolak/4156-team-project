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

function noMessages(){
    var card = $("<div class = 'card_container'>")
    var cardHeader = $('<div class="card-header" id="headingOne">')
    var h2 = $('<h2 class="mb-0">')
    var expandBtn = $('<button class="btn btn-link collapsed" type="button" data-toggle="collapse" data-target="#collapseOne" aria-expanded="false" aria-controls="collapseOne">')
    expandBtn.append("You have no messages.")
    h2.append(expandBtn)
    cardHeader.append(h2)
    card.append(cardHeader)
    var collapsableDiv = $('<div id="collapseOne" class="collapse" aria-labelledby="headingOne" data-parent="#msgAccordian">')
    cardBody = $('<div class="card-body">')
    cardBody.append("Update your information. Maybe you'll get some new messages!<br><br>")
    var editBtn = $('<button type="button" class="btn btn-primary" onclick="window.location.href=\'editparticipantinfo.html\'">')
    editBtn.append("Edit Information")
    cardBody.append(editBtn)
    collapsableDiv.append(cardBody)
    card.append(collapsableDiv)
    $("#msgAccordian").append(card)
}

function loadMessage(msg){
  var time = msg.time
  var content = msg.body
  var card = $("<div class = 'card_container'>")
  var cardHeader = $('<div class="card-header" id="headingOne">')
  var h2 = $('<h2 class="mb-0">')
  var expandBtn = $('<button class="btn btn-link collapsed" type="button" data-toggle="collapse" data-target="#collapseOne" aria-expanded="false" aria-controls="collapseOne">')
  expandBtn.append(time)
  h2.append(expandBtn)
  cardHeader.append(h2)
  card.append(cardHeader)
  var collapsableDiv = $('<div id="collapseOne" class="collapse" aria-labelledby="headingOne" data-parent="#msgAccordian">')
  cardBody = $('<div class="card-body">')
  cardBody.append(content + "<br><br>")
  collapsableDiv.append(cardBody)
  card.append(collapsableDiv)
  $("#msgAccordian").append(card)
}

function loadMessages(messages){
if (messages == "") {
    noMessages()
  }
  else {
    for (index = 0; index < messages.length; index++) { 
      msg = messages[index]
      loadMessage(msg)
    }
  }
}

$(document).ready(function(){

  // Get request
  $.ajax({
      type: "GET",
      url: "/activity",                
      dataType : "json",
      contentType: "application/json; charset=utf-8",
      success: function(result){
        loadMessages(result)
      },
      error: function(request, status, error){
          console.log("Error");
          console.log(request)
          console.log(status)
          console.log(error)
      }
    });
})