package com.example.jetty_jersey.model;

/*
 * Class qui donne les informations complete des reservations
 * Elle  n'est seulement utilise pour renvoyer ces informations au FrontEnd
 */
public class InformationReservation {
	String fistNamePassenger;
	String lastNamePassenger;
	String departureAirport;
	String arrivalAirport;
	String date;
	String numberPlace;
	String idReservation;
	boolean statut; // true = en attente, false si deja accepter

	public InformationReservation(String fistNamePassenger, String lastNamePassenger, String departureAirport,
			String arrivalAirport, String date, String numberPlace, String idReservation, boolean statut) {
		this.fistNamePassenger = fistNamePassenger;
		this.lastNamePassenger = lastNamePassenger;
		this.departureAirport = departureAirport;
		this.arrivalAirport = arrivalAirport;
		this.date = date;
		this.numberPlace = numberPlace;
		this.idReservation = idReservation;
		this.statut = statut;
	}

	public String getFistNamePassenger() {
		return fistNamePassenger;
	}

	public void setFistNamePassenger(String fistNamePassenger) {
		this.fistNamePassenger = fistNamePassenger;
	}

	public String getLastNamePassenger() {
		return lastNamePassenger;
	}

	public void setLastNamePassenger(String lastNamePassenger) {
		this.lastNamePassenger = lastNamePassenger;
	}

	public String getDepartureAirport() {
		return departureAirport;
	}

	public void setDepartureAirport(String departureAirport) {
		this.departureAirport = departureAirport;
	}

	public String getArrivalAirport() {
		return arrivalAirport;
	}

	public void setArrivalAirport(String arrivalAirport) {
		this.arrivalAirport = arrivalAirport;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getNumberPlace() {
		return numberPlace;
	}

	public void setNumberPlace(String numberPlace) {
		this.numberPlace = numberPlace;
	}

	public String getIdReservation() {
		return idReservation;
	}

	public void setIdReservation(String idReservation) {
		this.idReservation = idReservation;
	}

	public boolean isStatut() {
		return statut;
	}

	public void setStatut(boolean statut) {
		this.statut = statut;
	}
	
	
}