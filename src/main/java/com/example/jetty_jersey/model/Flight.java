package com.example.jetty_jersey.model;

/*
 * Class qui parse les information pour les flights
 */
public class Flight {
    private String idFlight;
    private String date;
    private String departureAirport;
    private String arrivalAirport;
    private double travelTime;
    private double price;
    private String timep;
    private String modelePlane;
    private Pilot pilot;
    private int seatLeft;
    private String rdv;
    

    public Flight(String idFlight, String date, String departureAirport, String arrivalAirport, double travelTime,
	    double price, String timep, String modelePlane, Pilot pilot, int seatLeft, String rdv) {
	this.idFlight = idFlight;
	this.date = date;
	this.departureAirport = departureAirport;
	this.arrivalAirport = arrivalAirport;
	this.travelTime = travelTime;
	this.price = price;
	this.timep = timep;
	this.modelePlane = modelePlane;
	this.pilot = pilot;
	this.seatLeft = seatLeft;
	this.rdv = rdv;
    }

    public Flight() {
    }

    public String getTimep() {
	return timep;
    }

    public void setTimep(String timep) {
	this.timep = timep;
    }

    public int getSeatLeft() {
	return seatLeft;
    }

    public void setSeatLeft(int seatLeft) {
	this.seatLeft = seatLeft;
    }

    public String getIdFlight() {
	return idFlight;
    }

    public void setIdFlight(String idFlight) {
	this.idFlight = idFlight;
    }

    public String getDate() {
	return date;
    }

    public void setDate(String date) {
	this.date = date;
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

    public double getTravelTime() {
	return travelTime;
    }

    public void setTravelTime(double travelTime) {
	this.travelTime = travelTime;
    }

    public double getPrice() {
	return price;
    }

    public void setPrice(double price) {
	this.price = price;
    }

    public void setModelePlane(String modelePlane) {
	this.modelePlane = modelePlane;
    }

    public String getModelePlane() {
	return modelePlane;
    }

    public Pilot getPilot() {
	return pilot;
    }

    public void setPilot(Pilot pilot) {
	this.pilot = pilot;
    }
    
    public String getRdv() {
	return rdv;
    }
    
    public void setRdv(String rdv) {
	this.rdv = rdv;
    }

}
