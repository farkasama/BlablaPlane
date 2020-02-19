package com.example.jetty_jersey.model;

/*
 * Class qui parse les information pour les recherches de vols
 */
public class Recherche {
    private String typeLocal;
    private String typeTravel;
    private String date;
    private String departure;
    private String arrival;

    public Recherche() {
    }

    public Recherche(String typeLocal, String typeTravel, String date, String departure, String arrival) {
	this.typeLocal = typeLocal;
	this.typeTravel = typeTravel;
	this.date = date;
	this.departure = departure;
	this.arrival = arrival;
    }

    public String getTypeLocal() {
	return typeLocal;
    }

    public void setTypeLocal(String typeLocal) {
	this.typeLocal = typeLocal;
    }

    public String getTypeTravel() {
	return typeTravel;
    }

    public void setTypeTravel(String typeTravel) {
	this.typeTravel = typeTravel;
    }

    public String getDate() {
	return date;
    }

    public void setDate(String date) {
	this.date = date;
    }

    public String getDeparture() {
	return departure;
    }

    public void setDeparture(String departure) {
	this.departure = departure;
    }

    public String getArrival() {
	return arrival;
    }

    public void setArrival(String arrival) {
	this.arrival = arrival;
    }

}