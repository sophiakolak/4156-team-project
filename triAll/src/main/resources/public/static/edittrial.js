$(document).ready(function(){
    $('.metric').hide(); 
    $('.imperial').show();
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

    $('input[type="radio"]').click(function() {
       if($(this).attr('id') == 'metricButton') {
            $('.imperial').hide();
            $('.metric').show()

       }
       else if($(this).attr('id') == 'imperialButton') {
            $('.metric').hide(); 
            $('.imperial').show();      
       }
   });
    // Add function to load in trial info
    // should be trial ID
    // this is here for testing
    var trialId = 4



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

    $("#saveChanges").submit(function( event ) {
        // Convert height to height in inches
        // Convert weight to weight in pounds
        var heightInInchesMin;
        var weightInLbsMin;
        var heightInInchesMax;
        var weightInLbsMax;

        if ($('#metricButton:checked').length > 0) {
          // metric
          weightInLbsMin = $('#kilogramsMin').val() * 2.205
          heightInInchesMin = $('#centimetersMin').val() * (1/2.54)

          weightInLbsMax = $('#kilogramsMax').val() * 2.205
          heightInInchesMax = $('#centimetersMax').val() * (1/2.54)

        } else {
          // imperial
          weightInLbsMin = $('#poundsMin').val()
          heightInInchesMin = (12 * $('#feetMin').val()) + Number($('#inchesMin').val())

          weightInLbsMax = $('#poundsMax').val()
          heightInInchesMax = (12 * $('#feetMax').val()) + Number($('#inchesMax').val())
        }

        $("#heightInInchesMin").val(heightInInchesMin)
        $("#weightInLbsMin").val(weightInLbsMin) 

        $("#heightInInchesMax").val(heightInInchesMax)
        $("#weightInLbsMax").val(weightInLbsMax)

        $.ajax({
          type: "POST",
          url: "/edit-trial-submit/:" + trialId + "/",                
          dataType : "json",
          contentType: "application/json; charset=utf-8",
          // data : JSON.stringify(form_data),
          data : JSON.stringify("TEST TEST HI"),
          success: function(result){
            alert( "Saved Changes" );
          },
          error: function(request, status, error){
              console.log("Error");
              console.log(request);
              console.log(status);
              console.log(error);
          }
    });    
        event.preventDefault();
    });
    $("#cancel").click(function( event ) {
        window.location.href = "researcherdashboard.html"
    });
})
var placeSearch, autocomplete, lat, lon;

function initAutocomplete() {
  // Create the autocomplete object, restricting the search to geographical
  // location types.
  autocomplete = new google.maps.places.Autocomplete(
    /** @type {!HTMLInputElement} */(document.getElementById('autocomplete')),
    {types: ['geocode']});

  // When the user selects an address from the dropdown, populate the address
  // fields in the form.
  autocomplete.addListener('place_changed', setLatLon);
}

function setLatLon() {
    lat = autocomplete.getPlace().geometry.location.lat()
    lon = autocomplete.getPlace().geometry.location.lng()
    $("#lat").val(lat)
    $("#lon").val(lon)
}

// Bias the autocomplete object to the user's geographical location,
// as supplied by the browser's 'navigator.geolocation' object.
function geolocate() {
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(function(position) {
      var geolocation = {
        lat: position.coords.latitude,
        lng: position.coords.longitude
      };
      var circle = new google.maps.Circle({
        center: geolocation,
        radius: position.coords.accuracy
      });
      autocomplete.setBounds(circle.getBounds());
    });
  }
}
