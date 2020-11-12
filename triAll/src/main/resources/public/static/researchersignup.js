$(document).ready(function(){
    // Check if user is signed in 
    gapi.load('auth2', function() {
      gapi.auth2.init({
        client_id: '46819195782-rhbp0ull70okmgsid0rrd2p8cdub7fpn.apps.googleusercontent.com',
      }).then(function(){
        auth2 = gapi.auth2.getAuthInstance();
        var signedIn = auth2.isSignedIn.get()
        if (!signedIn) {window.location.href = "/login.html"}   
      });
    });
    
    $("#saveChanges").submit(function( event ) {
    	save_changes($( this ).serializeArray())
        event.preventDefault();
    });
})

function save_changes(form_data){    
    $.ajax({
        type: "POST",
        url: "/new-res-submit",                
        dataType : "json",
        contentType: "application/json; charset=utf-8",
        data : JSON.stringify(form_data),
        success: function(result){
            alert( "Created New Researcher" );
        },
        error: function(request, status, error){
            console.log("Error");
            console.log(request)
            console.log(status)
            console.log(error)
        }
    });    
}

