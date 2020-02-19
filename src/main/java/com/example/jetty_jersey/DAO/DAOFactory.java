package com.example.jetty_jersey.DAO;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

//classe d'instanciation de toute les dao et connection a la bdd
public class DAOFactory {

    public static TransportClient connexion;

    public DAOFactory() {
	// TODO Auto-generated constructor stub
    }

    /*
     * 
     * M�thode charg�e de r�cup�rer les informations de connexion � la base de
     * 
     * donn�es, charger le driver JDBC et retourner une instance de la Factory
     * 
     */

    public static DAOFactory getInstance() {

	DAOFactory instance = new DAOFactory();
	return instance;

    }

    /* M�thode charg�e de fournir une connexion � la base de donn�es */

    @SuppressWarnings({ "resource", "unchecked" })
    synchronized public static TransportClient getConnextion() {
	if (connexion == null) {
	    try {
		connexion = new PreBuiltTransportClient(Settings.EMPTY)
			.addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300));
		return connexion;
	    } catch (UnknownHostException e) {

		e.printStackTrace();
		return null;
	    }

	} else
	    return connexion;
    }
    /*
     * 
     * M�thodes de r�cup�ration de l'impl�mentation des diff�rents DAO
     * 
     */

    public ReservationDAO getReservationDAO() {
	return new ReservationDAO(this);
    }

    public PilotDAO getPiloteDAO() {
	return new PilotDAO(this);
    }

    public PassengerDAO getPassengerDAO() {
	return new PassengerDAO(this);
    }

    public FlightDAO getFlightDAO() {
	return new FlightDAO(this);
    }

}