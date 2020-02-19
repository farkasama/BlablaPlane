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

import static org.elasticsearch.common.xcontent.XContentFactory.*;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

import com.example.jetty_jersey.JwTokenHelper;
import com.example.jetty_jersey.model.Connection;
import com.example.jetty_jersey.model.Pilot;

public class PilotDAO extends DAO<Pilot> {

	public DAOFactory daofactory;

	public PilotDAO(DAOFactory f) {
		daofactory = f;
	}

	/*
	 * Ajoute un pilote dans la base de donnees
	 */
	@Override
	public String put(Pilot obj) {
		TransportClient client = DAOFactory.getConnextion();
		try {
			String certificate = obj.getCertificate();
			if (obj.getCertificate() == null)
				certificate = "aucune";
			IndexResponse response = client.prepareIndex("pilot", "_doc")
					.setSource(jsonBuilder().startObject().field("lastName", obj.getLastName())
							.field("firstName", obj.getFirstName()).field("mail", obj.getMail())
							.field("password", BCrypt.hashpw(obj.getPassword(), BCrypt.gensalt()))
							.field("experience", obj.getExperience()).field("certificate", certificate).endObject())
					.get();
			if (response.status() == RestStatus.CREATED) {
				return "{" + "\"status\":\"201\"," + "\"id\":\"" + response.getId() + "\"" + "}";
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "{" + "\"status\":\"500\"," + "\"error\":\"User couldnt be created \"" + "}";
	}

	/*
	 * Supprime un pilot dans la base de donnees
	 */
	@Override
	public boolean delete(Pilot obj, String idPilot) {
		TransportClient client = DAOFactory.getConnextion();
		try {
			client.prepareDelete("book", "_doc", idPilot).execute().actionGet();
			return true;
		} catch (ElasticsearchException e) {
			if (e.status() == RestStatus.CONFLICT)
				e.printStackTrace();
		}
		return false;

	}

	/*
	 * Met a jour les informations d'un pilot dans la base de donnees
	 */
	@Override
	public boolean update(Pilot obj, String idPilot) {
		TransportClient client = DAOFactory.getConnextion();
		try {
			client.prepareUpdate("passenger", "_doc", idPilot)
					.setDoc(jsonBuilder().startObject().field("lastName", obj.getLastName())
					.field("firstName", obj.getLastName()).field("mail", obj.getMail())
					.field("password", obj.getPassword()).field("experience", obj.getExperience())
					.field("certificate", obj.getCertificate()).endObject())
					.get();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/*
	 * Retourne le pilot qui correspond a son ID
	 */
	public List<Pilot> get(String id) {
		TransportClient client = DAOFactory.getConnextion();
		GetResponse get = client.prepareGet("pilot", "_doc", id).get();
		ArrayList<Pilot> list = new ArrayList<Pilot>();
		if (get.isSourceEmpty()) {
			return list;
		}
		Map<String, Object> map = get.getSource();
		Pilot p = new Pilot(map.get("firstName").toString(), map.get("lastName").toString(), map.get("mail").toString(),
				map.get("password").toString(), Integer.parseInt(map.get("experience").toString()),
				map.get("certificate").toString());
		list.add(p);
		return list;
	}

	/*
	 * Retourne le token de connection si les informations sont bonnes
	 * 
	 * @c : l'email et le password du pilot
	 * 
	 * @return : le token si les informations sont bonnes, un message d'erreur sinon
	 */
	public String connect(Connection c) {
		TransportClient client = DAOFactory.getConnextion();

		// Recupere tous les pilots
		SearchResponse response = client.prepareSearch("pilot").setTypes("_doc").setQuery(matchAllQuery())
				.setSize(10000).get();

		SearchHit[] result = response.getHits().getHits();

		for (int i = 0; i < result.length; i++) {

			Map<String, Object> map = result[i].getSourceAsMap();

			// Si l'email existe
			if (map.get("mail").toString().equals(c.getMail())) {

				// Si le password match alors retourne le token genere, un message d'erreur
				// sinon
				if (BCrypt.checkpw(c.getPassword(), map.get("password").toString())) {
					String token = JwTokenHelper.getInstance().generatePrivateKey(result[i].getId(), "pilot");
					return "{" + "\"status\":\"200\"," + "\"id\":\"" + token + "\"" + "}";
				}
				return "{" + "\"status\":\"400\"," + "\"error\":\"Wrong password\"" + "}";
			}
		}
		return "{" + "\"status\":\"404\"," + "\"error\":\"User not found \"" + "}";
	}

	/*
	 * Fonction qui regarde si l'email existe deja dans la base de donnees
	 */
	public boolean checkEmailExist(String mail) {
		TransportClient client = DAOFactory.getConnextion();

		SearchResponse response = client.prepareSearch("pilot").setTypes("_doc").setQuery(matchAllQuery())
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