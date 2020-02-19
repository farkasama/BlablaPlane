var urlSearchFlight = '/blablaplane/flights/search';
var urlPostFlight = '/blablaplane/flights/add';
var urlPostPassenger = '/blablaplane/passengers/signup';
var urlLogPassenger = '/blablaplane/passengers/signin';
var urlPostPilot = '/blablaplane/pilots/signup';
var urlLogPilot = '/blablaplane/pilots/signin';
var urlBook = '/blablaplane/flights/book';
<<<<<<< HEAD

=======
var urlGetFlights = '/blablaplane/passengers/myflights';
>>>>>>> master

var type = "";
var header = null;

<<<<<<< HEAD
=======
$( document ).ready(function() {
	
	
	if(window.localStorage.getItem('token')) {
		
		if (window.localStorage.getItem('type') == "passenger") {
			
			$("#lienRecherche").show();
			$("#lienReservation").show();
			
		} else {
			$("#lienpost").show();
		}
		
		header = new Headers();
		header.append('token', window.localStorage.getItem('token'));
		
		$("#logOut").show();
		$("#sup").hide();
		$("#sin").hide();
		
	} else {
		
		$(".connected").hide();
		$("#lienRecherche").hide();
		$("#lienReservation").hide();
		$("#logOut").hide();
		$("#sup").show();
		$("#sin").show();
	}
	
});

>>>>>>> master
function afterSearch(listF) {
	$("#resultsearch").text("");
	$("#notfound").hide();
	if (listF.length == 0) {
		$("#notfound").show();
		return;
	}
	for (i = 0; i < listF.length; i++) {
		var templateExample = _.template($('#ajoutsearch').html());
		var html = templateExample({
			"idf" : listF[i].idFlight,
			"depart" : listF[i].departureAirport,
			"arrive" : listF[i].arrivalAirport,
			"date" : listF[i].date,
			"time" : listF[i].timep,
			"mail" : listF[i].pilot.mail,
			"pilote" : listF[i].pilot.firstName,
			"modele" : listF[i].modelePlane,
			"price" : listF[i].price,
			"place" : listF[i].seatLeft
		});
		$("#resultsearch").append(html);
	
	}
}

// fonction generique pour recuperer des donnes
function getServerData(url, callBack, type, data, header) {
	$.ajax({
		url : url,
		type : type,
		dataType : 'json',
		beforeSend: function(request) {
			if(header)
				request.setRequestHeader("token", header.get('token'));
		  },
		contentType : 'application/json',
		success : function(data) {
			if (callBack)
				callBack(data);
		},
		data : JSON.stringify(data),
		error : function(jqXHR, textStatus, errorThrown) {
			alert("pas ok " + textStatus);
		}
	});
}

// click de bouton recherche vol
$(function() {
	$("#search").click(function() {
		var local = "";
		var travel = "";
		if ($('#travelsearch').is(':checked'))
			travel = "travel";
		if ($('#localsearch').is(':checked'))
			local = "local";
		var data = {
			typeLocal : local,
			typeTravel : travel,
			date : $("#datesearch").val(),
			departure : $("#departuresearch").val(),
			arrival : $("#arrivalsearch").val()
		};
		getServerData(urlSearchFlight, afterSearch, 'post', data, header);
<<<<<<< HEAD
		// alert(data.typeLocal+" "+data.typeTravel+" "+data.date+
		// " "+data.departure+" "+data.arrival);
=======
	
>>>>>>> master
	});
});

function afterPost(flight) {
	$("#signin").removeData('bs.modal');
	$('#postFlight').modal('hide');
}

// click de bouton planifié vol
$(function() {
	$("#postflight").click(function() {
		// construction des donne du vol
		var typeVol = "";
		if ($('#travelpost').is(':checked'))
			typeVol = "travel";
		else
			typeVol = "local";
		var data = {
			date : $("#datepost").val(),
			timep : $("#timepost").val(),
			departureAirport : $("#departurepost").val(),
			arrivalAirport : $("#arrivalpost").val(),
			travelTime : $("#traveltimepost").val(),
			price : $("#pricepost").val(),
			seatLeft : $("#placepost").val(),
			typeFlight : typeVol,
			pilot : null,
			idFlight : null,
			modelePlane : null,
			passagers : null
		}
		
		getServerData(urlPostFlight, afterPost, 'post', data, header);
		
	});
});

