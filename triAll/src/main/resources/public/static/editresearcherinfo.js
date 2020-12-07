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
          newAlert("Success!", "You have succesfully signed out of TriAll.", result)
        },
        error: function(request, status, error){
            newAlert("Oh no!", "Something went wrong. Please contact clinicaltriall@aol.com for more information.", "/researcherdashboard.html")
            console.log("Error");
            console.log(request)
            console.log(status)
            console.log(error)
        }
    });
  });
}

function newAlert(title, text, redirect){
  var alert = $('<div class="modal fade" id="alert" tabindex="-1" role="dialog" aria-labelledby="exampleModalCenterTitle" aria-hidden="true">')
  var alert1 = $('<div class="modal-dialog modal-dialog-centered" role="document">')
  var alert2 = $('<div class="modal-content">')
  var alert3 = $('<div class="modal-header">')
  var alert4 = $('<h5 class="modal-title" id="exampleModalLongTitle">')
  alert4.append(title)
  var alert5 = $('<div class="modal-body">')
  alert5.append(text)
  var alert6 = $('<div class="modal-footer">')
  var alert7 = ('<button type="button" onclick=\"location.href=\''+redirect+'\'\" class="btn btn-primary">Continue</button>')
  alert6.append(alert7)
  alert3.append(alert4)
  alert2.append(alert3, alert5, alert6)
  alert1.append(alert2)
  alert.append(alert1)
  $("#modal").html(alert)
  $('#alert').modal('toggle')
}

$(document).ready(function(){

    // Get request
    $.ajax({
        type: "GET",
        url: "/edit-res-form",                
        dataType : "json",
        contentType: "application/json; charset=utf-8",
        success: function(result){
          loadInfo(result)
        },
        error: function(request, status, error){
            newAlert("Oh no!", "Something went wrong when retrieving your information. Please contact clinicaltriall@aol.com for more information.", "/researcherdashboard.html")
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
    	save_changes($( this ).serializeArray())
      event.preventDefault();
    });
})

function loadInfo(researcherInfo) {
  console.log("Researcher: " + researcherInfo)
  console.log(researcherInfo.first, researcherInfo.last, researcherInfo.email, researcherInfo.location)
  $(".first").val(researcherInfo.first)
  $(".last").val(researcherInfo.last)
  $(".email").val(researcherInfo.email)
  $(".location").val(researcherInfo.location)
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
        url: "/edit-res-submit",                
        dataType : "json",
        contentType: "application/json; charset=utf-8",
        data : JSON.stringify(form_data),
        success: function(result){
        	  newAlert("Success", "You have successfully saved your changes", result)
        },
        error: function(request, status, error){
            newAlert("Oh no!", "Something went wrong when saving your changes. Please contact clinicaltriall@aol.com for more information.", "/researcherdashboard.html")
            console.log("Error");
            console.log(request)
            console.log(status)
            console.log(error)
        }
    });    
}