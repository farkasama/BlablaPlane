var urlSearchFlight = '/blablaplane/flights/search';
var urlPostFlight = '/blablaplane/flights/add';
var urlPostPassenger = '/blablaplane/passengers/signup';
var urlLogPassenger = '/blablaplane/passengers/signin';
var urlPostPilot = '/blablaplane/pilots/signup';
var urlLogPilot = '/blablaplane/pilots/signin';
var urlBook = '/blablaplane/flights/book';
var urlGetFlights = '/blablaplane/passengers/myflight/confirmed';
var urlFlightsWaiting = '/blablaplane/passengers/myflight/waiting';
var urlFlightsPilots = '/blablaplane/pilots/myflights';
var urlMyPassengers = '/blablaplane/pilots/myflights/passenger';
var urlConfirmYes = '/blablaplane/flights/book/confirm/yes';
var urlConfirmNo = '/blablaplane/flights/book/confirm/no';
var urlreservationwaiting='/blablaplane/pilots/book';

var type = "";
var header = null;
var mymap;

$(function () {
	  $(document).scroll(function () {
	    var $nav = $(".navbar-default");
	    var $search = $(".ftco-section-search");
	    $nav.toggleClass('scrolled', $(this).scrollTop() > $nav.height() + $search.height()+100);
	  });
	});

$(document).ready(function() {
	
	if(window.localStorage.getItem('token')) {
		type = window.localStorage.getItem('type');
		if (type == "passenger") {
			$("#myPost").hide();
            $("#myReservation").show();
            $("#lienpost").hide();
            $("#statu").hide();
		} else {
			$("#lienpost").show();
            $("#myPost").show();
		}
		
		header = new Headers();
		header.append('token', window.localStorage.getItem('token'));
		
		$("#logOut").show();
		$("#sup").hide();
		$("#sin").hide();
		
	} else {
        $("#myReservation").hide();
        $("#myPost").hide();
		$("#lienpost").hide();
		$("#logOut").hide();
		$("#sup").show();
		$("#sin").show();
	}
	
});

function afterSearch(listF) {
	$("#resultsearch").text("");
	$("#notfound").hide();
    $("#mapid").css("display","none");
	if (listF.length == 0) {
		$("#notfound").show();
		return;
	}
    $("#mapid").css("display","block");
    showMap($("#departuresearch").val(),$("#arrivalsearch").val());
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



// ----------ajout de la liste des reservation----


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
	
		var data = {
			date : $("#datesearch").val(),
			departure : $("#departuresearch").val(),
			arrival : $("#arrivalsearch").val()
		};
		getServerData(urlSearchFlight, afterSearch, 'post', data, header);
	
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
		var data = {
			date : $("#datepost").val(),
			timep : $("#timepost").val(),
			departureAirport : $("#departurepost").val(),
			arrivalAirport : $("#arrivalpost").val(),
            rdv : $("#rdvpost").val(),
			travelTime : $("#traveltimepost").val(),
			price : $("#pricepost").val(),
			seatLeft : $("#placepost").val(),
			pilot : null,
			idFlight : null,
			modelePlane : $("#modelepost").val()
		}
		
		getServerData(urlPostFlight, afterPost, 'post', data, header);
		
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
				"You are required to complete all fields");
	} else if (!regex.test($('#mail').val())) {
		$("#formsignup .error-form").fadeIn().text("Inc");
		valid = false;
	} else {
		$("#formsignup .error-form").fadeIn().text("");
		valid = true;
	}
	return valid;
}

// creer un pilote ou un passager
$(function() {$("#createUser").click(function() {
		if (VerifFormSingup()) {
			if ($('#formsignup input[name="type"]:checked')
					.val() == "pilot") {
				$("#formsignup .error-form").text("");
				getServerData(urlPostPilot, afterPostUser,
						'post', formsignupToJSON(), header);
			} else if ($('#formsignup input[name="type"]:checked')
					.val() == "passenger") {
				$("#formsignup .error-form").text("");
				getServerData(urlPostPassenger, afterPostUser,
						'post', formsignupToJSON(), header);
			} else {
				$("#formsignup .error-form")
						.fadeIn()
						.text("Please check the Pilot or Passenger box.");
			}
		}
	});
});