// lien de recherche de vol
$(function() {
	$("#lienRecherche").click(function() {
		$("#resultsearch").text("");
	});
});

// verifier si le formulaire d'incription a bien ete rempli
function VerifFormSingup() {
	var regex = /^[a-zA-Z0-9._-]+@[a-z0-9._-]{2,}\.[a-z]{2,4}$/;
	valid = true;
	if ($("#firstName").val() == "" || $("#lastName").val() == ""
			|| $("#mail").val() == "" || $("#password").val() == "") {
		valid = false;
		$("#formsignup .error-form").fadeIn().text(
				"veuillez remplir tout les champs");
	} else if (!regex.test($('#mail').val())) {
		$("#formsignup .error-form").fadeIn().text("Format email incorrect");
		valid = false;
	} else {
		$("#formsignup .error-form").fadeIn().text("");
		valid = true;
	}
	return valid;
}

// creer un pilote ou un passager
$(function() {
	$("#createUser")
			.click(
					function() {
						if (VerifFormSingup()) {
							if ($('#formsignup input[name="type"]:checked')
									.val() == "pilot") {
								$("#formsignup .error-form").text("");
								getServerData(urlPostPilot, afterPostUser,
										'post', formsignupToJSON(), header);
							} else if ($(
									'#formsignup input[name="type"]:checked')
									.val() == "passenger") {
								$("#formsignup .error-form").text("");
								getServerData(urlPostPassenger, afterPostUser,
										'post', formsignupToJSON(), header);
							} else {
								$("#formsignup .error-form")
										.fadeIn()
										.text(
												"veuillez cocher la case pilote ou passager");
							}
						}
					});
});

function afterPostUser(user) {
	// jQuery.noConflict();
	$('#signup').modal('hide');
	$('#signin').modal('show');
	$(".signup-sucess").fadeIn().text(
			"Inscription reussie vous pouvez vous connecter");
}

// return les info du formulaire d'inscription
function formsignupToJSON() {
	// var formulaire=JSON.stringify({
	var formulaire = {
		firstName : $('#firstName').val(),
		lastName : $('#lastName').val(),
		mail : $('#mail').val(),
		password : $('#password').val()
	};
	return formulaire;
}

// verifie si le formulaire de connection a bien ete rempli
function VerifFormSingin() {
	var regex = /^[a-zA-Z0-9._-]+@[a-z0-9._-]{2,}\.[a-z]{2,4}$/;
	valid = true;
	if ($("#mailog").val() == "" || $('#logpass').val() == "") {
		valid = false;
		$("#formsignin .error-form").fadeIn().text(
				"veuillez remplir tout les champs");
	} else if (!regex.test($('#mailog').val())) {
<<<<<<< HEAD
		console.log("jjjjjj");
=======
>>>>>>> master
		$("#formsignin .error-form").fadeIn().text("Format email incorrect");
		valid = false;
	} else {
		$("#formsignin .error-form").fadeIn().text("");
		valid = true;
	}
	return valid;
}

