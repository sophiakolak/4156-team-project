$(document).ready(function(){
    $("#saveChanges").submit(function( event ) {
        alert( "Saved Changes (not really but we have the button)" );
        event.preventDefault();
        // window.location.href = "participantdashboard.html"
    });
})