function afterPostUser(user) {
	// jQuery.noConflict();
	$('#signup').modal('hide');
	$('#signin').modal('show');
	$(".signup-sucess").fadeIn().text(
			"You have successfuly signed up to BlablPlane");
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
				"You are required to complete all fields");
	} else if (!regex.test($('#mailog').val())) {
		$("#formsignin .error-form").fadeIn().text("Incorrect email format.");
		valid = false;
	} else {
		$("#formsignin .error-form").fadeIn().text("");
		valid = true;
	}
	return valid;
}

// connection pilote ou passager
$(function() {$("#connectuser").click(function() {
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
					.text("Please check the Pilot or Passenger box.");
			}
		}
	});
});

function afterLoginUser(user) {
	
	if (user.error) {
		$("#formsignin .error-form").fadeIn().text(
				"The email and password do not match. Please try again."+user.id);
	} else {
		
		window.localStorage.setItem('type', type);
		window.localStorage.setItem('token', user.id);
		
		$('#signin').modal('hide');
		$(".signup-sucess").text("");
		$("#signin").removeData('bs.modal');
		
		window.location.href = "http://localhost:8080";

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

function mesReservation() {
	getServerData(urlGetFlights, reservationResponse, 'post', null, header);
}


// click sur lien pour avoir les vol poster par un pilote
$(function() {
	$("#myPost").click(function() {
		myFlightsPilots();
	});
});

function myFlightsPilots() {
	getServerData(urlFlightsPilots, ajoutPost, 'post', null, header);
}

function ajoutPost(listF) {
	$("#resultspost").text("");
	$("#nopost").hide();
	if (listF.length == 0) {
		$("#nopost").show();
		return;
	}
	for (i = 0; i < listF.length; i++) {
		var templateExample = _.template($('#ajoutpost').html());
		var html = templateExample({
			"depart" : listF[i].departureAirport,
			"arrive" : listF[i].arrivalAirport,
			"date" : listF[i].date,
			"time" : listF[i].timep,
			"modele" : listF[i].modelePlane,
			"price" : listF[i].price,
			"place" : listF[i].seatLeft
		});
		$("#resultspost").append(html);
	
	}
}


// get list passenger for a flight
function getMyPassengers(idFlight) {
	var data = {
			idFlight : idflight
		}
	getServerData(urlFlightsPilots, null, 'post', data, header);
}

function reservationResponse(listR) {
	
	$("#resultReservation").text("");
	$("#noreservation").hide();
	if (listR.length == 0) {
		$("#noreservation").show();
		return;
	}
	
	if(type == "passenger") {
		
		for (i = 0; i < listR.length; i++) {
			var templateExample = _.template($('#clientreservation').html());
			var html = templateExample({
				"depart" : listR[i].departureAirport,
				"arrival" : listR[i].arrivalAirport,
				"dater" : listR[i].date,
				"nbplace" : listR[i].numberPlace,
				"statu" : trad(listR[i].statut)
			});
			$("#resultReservation").append(html);
		}
	} else {
        
		for (i = 0; i < listR.length; i++) {
            if(listR[i].statut==true){//en attente
                var templateExample = _.template($('#demandereservation').html());
                var html = templateExample({
                    "idr" : listR[i].idReservation,
                    "first" : listR[i].fistNamePassenger,
                    "last" : listR[i].lastNamePassenger,
                    "depart" : listR[i].departureAirport,
                    "arrival" : listR[i].arrivalAirport,
                    "dater" : listR[i].date,
                    "nbplace" : listR[i].numberPlace,
                    "statu" : trad(listR[i].statut)
                });
                $("#resultReservation").append(html);	
            }
            else{//accepter
                var templateExample = _.template($('#listreservation').html());
                var html = templateExample({
                    "idr" : listR[i].idReservation,
                    "first" : listR[i].fistNamePassenger,
                    "last" : listR[i].lastNamePassenger,
                    "depart" : listR[i].departureAirport,
                    "arrival" : listR[i].arrivalAirport,
                    "dater" : listR[i].date,
                    "nbplace" : listR[i].numberPlace,
                    "statu" : trad(listR[i].statut)
                });
                $("#resultReservation").append(html);	
            }
        }
	}	
}

//fonction qui traduit le statut en texte
function trad(b){
    if(b==true)
        return "waitting";
    else
        return "confirmed";
}

//clic sur "reservations"
function voirReservation() {
		
	if(type=="passenger") {
		mesReservation();
	} else {
        console.log("pilote");
		getServerData(urlreservationwaiting, reservationResponse, 'post', null, header);	
	}	
	
}

// fonction accepter reservation
function confirmer(idReservation) {
	
	var data = {
			id: idReservation
		}
	getServerData(urlConfirmYes, afterBook, 'post', data, header);
	window.location.href = "http://localhost:8080";
} 

// fonction refuser reservation
function refuser(idReservation) {
	
	var data = {
			id: idReservation
		}
	getServerData(urlConfirmNo, afterBook, 'post', data, header);
	window.location.href = "http://localhost:8080";
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
	});
});

