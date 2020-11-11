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

