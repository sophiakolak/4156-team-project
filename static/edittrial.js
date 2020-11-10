$(document).ready(function(){
    $("#saveChanges").submit(function( event ) {
        alert( "Saved Changes (not really but we have the button)" );
        //make request here
        event.preventDefault();
        window.location.href = "researcherdashboard.html"
    });
    $("#cancel").click(function( event ) {
        alert( "Cancel. No Changes Saved. (not really but we have the button)" );
        event.preventDefault();
        window.location.href = "researcherdashboard.html"
    });
})