function afterBook(response) {
	if(response.error)
		alert(response.error);
	else 
		alert(response.message);
	
	return mesReservation();
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

// fonction d,'affichage de la map
function showMap(depart,arrive){
    var nom1="",nom2="";
	var coord1=[],coord2=[];
	var p1=false,p2=false;
	
	// recupere coordonnee addresse 1;
	// je prend le 1er resultat, limit=1
	$.getJSON('http://nominatim.openstreetmap.org/search?format=json&limit=1&q=' + depart, function(data) {
		// var items = [];
		
		$.each(data, function(key, val) {
			coord1[0]=val.lat;
			coord1[1]=val.lon;
			nom1=val.display_name;
			// alert(coord1[0]+" "+coord1[1]+"\n"+nom1);
		});
		// traiter le resultat
		if(nom1=="")
			alert(depart+" pas trouver");
		else{
			// recupere coordonnee addresse 2;
			// je prend le 1er resultat, limit=1
			$.getJSON('http://nominatim.openstreetmap.org/search?format=json&limit=1&q=' + arrive, function(data) {
				// var items = [];
				$.each(data, function(key, val) {
					coord2[0]=val.lat;
					coord2[1]=val.lon;
					nom2=val.display_name;
					// alert(coord2[0]+" "+coord2[1]+"\n"+nom2);
				});
				// traiter le resultat
				if(nom2=="")
					alert(arrive+" pas trouver");
				else{
                    if(mymap)
                        mymap.remove();
                    mymap = L.map('mapid').setView([48.8534, 2.3488], 13);
                    L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={accessToken}', {
                    attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors, <a href="https://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="https://www.mapbox.com/">Mapbox</a>',
                    maxZoom: 18,
                    id: 'mapbox.streets',
                    accessToken: 'pk.eyJ1Ijoia2l6eXgiLCJhIjoiY2pzenNub2RhMHA2ZDQ0cGhpejFjMjI1aiJ9.WKv99B76fpwk-VIz9LNbIA'
                    }).addTo(mymap);
					var latlngs = [coord1,coord2];
					var polyline = L.polyline(latlngs, {color: 'red'}).addTo(mymap);
					var marker1 = L.marker(coord1, {draggable: true}).addTo(mymap);
					var marker2 = L.marker(coord2, {draggable: true}).addTo(mymap);
				  	marker1.bindPopup(nom1).openPopup();
				  	marker2.bindPopup(nom2).openPopup();
				}
			});
		}
	});
}