// connection pilote ou passager
$(function() {
	$("#connectuser")
<<<<<<< HEAD
			.click(
					function() {
						if (VerifFormSingin()) {
							if ($('#formsignin input[name="type"]:checked')
									.val() == "pilot") {
								$("#formsignin .error-form").text("");
								type = "pilot";
								getServerData(urlLogPilot, afterLoginUser,
										'post', formlogToJSON(), header);
							} else if ($(
									'#formsignin input[name="type"]:checked')
									.val() == "passenger") {
								$("#formsignin .error-form").text("");
								type = "passenger";
								getServerData(urlLogPassenger, afterLoginUser,
										'post', formlogToJSON(), header);
							} else {
								$("#formsignin .error-form")
										.fadeIn()
										.text(
												"veuillez cocher la case pilote ou passager");
							}
						}
					});
=======
			.click(function() {
				if (VerifFormSingin()) {
					if ($('#formsignin input[name="type"]:checked').val() == "pilot") {
						$("#formsignin .error-form").text("");
						type = "pilot";
						getServerData(urlLogPilot, afterLoginUser,
								'post', formlogToJSON(), header);
					} else if ($('#formsignin input[name="type"]:checked')
								.val() == "passenger") {
						$("#formsignin .error-form").text("");
						type = "passenger";
						getServerData(urlLogPassenger, afterLoginUser,
							'post', formlogToJSON(), header);
					} else {
						$("#formsignin .error-form")
							.fadeIn()
							.text("veuillez cocher la case pilote ou passager");
					}
				}
			});
>>>>>>> master
});

function afterLoginUser(user) {
	
	if (user.error) {
		$("#formsignin .error-form").fadeIn().text(
<<<<<<< HEAD
				"Votre email ou mot de passe sont incorrect");
	} else {
		header = new Headers();
		header.append('token', user.id);

		$('#signin').modal('hide');
		$(".signup-sucess").text("");
		$("#signin").removeData('bs.modal');
		// ////////////////////////////////////////////////////////////////////////
		// achanger par session
		
		if (type == "passenger") {
			$("#lienRecherche").show();
			$("#lienReservation").show();
		} else {
			$("#lienpost").show();
		}
		$("#logOut").show();
		$("#sup").hide();
		$("#sin").hide();
		// ///////////////////////////////////////////////////////////////////////
=======
				"Votre email ou mot de passe sont incorrect"+user.id);
	} else {
		
		window.localStorage.setItem('type', type);
		window.localStorage.setItem('token', user.id);
		
		$('#signin').modal('hide');
		$(".signup-sucess").text("");
		$("#signin").removeData('bs.modal');
		
		window.location.href = "http://localhost:8080";

>>>>>>> master
	}
}

// return les infos du formulaire
function formlogToJSON() {
	var form = {
		mail : $('#mailog').val(),
		password : $('#logpass').val()
	};
	return form;
}

<<<<<<< HEAD
// se deconnecter
$(function() {
	$("#logOut").click(function() {
		$(".connected").hide();
		$("#lienRecherche").hide();
		$("#lienReservation").hide();
		$("#logOut").hide();
		$("#sup").show();
		$("#sin").show();
		header.delete('token');
		header = null;
=======
function mesReservation() {
	getServerData(urlGetFlights, reservationResponse, 'post', null, header);
}

function reservationResponse(listF) {
	$("#resultsearch").text("");
	$("#notfound").hide();
	if (listF.length == 0) {
		return;
	}
	for (i = 0; i < listF.length; i++) {
		var templateExample = _.template($('#ajoutReservation').html());
		var html = templateExample({
			"idf" : listF[i].idFlight,
			"depart" : listF[i].departureAirport,
			"arrive" : listF[i].arrivalAirport,
			"date" : listF[i].date,
			"time" : listF[i].timep,
			"mail" : listF[i].pilot.mail,
			"pilote" : listF[i].pilot.firstName,
			"modele" : listF[i].modelePlane,
			"price" : listF[i].price,
			"place" : listF[i].seatLeft
		});
		$("#mesReservation").append(html);
	
	}
}

// se deconnecter
$(function() {
	$("#logOut").click(function() {
		
		window.localStorage.removeItem('token');
		window.localStorage.removeItem('type');
		window.localStorage.clear();
				
		header.delete('token');
		header = null;
		type = "";		
		window.location.href = "http://localhost:8080";
>>>>>>> master
	});
});

function afterBook(response) {
	if(response.error)
		alert(response.error);
	else 
		alert(response.message);
}

// reserver un vol par un passenger
function bookFlight(idflight) {
	if (header == null)
		$('#signin').modal('show');
	else {
		var data = {
				idPassenger : null,
				idFlight : idflight,
				numberPlace : $('#nbPlace').val()
			}
		getServerData(urlBook, afterBook, 'post', data, header);
	}
}