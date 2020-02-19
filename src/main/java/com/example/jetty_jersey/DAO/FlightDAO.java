package com.example.jetty_jersey.DAO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;

import com.example.jetty_jersey.model.*;
import static org.elasticsearch.common.xcontent.XContentFactory.*;

public class FlightDAO extends DAO<Flight> {

	public static List<Flight> list = new ArrayList<Flight>();
	// un fois la bdd est en place
	public DAOFactory daofactory;

	public FlightDAO(DAOFactory f) {

		daofactory = f;
	}

	/*
	 * Fonction qui ajoute un vol dans la base de donnees
	 * 
	 * @obj : l'objet Flight
	 * 
	 * @return : un string pour le JS
	 */
	public String put(Flight obj) {
		TransportClient client = DAOFactory.getConnextion();
		try {

			String modele = obj.getModelePlane();
			if (modele == null) {
				modele = "avion";
			}
			IndexResponse response = client.prepareIndex("flight", "_doc")
					.setSource(jsonBuilder().startObject().field("date", obj.getDate())
							.field("departureAirport", obj.getDepartureAirport())
							.field("arrivalAirport", obj.getArrivalAirport()).field("travelTime", obj.getTravelTime())
							.field("price", obj.getPrice()).field("time", obj.getTimep()).field("rdv", obj.getRdv())
							.field("modelePlane", modele).field("pilot", obj.getPilot().getFirstName())
							.field("seatLeft", obj.getSeatLeft()).endObject())
					.get();
			if (response.status() == RestStatus.CREATED) {
				return "{" + "\"status\":\"201\"," + "\"id\":\"" + response.getId() + "\"" + "}";
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "{" + "\"status\":\"500\"," + "\"error\":\"Flight couldnt be created \"" + "}";
	}

	/*
	 * Suppression d'un vol dans la base de donnees
	 * 
	 * @obj : l'objet flight
	 * 
	 * @idFlight : l'id du vol
	 * 
	 * @return : true si bien supprime, false sinon
	 */
	@Override
	public boolean delete(Flight obj, String idFlight) {
		TransportClient client = DAOFactory.getConnextion();
		try {
			client.prepareDelete("book", "_doc", idFlight).execute().actionGet();
			return true;
		} catch (ElasticsearchException e) {
			if (e.status() == RestStatus.CONFLICT)
				e.printStackTrace();
		}
		return false;
	}

	/*
	 * Mise a jour des information d'un vol dans la base de donnees
	 * 
	 * @obj : l'objet flight
	 * 
	 * @idFlight : l'id du vol
	 * 
	 * @return : true si bien supprime, false sinon
	 */
	@Override
	public boolean update(Flight obj, String idFlight) {
		TransportClient client = DAOFactory.getConnextion();
		try {
			client.prepareUpdate("flight", "_doc", idFlight)
					.setDoc(jsonBuilder().startObject().field("date", obj.getDate())
							.field("departureAirport", obj.getDepartureAirport())
							.field("arrivalAirport", obj.getArrivalAirport()).field("travelTime", obj.getTravelTime())
							.field("price", obj.getPrice()).field("time", obj.getTimep()).field("rdv", obj.getRdv())
							.field("pilot", obj.getPilot()).field("seatLeft", obj.getSeatLeft()).endObject())
					.get();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/*
	 * Retourne un vol en fonction de son id
	 * 
	 * @id : l'ID du vol
	 * 
	 * @return : une liste de 1 element contenant le vol
	 */
	@Override
	public List<Flight> get(String id) {
		TransportClient client = DAOFactory.getConnextion();
		ArrayList<Flight> list = new ArrayList<Flight>();
		GetResponse get = client.prepareGet("flight", "_doc", id).get();
		if (get.isSourceEmpty()) {
			return list;
		}
		Map<String, Object> map = get.getSource();

		// Juste un nouveau Flight avec ses attribut
		Flight f = new Flight(get.getId(), map.get("date").toString(), map.get("departureAirport").toString(),
				map.get("arrivalAirport").toString(), Double.valueOf(map.get("travelTime").toString()),
				Double.valueOf(map.get("price").toString()), map.get("time").toString(),
				map.get("modelePlane").toString(),
				DAOFactory.getInstance().getPiloteDAO().get(map.get("pilot").toString()).get(0),
				Integer.parseInt(map.get("seatLeft").toString()), map.get("rdv").toString());
		list.add(f);
		return list;
	}

	/*
	 * Retourne une liste de vols en fonction de la recherche
	 * 
	 * @r : les attribut recherche (l'aeroport de depart, d'arrive et la date)
	 * 
	 * @return : la liste des vols qui correspond a la recherche
	 */
	public List<Flight> get(Recherche r) {
		TransportClient client = DAOFactory.getConnextion();

		// Recupere toutes les lignes dans la base de donnees dans la table flight
		SearchResponse response = client.prepareSearch("flight").setTypes("_doc")
				.setQuery(QueryBuilders.matchAllQuery()).setSize(10000).get();

		SearchHit[] result = response.getHits().getHits();

		ArrayList<Flight> list = new ArrayList<Flight>();

		// Pour tous les resultats de la recherche
		for (SearchHit sh : result) {

			Map<String, Object> map = sh.getSourceAsMap();

			// Si la ligne a la meme date, aeroport d'arrive et de depart alors on ajoute
			// dans la liste le vol
			if (map.get("date").equals(r.getDate()) && map.get("departureAirport").equals(r.getDeparture())
					&& map.get("arrivalAirport").equals(r.getArrival())
					&& Integer.parseInt(map.get("seatLeft").toString()) > 0) {

				Flight f = new Flight(sh.getId(), map.get("date").toString(), map.get("departureAirport").toString(),
						map.get("arrivalAirport").toString(), Double.valueOf(map.get("travelTime").toString()),
						Double.valueOf(map.get("price").toString()), map.get("time").toString(),
						map.get("modelePlane").toString(),
						DAOFactory.getInstance().getPiloteDAO().get(map.get("pilot").toString()).get(0),
						Integer.parseInt(map.get("seatLeft").toString()), map.get("rdv").toString());
				list.add(f);
			}
		}
		return list;

	}

	/*
	 * Fonction qui recupere tous les vols creer par le pilot
	 * 
	 * @idPilot : l'id du pilot
	 * 
	 * @return : la list des vols
	 */
	public List<Flight> getFlightsForPilots(String idPilot) {
		TransportClient client = DAOFactory.getConnextion();

		ArrayList<Flight> list = new ArrayList<Flight>();

		// Recupere toutes les lignes de la table flight
		SearchResponse response = client.prepareSearch("flight").setTypes("_doc")
				.setQuery(QueryBuilders.matchAllQuery()).setSize(10000).get();

		SearchHit[] result = response.getHits().getHits();

		// Pour toutes les lignes
		for (int i = 0; i < result.length; i++) {
			Map<String, Object> map = result[i].getSourceAsMap();

			// Si l'ID du pilot correspond a celui qui est enregistrer alors on ajoute dans
			// la list
			if (map.get("pilot").toString().equals(idPilot)) {

				Flight f = new Flight(result[i].getId(), map.get("date").toString(),
						map.get("departureAirport").toString(), map.get("arrivalAirport").toString(),
						Double.valueOf(map.get("travelTime").toString()), Double.valueOf(map.get("price").toString()),
						map.get("time").toString(), map.get("modelePlane").toString(),
						DAOFactory.getInstance().getPiloteDAO().get(map.get("pilot").toString()).get(0),
						Integer.parseInt(map.get("seatLeft").toString()), map.get("rdv").toString());

				list.add(f);
			}
		}

		return list;
	}

	/*
	 * Fonction qui reduit le nombre de place disponible dans un vol apres
	 * reservation
	 * 
	 * @idFlight : l'id du vol
	 * 
	 * @numberPlace : le nombre de place a reduire
	 * 
	 * @return : rien
	 */
	public void reductionNumberPlace(String idFlight, int numberPlace) {
		Flight f = get(idFlight).get(0);

		TransportClient client = DAOFactory.getConnextion();
		try {
			client.prepareUpdate("flight", "_doc", idFlight)
					.setDoc(jsonBuilder().startObject().field("seatLeft", f.getSeatLeft() - numberPlace).endObject())
					.get();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void incrementeNumberPlace(String idFlight, int numberPlace) {
		Flight f = get(idFlight).get(0);

		TransportClient client = DAOFactory.getConnextion();
		try {
			client.prepareUpdate("flight", "_doc", idFlight)
					.setDoc(jsonBuilder().startObject().field("seatLeft", f.getSeatLeft() + numberPlace).endObject())
					.get();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getIdPilot(String idFlight) {
		TransportClient client = DAOFactory.getConnextion();

		GetResponse get = client.prepareGet("flight", "_doc", idFlight).get();

		Map<String, Object> map = get.getSource();

		return map.get("pilot").toString();
	}

}