package com.example.jetty_jersey.DAO;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import com.example.jetty_jersey.model.Flight;
import com.example.jetty_jersey.model.InformationReservation;
import com.example.jetty_jersey.model.Passenger;
import com.example.jetty_jersey.model.Reservation;

public class ReservationDAO extends DAO<Reservation> {

	public DAOFactory daofactory;

	public ReservationDAO(DAOFactory f) {
		daofactory = f;
	}

	/*
	 * Ajoute une reservation dans la base de donnees. Verifie avant si le passager
	 * n'a pas de reservation pour le meme vol, si oui alors met a jour le nombre de
	 * place
	 * 
	 * @r : les informations de la reservation (l'id du passenger, l'id du flight,
	 * le nombre de place
	 */
	@Override
	public String put(Reservation r) {
		TransportClient client = DAOFactory.getConnextion();

		// Recupere toutes les reservations
		SearchResponse res = client.prepareSearch("book").setTypes("_doc").setQuery(QueryBuilders.matchAllQuery())
				.setSize(10000).get();

		SearchHit[] result = res.getHits().getHits();

		for (int i = 0; i < result.length; i++) {

			Map<String, Object> map = result[i].getSourceAsMap();

			// Si une reservation existe deja pour le meme flight et le meme passenger alors
			// on ajoute le nombre de place
			if (map.get("idFlight").toString().equals(r.getIdFlight())
					&& map.get("idPassenger").toString().equals(r.getIdPassenger())) {
				int np = Integer.parseInt(map.get("numberPlace").toString()) + r.getNumberPlace();
				try {
					UpdateResponse update = client.prepareUpdate("book", "_doc", result[i].getId()).setDoc(
							jsonBuilder().startObject().field("numberPlace", np).field("confirmed", "0").endObject())
							.get();
					if (update.status() == RestStatus.OK) {
						DAOFactory.getInstance().getFlightDAO().reductionNumberPlace(r.getIdFlight(),
								r.getNumberPlace());
						return "{" + "\"status\":\"201\"," + "\"message\":\"Well booked\"" + "}";
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				return "{" + "\"status\":\"400\"," + "\"error\":\"Can not book the flight\"" + "}";
			}
		}

		// Si aucune reservation trouve alors on l'ajoute dans la base de donnees
		try {
			IndexResponse response = client.prepareIndex("book", "_doc")
					.setSource(jsonBuilder().startObject().field("idFlight", r.getIdFlight())
							.field("idPassenger", r.getIdPassenger()).field("numberPlace", r.getNumberPlace())
							.field("confirmed", "0").endObject())
					.get();
			if (response.status() == RestStatus.CREATED) {
				DAOFactory.getInstance().getFlightDAO().reductionNumberPlace(r.getIdFlight(), r.getNumberPlace());
				return "{" + "\"status\":\"201\"," + "\"message\":\"Well booked\"" + "}";
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "{" + "\"status\":\"400\"," + "\"error\":\"Can not book the flight\"" + "}";
	}

	/*
	 * Supprime la reservation dans la base de donnees
	 */
	@Override
	public boolean delete(Reservation obj, String idReservation) {
		TransportClient client = DAOFactory.getConnextion();
		try {
			obj = this.get(idReservation).get(0);
			DAOFactory.getInstance().getFlightDAO().incrementeNumberPlace(obj.getIdFlight(), obj.getNumberPlace());
			client.prepareDelete("book", "_doc", idReservation).execute().actionGet();
			return true;
		} catch (ElasticsearchException e) {
			if (e.status() == RestStatus.CONFLICT)
				e.printStackTrace();
		}
		return false;
	}

	/*
	 * Confirme la reservation dans la base de donnees
	 */
	@Override
	public boolean update(Reservation r, String idReservation) {
		TransportClient client = DAOFactory.getConnextion();
		try {
			UpdateResponse update = client.prepareUpdate("book", "_doc", idReservation)
					.setDoc(jsonBuilder().startObject().field("confirmed", "1").endObject()).get();
			if (update.status() == RestStatus.OK) {
				
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/*
	 * Retourne la list de flight dont le passenger a reserver
	 */
	public List<Flight> getFlightForPassenger(String idPassenger, String status) {
		TransportClient client = DAOFactory.getConnextion();

		ArrayList<Flight> list = new ArrayList<Flight>();

		// Recupere toutes les reservations
		SearchResponse response = client.prepareSearch("book").setTypes("_doc").setQuery(QueryBuilders.matchAllQuery())
				.setSize(10000).get();

		SearchHit[] result = response.getHits().getHits();

		for (int i = 0; i < result.length; i++) {

			Map<String, Object> map = result[i].getSourceAsMap();

			// Si l'id du passenger corresponds alors on recupere le vol et on l'ajoute dans
			// la list
			if (map.get("idPassenger").toString().equals(idPassenger)
					&& map.get("confirmed").toString().equals(status)) {
				Flight f = DAOFactory.getInstance().getFlightDAO().get(map.get("idFlight").toString()).get(0);
				list.add(f);
			}
		}

		return list;
	}

	/*
	 * Retourne la liste des passagers qui ont reserve pour le vol idFlight
	 */
	public List<Passenger> getPassengerForPilots(String idFlight) {
		TransportClient client = DAOFactory.getConnextion();

		ArrayList<Passenger> list = new ArrayList<Passenger>();
		SearchResponse response = client.prepareSearch("book").setTypes("_doc").setQuery(QueryBuilders.matchAllQuery())
				.setSize(10000).get();

		SearchHit[] result = response.getHits().getHits();

		for (int i = 0; i < result.length; i++) {
			Map<String, Object> map = result[i].getSourceAsMap();

			// Si l'id du flight correspond alors on recupere le passager et on l'ajoute
			if (map.get("idFlight").toString().equals(idFlight)) {
				list.add(daofactory.getPassengerDAO().get(map.get("idPassenger").toString()).get(0));
			}
		}

		return list;
	}

	/*
	 * Retourne la liste de vols qu'a creer le pilot ou il y a au moins une
	 * reservation
	 */
	public List<Flight> getFlightForPilots(String idPilot) {
		TransportClient client = DAOFactory.getConnextion();

		ArrayList<Flight> list = new ArrayList<Flight>();

		SearchResponse response = client.prepareSearch("book").setTypes("_doc").setQuery(QueryBuilders.matchAllQuery())
				.setSize(10000).get();

		SearchHit[] result = response.getHits().getHits();

		for (int i = 0; i < result.length; i++) {
			Map<String, Object> map = result[i].getSourceAsMap();

			String idFlight = map.get("idFlight").toString();

			if (DAOFactory.getInstance().getFlightDAO().getIdPilot(idFlight).equals(idPilot)) {
				boolean b = true;
				for (int j = 0; j < list.size(); j++) {
					if (list.get(j).getIdFlight().equals(idFlight)) {
						b = false;
						break;
					}
				}
				if (b) {
					list.add(DAOFactory.getInstance().getFlightDAO().get(idFlight).get(0));
				}
			}
		}
		return list;
	}

	/*
	 * Retourne la liste des passagers ou
	 */
	public List<InformationReservation> getReservationForPilots(String idPilot) {
		TransportClient client = DAOFactory.getConnextion();

		ArrayList<InformationReservation> list = new ArrayList<InformationReservation>();

		SearchResponse response = client.prepareSearch("book").setTypes("_doc").setQuery(QueryBuilders.matchAllQuery())
				.setSize(10000).get();

		SearchHit[] result = response.getHits().getHits();

		for (int i = 0; i < result.length; i++) {
			Map<String, Object> map = result[i].getSourceAsMap();

			String idFlight = map.get("idFlight").toString();

			if (DAOFactory.getInstance().getFlightDAO().getIdPilot(idFlight).equals(idPilot)) {
				Passenger p = DAOFactory.getInstance().getPassengerDAO().get(map.get("idPassenger").toString()).get(0);
				Flight f = DAOFactory.getInstance().getFlightDAO().get(idFlight).get(0);
				list.add(new InformationReservation(p.getFirstName(), p.getLastName(), f.getDepartureAirport(),
						f.getArrivalAirport(), f.getDate(), map.get("numberPlace").toString(), result[i].getId(),
						map.get("confirmed").toString().equals("0")));
			}
		}
		return list;
	}

	@Override
	public List<Reservation> get(String id) {
		TransportClient client = DAOFactory.getConnextion();
		GetResponse get = client.prepareGet("book", "_doc", id).get();
		ArrayList<Reservation> list = new ArrayList<Reservation>();
		if (get.isSourceEmpty()) {
			return list;
		}
		Map<String, Object> map = get.getSource();
		Reservation r = new Reservation(map.get("idPassenger").toString(), map.get("idFlight").toString(),
				Integer.parseInt(map.get("numberPlace").toString()));
		list.add(r);
		return list;
	}

	public String getIdPassenger(String idReservation) {
		return get(idReservation).get(0).getIdPassenger();
	}

	public List<InformationReservation> getReservationForPassenger(String idPassenger, String status) {

		TransportClient client = DAOFactory.getConnextion();

		ArrayList<InformationReservation> list = new ArrayList<InformationReservation>();

		SearchResponse response = client.prepareSearch("book").setTypes("_doc").setQuery(QueryBuilders.matchAllQuery())
				.setSize(10000).get();

		SearchHit[] result = response.getHits().getHits();

		for (int i = 0; i < result.length; i++) {
			Map<String, Object> map = result[i].getSourceAsMap();
			String idFlight = map.get("idFlight").toString();
			if (map.get("idPassenger").toString().equals(idPassenger) && map.get("confirmed").toString().equals(status)) {

				Passenger p = DAOFactory.getInstance().getPassengerDAO().get(map.get("idPassenger").toString()).get(0);
				
				System.out.println("passenger "+p.getFirstName() +" "+ p.getLastName());
				
				Flight f = DAOFactory.getInstance().getFlightDAO().get(idFlight).get(0);
				
				System.out.println("flight "+f.getDepartureAirport() +" "+ f.getArrivalAirport());

				list.add(new InformationReservation(p.getFirstName(), p.getLastName(), f.getDepartureAirport(),
						f.getArrivalAirport(), f.getDate(), map.get("numberPlace").toString(), result[i].getId(),
						map.get("confirmed").toString().equals(status)));
			}
		}
		return list;

	}
}
