// setTimeout(function () {
//         $('#signUpBtn div div span span:last').text("Sign up with Google");
//         $('#signUpBtn div div span span:first').text("Sign up with Google");
// }, 300);

function onSignIn(googleUser) {
    var id_token = String(googleUser.getAuthResponse().id_token)
    var profile = googleUser.getBasicProfile();
    var email = String(profile.getEmail()) // This is null if the 'email' scope is not present.

    data = {
        "email": email,
        "id_token": id_token
    }

    $.ajax({
        type: "POST",
        url: "/login-submit",                
        dataType : "json",
        contentType: "application/json; charset=utf-8",
        data : JSON.stringify(data),
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
}

