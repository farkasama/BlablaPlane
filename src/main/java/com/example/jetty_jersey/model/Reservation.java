package com.example.jetty_jersey.model;

/*
 * Class qui parse les information pour les reservations
 */
public class Reservation {
    private String idPassenger;
    private String idFlight;
    private int numberPlace;

    public Reservation() {

    }

    public Reservation(String idPassenger, String idFlight, int numberPlace) {

	this.idPassenger = idPassenger;
	this.idFlight = idFlight;
	this.numberPlace = numberPlace;
    }

    public String getIdPassenger() {
	return idPassenger;
    }

    public void setIdPassenger(String idPassenger) {
	this.idPassenger = idPassenger;
    }

    public String getIdFlight() {
	return idFlight;
    }

    public void setIdFlight(String idFlight) {
	this.idFlight = idFlight;
    }

    public int getNumberPlace() {
	return numberPlace;
    }

    public void setNumberPlace(int numberPlace) {
	this.numberPlace = numberPlace;
    }

}
