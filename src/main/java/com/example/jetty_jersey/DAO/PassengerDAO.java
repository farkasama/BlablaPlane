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
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.mindrot.jbcrypt.BCrypt;

import static org.elasticsearch.index.query.QueryBuilders.*;

import static org.elasticsearch.common.xcontent.XContentFactory.*;

import com.example.jetty_jersey.JwTokenHelper;
import com.example.jetty_jersey.model.Connection;
import com.example.jetty_jersey.model.Passenger;

public class PassengerDAO extends DAO<Passenger> {

	public PassengerDAO(DAOFactory f) {
	
	}

	/*
	 * Fonction qui ajoute un utilisateur Passenger dans la base de donnees
	 */
	@Override
	public String put(Passenger obj) {
		TransportClient client = DAOFactory.getConnextion();
		try {
			IndexResponse response = client.prepareIndex("passenger", "_doc")
					.setSource(jsonBuilder().startObject().field("firstName", obj.getFirstName())
							.field("lastName", obj.getLastName()).field("mail", obj.getMail())
							.field("password", BCrypt.hashpw(obj.getPassword(), BCrypt.gensalt())).endObject())
					.get();
			if (response.status() == RestStatus.CREATED) {
				return "{" + "\"status\":\"201\"," + "\"id\":\"" + response.getId() + "\"" + "}";
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "{" + "\"status\":\"500\"," + "\"error\":\"Plane couldnt be created \"" + "}";
	}

	/*
	 * Fonction qui supprime un utilisateur Passenger dans la base de donnees en
	 * fonction de son ID
	 */
	@Override
	public boolean delete(Passenger obj, String idPassenger) {
		TransportClient client = DAOFactory.getConnextion();
		try {
			client.prepareDelete("book", "_doc", idPassenger).execute().actionGet();
			return true;
		} catch (ElasticsearchException e) {
			if (e.status() == RestStatus.CONFLICT)
				e.printStackTrace();
		}
		return false;

	}

	/*
	 * Fonction qui met a jour les informations d'un utilisateur Passenger en
	 * fonction de sont ID
	 */
	@Override
	public boolean update(Passenger obj, String idPassenger) {
		TransportClient client = DAOFactory.getConnextion();
		try {
			client.prepareUpdate("passenger", "_doc", idPassenger)
					.setDoc(jsonBuilder().startObject().field("firstName", obj.getFirstName())
							.field("lastName", obj.getLastName()).field("mail", obj.getMail())
							.field("password", obj.getPassword()).endObject())
					.get();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

	}

	/*
	 * Fonction qui cherche un Passenger en fonction de son ID et le retourne
	 */
	public List<Passenger> get(String id) {
		TransportClient client = DAOFactory.getConnextion();
		GetResponse get = client.prepareGet("passenger", "_doc", id).get();
		ArrayList<Passenger> list = new ArrayList<Passenger>();
		if (get.isSourceEmpty()) {
			return list;
		}
		Map<String, Object> map = get.getSource();
		Passenger p = new Passenger(map.get("firstName").toString(), map.get("lastName").toString(),
				map.get("mail").toString(), map.get("password").toString());
		list.add(p);
		return list;
	}

	/*
	 * Fonction qui retourne un token si les information de connection sont bonnes
	 * 
	 * @c : l'email et le password
	 * 
	 * @return : un string avec un token si l'username existe dans la base de
	 * donnees et le password match
	 */
	public String connect(Connection c) {
		TransportClient client = DAOFactory.getConnextion();

		// Recupere tous les passager
		SearchResponse response = client.prepareSearch("passenger").setTypes("_doc").setQuery(matchAllQuery())
				.setSize(10000).get();

		SearchHit[] result = response.getHits().getHits();

		for (int i = 0; i < result.length; i++) {

			Map<String, Object> map = result[i].getSourceAsMap();

			// Si le mail existe
			if (map.get("mail").toString().equals(c.getMail())) {
				// Si le password correspond retourne le token genere, sinon retourne wrong
				// password
				if (BCrypt.checkpw(c.getPassword(), map.get("password").toString())) {

					String token = JwTokenHelper.getInstance().generatePrivateKey(result[i].getId(), "passenger");

					return "{" + "\"status\":\"200\"," + "\"id\":\"" + token + "\"" + "}";
				}
				return "{" + "\"status\":\"400\"," + "\"error\":\"Wrong password\"" + "}";
			}
		}

		// Si aucun email trouver
		return "{" + "\"status\":\"404\"," + "\"error\":\"User not found \"" + "}";
	}

	/*
	 * Fonction qui regarde dans la base de donnees si l'email est deja pris
	 */
	public boolean checkEmailExist(String mail) {
		TransportClient client = DAOFactory.getConnextion();

		SearchResponse response = client.prepareSearch("passenger").setTypes("_doc").setQuery(matchAllQuery())
				.setSize(10000).get();

		SearchHit[] result = response.getHits().getHits();
		for (int i = 0; i < result.length; i++) {
			Map<String, Object> map = result[i].getSourceAsMap();
			if (map.get("mail").equals(mail)) {
				return true;
			}
		}
		return false;
	}

}