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
    alert(JSON.stringify(form_data))   
    $.ajax({
        type: "POST",
        url: "/new-part-submit",                
        dataType : "json",
        contentType: "application/json; charset=utf-8",
        data : JSON.stringify(form_data),
        success: function(result){
            alert( "Created New Participant" );
        },
        error: function(request, status, error){
            console.log("Error");
            console.log(request)
            console.log(status)
            console.log(error)
        }
    });    
}
