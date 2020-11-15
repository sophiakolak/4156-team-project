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

function loadTrial(id, researcher, desc, lat, lon, time, IRB, part_needed, part_confirmed){
    var card = $("<div class = 'card_container'>")
    var cardHeader = $('<div class="card-header" id="headingOne">')
    var h2 = $('<h2 class="mb-0">')
    var expandBtn = $('<button class="btn btn-link collapsed" type="button" data-toggle="collapse" data-target="#collapseOne" aria-expanded="false">')
    expandBtn.append(time)
    h2.append(expandBtn)
    cardHeader.append(h2)
    card.append(cardHeader)

    var collapsableDiv = $('<div id="collapseOne" class="collapse" data-parent="#trialAccordian">')
    cardBody = $('<div class="card-body">')
    cardBody.append("Description: ", desc, "<br>", lat, lon, time, IRB, part_needed, part_confirmed)
    var editBtn = $('<button type="button" class="btn btn-primary editTrial">')
    editBtn.append("Edit")
    editBtn.attr('id', id)
    cardBody.append(editBtn)
    collapsibleDiv.append(cardBody)
    card.append(collapsibleDiv)
    $("#trialAccordian").append(card)
}


$(document).ready(function(){
  // Add function to load in trials
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
    $(".editTrial").click(function( event ) {
        var trial_id = event.target.id
        window.location.href = "/edit/:".concat(trial_id.toString())   
    });
})