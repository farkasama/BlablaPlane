package com.example.jetty_jersey.ws;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.example.jetty_jersey.JwTokenHelper;
import com.example.jetty_jersey.SendEmailTLS;
import com.example.jetty_jersey.DAO.DAOFactory;
import com.example.jetty_jersey.DAO.FlightDAO;
import com.example.jetty_jersey.DAO.PassengerDAO;
import com.example.jetty_jersey.DAO.ReservationDAO;
import com.example.jetty_jersey.model.Flight;
import com.example.jetty_jersey.model.Recherche;
import com.example.jetty_jersey.model.Reservation;
//import com.example.jetty_jersey.model.Pilot;

import com.example.jetty_jersey.model.ID;
import com.example.jetty_jersey.model.Passenger;
import com.example.jetty_jersey.model.Pilot;

@Path("/flights")
public class FlightRessource {

	private FlightDAO daoFlight = DAOFactory.getInstance().getFlightDAO();
	private ReservationDAO daoReservation = DAOFactory.getInstance().getReservationDAO();
	private PassengerDAO daoPassenger = DAOFactory.getInstance().getPassengerDAO();

	// Ajoute un vol
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/add")
	public String postFlight(@HeaderParam("token") String token, Flight f) {

		// Verifie si le token est bon
		if (JwTokenHelper.getInstance().isTokenInvalid(token)) {
			return "{" + "\"status\":\"403\"," + "\"error\":\"Your token is not valid. Try to reconnect.\"" + "}";
		} else if (!JwTokenHelper.getInstance().getUserType(token).equals("pilot")) {
			return "{" + "\"status\":\"403\"," + "\"error\":\"You dont have the permission\"" + "}";
		}
		String id = JwTokenHelper.getInstance().getIdFromToken(token);
		f.setPilot(new Pilot(id, null, null, null, 0, null));
		return daoFlight.put(f);
	}

	// Recuperer infos d'un vol
	// reserver un vol
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/get")
	public List<Flight> get(ID identification) {
		return daoFlight.get(identification.getId());
	}

	// Reserve un vol
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/book")
	public String book(@HeaderParam("token") String token, Reservation r) {
		if (JwTokenHelper.getInstance().isTokenInvalid(token)) {
			return "{" + "\"status\":\"403\"," + "\"error\":\"Your token is not valid. Try to reconnect.\"" + "}";
		} else if (!JwTokenHelper.getInstance().getUserType(token).equals("passenger")) {
			return "{" + "\"status\":\"403\"," + "\"error\":\"You dont have the permission\"" + "}";
		}

		Flight f = daoFlight.get(r.getIdFlight()).get(0);
		String to = f.getPilot().getMail();

		// Envoie un email
		String text = "Hello " + f.getPilot().getFirstName() + " " + f.getPilot().getLastName() + "! \n"
				+ "A new passenger booked a flight with you the " + f.getDate() + ". \n" + "Departure: "
				+ f.getDepartureAirport() + "\n" + "Arrival: " + f.getArrivalAirport() + "\n"
				+ "To confirm the booking, go to blablaplane.com \n"
				+ "Please sign in to approve or refuse the reservation from 'My Flights' section \n\n"
				+ "The Blablaplane team hope to see you soon";

		SendEmailTLS.envoyerMail(to, SendEmailTLS.subjectReservation, text);

		r.setIdPassenger(JwTokenHelper.getInstance().getIdFromToken(token));

		String response = daoReservation.put(r);
		return response;
	}

	// Recherche un flight
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/search")
	public List<Flight> search(Recherche r) {
		List<Flight> list = daoFlight.get(r);
		return list;
	}

	//Accepte la reservation
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/book/confirm/yes")
	public String confirmYes(@HeaderParam("token") String token, ID id) {
		if (JwTokenHelper.getInstance().isTokenInvalid(token)) {
			return "{" + "\"status\":\"403\"," + "\"error\":\"Your token is not valid. Try to reconnect.\"" + "}";
		} else if (!JwTokenHelper.getInstance().getUserType(token).equals("pilot")) {
			return "{" + "\"status\":\"403\"," + "\"error\":\"You dont have the permission\"" + "}";
		}
		
		String idR = id.getId();
		//Preparation pour l'envoie de mail
		Reservation r = daoReservation.get(idR).get(0);
		Flight f = daoFlight.get(r.getIdFlight()).get(0);
		Passenger p = daoPassenger.get(r.getIdPassenger()).get(0);
		
		// text à changer
		String text = "Hello " + p.getFirstName() + " " + p.getLastName() + "! \n"
				+ "The pilot confirmed the booking.\n"+"Date:" + f.getDate() + ". \n" + "Departure: "
				+ f.getDepartureAirport() + "\n" + "Arrival: " + f.getArrivalAirport() + "\n"
				+ "You can go to the 'My Flights' section to access to all of the information of your flight \n\n"
				+ "The Blablaplane team hope to see you soon";

		SendEmailTLS.envoyerMail(p.getMail(), SendEmailTLS.subjectConfirmation, text);
		
		//Confirme la reservation dans la base
		if (daoReservation.update(null,idR)) {
			return "{" + "\"status\":\"200\"," + "\"error\":\"Booking well confirmed\"" + "}";
		}
		return "{" + "\"status\":\"404\"," + "\"error\":\"Reservation couldnt be found\"" + "}";
	}

	//Refuse la presentation
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/book/confirm/no")
	public String confirmNo(@HeaderParam("token") String token, ID id) {
		if (JwTokenHelper.getInstance().isTokenInvalid(token)) {
			return "{" + "\"status\":\"403\"," + "\"error\":\"Your token is not valid. Try to reconnect.\"" + "}";
		} else if (!JwTokenHelper.getInstance().getUserType(token).equals("pilot")) {
			return "{" + "\"status\":\"403\"," + "\"error\":\"You dont have the permission\"" + "}";
		}

		String idR = id.getId();
		//Preparation de l'envoie de mail
		Reservation r = daoReservation.get(idR).get(0);
		Flight f = daoFlight.get(r.getIdFlight()).get(0);
		Passenger p = daoPassenger.get(r.getIdPassenger()).get(0);
		
		//text à changer
		String text = "Hello " + p.getFirstName() + " " + p.getLastName() + "! \n"
				+ "The pilot refused the booking of your flight:\n"+"Date: " + f.getDate() + ". \n" + "Departure: "
				+ f.getDepartureAirport() + "\n" + "Arrival: " + f.getArrivalAirport() + "\n"
				+ "If you want to book another flight, go to blablaplane.com \n"
				+ "The Blablaplane team hope to see you soon";

		SendEmailTLS.envoyerMail(p.getMail(), SendEmailTLS.subjectConfirmation, text);

		//Suppression de la reservation
		if (daoReservation.delete(null, idR)) {
			return "{" + "\"status\":\"200\"," + "\"error\":\"Booking well refused\"" + "}";
		}
		return "{" + "\"status\":\"404\"," + "\"error\":\"Reservation couldnt be found\"" + "}";
	}

}
