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
            newAlert("Oh no!", "Something went wrong. Please contact clinicaltriall@aol.com for more information.", "/participantdashboard.html")
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
    $('[type="date"]').prop('min', function(){
        return new Date().toJSON().split('T')[0];
    });

    $('#startDate').change(function(){
      var date = new Date($('#startDate').val());
      $('#endDate').attr('min', date.toJSON().split('T')[0])
    });

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
    $("#createTrial").submit(function( event ) {

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
    	save_changes($( this ).serializeArray())
      event.preventDefault();
    });

    $('input[type="radio"]').click(function() {
       if($(this).attr('id') == 'metricButton') {
            $('.imperial').hide();
            $('.metric').show()
            $('.metric').required = true;
            $('.imperial').removeAttr('required')

       }
       else if($(this).attr('id') == 'imperialButton') {
            $('.metric').hide(); 
            $('.imperial').show();   
            $('.imperial').required = true;
            $('.metric').removeAttr('required')     
       }
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

    $.ajax({
        type: "POST",
        url: "/new-trial-submit",                
        dataType : "json",
        contentType: "application/json; charset=utf-8",
        data : JSON.stringify(form_data),
        success: function(result){
        	newAlert("Success!", "You have successfully created a new trial.", result)
        },
        error: function(request, status, error){
            newAlert("Oh no!", "Something went wrong when saving your changes. Please contact clinicaltriall@aol.com for more information.", "/researcherdashboard.html")
            console.log("Error");
            console.log(request);
            console.log(status);
            console.log(error);
        }
    });    
}