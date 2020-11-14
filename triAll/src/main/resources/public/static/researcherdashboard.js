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
        // get trial id
        var trial_id = 2
        window.location.href = "/edit/".concat(trial_id.toString()),   
    });
})