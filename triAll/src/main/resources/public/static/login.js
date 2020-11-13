$(document).ready(function(){
    $('#participantForm').submit(function( event ) {
        alert( "Participant login form submitted! Add post request here" );
        //Also add validation that login and pword are correct
        event.preventDefault();
        window.location.href = "participantdashboard.html"
    });
    $('#researcherForm').submit(function( event ) {
        alert( "Researcher login form submitted! Add post request here" );
        //Also add validation that login and pword are correct
        event.preventDefault();
        window.location.href = "researcherdashboard.html"
    });
})

function onSignIn(googleUser) {
    var id_token = googleUser.getAuthResponse().id_token;
    var profile = googleUser.getBasicProfile();
    var email = profile.getEmail(); // This is null if the 'email' scope is not present.

    var user_credentials = {
        "email": email,
        "id_token": id_token
    }

    $.ajax({
        type: "POST",
        url: "/login-submit",                
        dataType : "json",
        contentType: "application/json; charset=utf-8",
        data : JSON.stringify(user_credentials),
        success: function(result){
          alert(result)
          alert( "Saved Changes" );
        },
        error: function(request, status, error){
            console.log("Error");
            console.log(request)
            console.log(status)
            console.log(error)
        }
    });
}

function signOut() {
  var auth2 = gapi.auth2.getAuthInstance();
  var profile = auth2.currentUser.get().getBasicProfile();
  var email = profile.getEmail();

  var user_credentials = {
    "email": email
  }

  auth2.signOut().then(function () {
    $.ajax({
        type: "POST",
        url: "/logout",                
        dataType : "json",
        contentType: "application/json; charset=utf-8",
        data : JSON.stringify(user_credentials),
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

