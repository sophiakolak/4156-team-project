function signOut() {
  var auth2 = gapi.auth2.getAuthInstance();
  var profile = auth2.currentUser.get().getBasicProfile();
  var email = profile.getEmail();
  auth2.signOut().then(function () {
    $.ajax({
        type: "POST",
        url: "/logout",                
        dataType : "json",
        contentType: "application/json; charset=utf-8",
        data : JSON.stringify(email),
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
  });
}

$(document).ready(function(){

    $('.metric').hide(); 
    $('.imperial').show();

    // Get request
    $.ajax({
        type: "GET",
        url: "/edit-part-form",                
        dataType : "json",
        contentType: "application/json; charset=utf-8",
        success: function(result){
          loadInfo(result)
        },
        error: function(request, status, error){
            console.log("Error");
            console.log(request)
            console.log(status)
            console.log(error)
        }
      });
    
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
      // Convert height to height in inches
      // Convert weight to weight in pounds
      var heightInInches;
      var weightInLbs
      if ($('#metricButton:checked').length > 0) {
        // metric
        weightInLbs = $('#kilograms').val() * 2.205
        heightInInches = $('#centimeters').val() * (1/2.54)
      } else {
        // imperial
        weightInLbs = $('#pounds').val()
        heightInInches = (12 * $('#feet').val()) + Number($('#inches').val())
      }

      $("#heightInInches").val(heightInInches)
      $("#weightInLbs").val(weightInLbs)
    	save_changes($( this ).serializeArray())
        event.preventDefault();
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
})

// load in participant info
function loadInfo(participantInfo) {
  console.log("participant: " + participantInfo)
  var criteria = participantInfo.data
  $(".first").val(participantInfo.first)
  $(".last").val(participantInfo.last)
  $(".email").val(participantInfo.email)
  $(".location").val(participantInfo.location)
  $(".age").val(criteria.minAge)
  var gender = criteria.gender
  if (gender == "Female") {
    $('#Female').attr('checked', 'checked');
  } else if (gender == "Male") {
    $('#Male').attr('checked', 'checked');
  } else if (gender == "Other") {
    $('#Other').attr('checked', 'checked');
  }
  $(".ethnicity").val(criteria.race)
  $(".nationality").val(criteria.nationality)

  var heightInInches = criteria.minHeight
  var weightInLbs = criteria.minWeight
  console.log(weightInLbs)

  var weightInKilos = weightInLbs / 2.205
  $("#kilograms").val(weightInKilos)
  var heightInCm = heightInInches * 2.54
  $("#centimeters").val(heightInCm)

  var feet = Math.floor(heightInInches / 12)
  $("#feet").val(feet)
  var inches = heightInInches % 12
  $("#inches").val(inches)
  $("#pounds").val(weightInLbs)

}

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

function save_changes(form_data){    

    $.ajax({
        type: "POST",
        url: "/edit-part-submit",                
        dataType : "json",
        contentType: "application/json; charset=utf-8",
        data : JSON.stringify(form_data),
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
