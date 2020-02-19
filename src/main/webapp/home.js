var datauserconnect;
var RedirictURL = "http://localhost:8080/profilPage/";

function Postuser(url, success){
	
	console.log(formToJSON());
    $.ajax({
	 type: 'POST',
contentType: 'application/json',
        dataType: "json",
data:formToJSON(),
        url: url,
       success: function(data){ console.log('findB success: ' + data.firstName);
 jQuery.noConflict();
$('#signup').modal('hide');
$('#signin').modal('show');
$(".signup-sucess").fadeIn().text("Inscription reussie vous pouvez vous connecter");

},
       error: function(jqXHR, textStatus, errorThrown){
			alert('add pilot error' + textStatus);
}
    });
}





function loguser(url, success){
	
	console.log(formlogToJSON());
    $.ajax({
	 type: 'POST',
contentType: 'application/json',
        dataType: "json",
data:formlogToJSON(),
        url: url,
       success: function(data){ 
	
	if(!data){
	$("#formsignin .error-form").fadeIn().text("Votre email ou mot de passe sont incorrect");	
	/*var templateExample = _.template($('#templateExample').html());
message="Votre mail et mot de passe sont incorrect";
	var html = templateExample({
		"attribute":message
	});

	$(".msEror").after(html);
	console.log("kkkkkk");	
		*/
	}else{
		$('#signin').modal('hide');
		$(".signup-sucess").text("");
		 $("#signin").removeData('bs.modal');
		
		
	}

	
	
	

},
       error: function(jqXHR, textStatus, errorThrown){
	
			alert('loginpilot error' + textStatus);
}
    });
}




$(function(){
	$("#createUser").click(function(){
		if(VerifFormSingup()){
		
		if($('#formsignup input[name="type"]:checked').val()=="pilot"){
			$("#formsignup .error-form").text("");	
		
		Postuser("blablaplane/user/pilots/signup/");
		}else if($('#formsignup input[name="type"]:checked').val()=="passenger"){
				$("#formsignup .error-form").text("");
		Postuser("blablaplane/user/passenger/signup");
		}else{
			$("#formsignup .error-form").fadeIn().text("veuillez cocher la case pilote ou passager");
		}
		}
		
	});
});

$(function(){
	$("#connectuser").click(function(){
		if(VerifFormSingin()){
			
		if($('#formsignin input[name="type"]:checked').val()=="pilot"){
		$("#formsignin .error-form").text("");
		loguser("blablaplane/user/pilots/signin/");
		}else if($('#formsignin input[name="type"]:checked').val()=="passenger"){
				$("#formsignin .error-form").text("");
		loguser("blablaplane/user/passenger/signin");
		}else{
		$("#formsignin .error-form").fadeIn().text("veuillez cocher la case pilote ou passager");	
		}}
	});
});






function VerifFormSingup(){
	var regex = /^[a-zA-Z0-9._-]+@[a-z0-9._-]{2,}\.[a-z]{2,4}$/;
	valid=true;
	
	if( $("#firstName").val()=="" || $("#lastName").val()=="" || $("#mail").val() == "" ||$("#password").val() == "" ){
		valid=false;
		$("#formsignup .error-form").fadeIn().text("veuillez remplir tout les champs");
		
	}else if(!regex.test($('#mail').val()))
   {

	$("#formsignup .error-form").fadeIn().text("Format email incorrect");
		valid=false;
}
else{
	
		$("#formsignup .error-form").fadeIn().text("");
		valid=true;
		
	}
	return valid;
	
}

function VerifFormSingin(){
	var regex = /^[a-zA-Z0-9._-]+@[a-z0-9._-]{2,}\.[a-z]{2,4}$/;
	valid=true;
	
	if( $("#mailog").val()=="" || $('#logpass').val()==""){
		valid=false;
		$("#formsignin .error-form").fadeIn().text("veuillez remplir tout les champs");
		
	}
	else if(!regex.test($('#mailog').val()))
   {

	console.log("jjjjjj");
	$("#formsignin .error-form").fadeIn().text("Format email incorrect");
		valid=false;
}
else{
		$("#formsignin .error-form").fadeIn().text("");
		valid=true;
		
	}
	return valid;
	
}




function formlogToJSON() {
	
	
	var form=JSON.stringify({firstName:"", lastName:"",mail:$('#mailog').val(),
	password:$('#logpass').val()});
	
	return form;
}

function formToJSON() {
	
	var formulaire=JSON.stringify({firstName:$('#firstName').val(),lastName:$('#lastName').val(),mail:$('#mail').val(),
	password:$('#password').val()});
	
	return formulaire;
}