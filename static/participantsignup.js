$(document).ready(function(){
    $("#saveChanges").submit(function( event ) {
    	save_changes($( this ).serializeArray())
        event.preventDefault();
    });
})

function save_changes(form_data){    
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
