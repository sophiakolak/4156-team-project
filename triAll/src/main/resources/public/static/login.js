setTimeout(function () {
        $('#signUpBtn div div span span:last').text("Sign up with Google");
        $('#signUpBtn div div span span:first').text("Sign up with Google");
}, 300);

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

