package com.example.jetty_jersey.ws;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.example.jetty_jersey.JwTokenHelper;
import com.example.jetty_jersey.DAO.DAOFactory;
import com.example.jetty_jersey.DAO.FlightDAO;
import com.example.jetty_jersey.DAO.PilotDAO;
import com.example.jetty_jersey.DAO.ReservationDAO;
import com.example.jetty_jersey.model.Pilot;
import com.example.jetty_jersey.model.Connection;
import com.example.jetty_jersey.model.Flight;
import com.example.jetty_jersey.model.ID;
import com.example.jetty_jersey.model.InformationReservation;
import com.example.jetty_jersey.model.Passenger;

@Path("/pilots/")
public class PilotRessource {

    private PilotDAO daoPilot = DAOFactory.getInstance().getPiloteDAO();
    private ReservationDAO daoReservation = DAOFactory.getInstance().getReservationDAO();
    private FlightDAO daoFlight = DAOFactory.getInstance().getFlightDAO();

    // Inscription
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/signup")
    public String signUp(Pilot p) {
	if (daoPilot.checkEmailExist(p.getMail())) {
	    return "{" + "\"status\":\"400\"," + "\"error\":\"Email already used\"" + "}";
	}
	return daoPilot.put(p);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/get")
    public List<Pilot> getPilot(ID identification) {
	return daoPilot.get(identification.getId());
    }

    // Connexion
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/signin")
    public String signIn(Connection c) {
	String token = daoPilot.connect(c);
	System.out.println("reponse" + token);
	return token;
    }

    // Retourne les vols creer par le pilot
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/myflights")
    public List<Flight> getListFlights(@HeaderParam("token") String token) {
	if (JwTokenHelper.getInstance().isTokenInvalid(token)
		|| !JwTokenHelper.getInstance().getUserType(token).equals("pilot")) {
	    return null;
	}
	String id = JwTokenHelper.getInstance().getIdFromToken(token);
	return daoFlight.getFlightsForPilots(id);
    }

    // Retourne les passenger pour un vol idFLight
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/myflights/passenger")
    public List<Passenger> getListPassenger(@HeaderParam("token") String token, ID idFlight) {
	if (JwTokenHelper.getInstance().isTokenInvalid(token)
		|| !JwTokenHelper.getInstance().getUserType(token).equals("pilot")) {
	    return null;
	}
	return daoReservation.getPassengerForPilots(idFlight.getId());
    }
    

    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/book/")
    public List<InformationReservation> getBookingForPilot(@HeaderParam("token") String token) {
	if (JwTokenHelper.getInstance().isTokenInvalid(token) || !JwTokenHelper.getInstance().getUserType(token).equals("pilot")) {
	    return null;
	}
	return daoReservation.getReservationForPilots(JwTokenHelper.getInstance().getIdFromToken(token));
    }

}