package com.example.jetty_jersey;

import java.security.Key;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class JwTokenHelper {

    private static JwTokenHelper jwTokenHelper = null;

    //Le nombre de minute avant que le token ne soit plus valide
    private static final long EXPIRATION_LIMIT = 30;

    //Encodage du token
    private Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    private JwTokenHelper() {}

    //Creation de la classe en singleton pour que la signature du token soit le meme durant que le serveur est lance
    synchronized public static JwTokenHelper getInstance() {
	if (jwTokenHelper == null)
	    jwTokenHelper = new JwTokenHelper();
	return jwTokenHelper;
    }

    /*
     * Fonction qui genere le token
     * @id : l'id de l'utilisateur
     * @user : si l'utilisateur est un "passenger" ou "pilot"
     * @return : le string generer
     */
    public String generatePrivateKey(String id, String user) {
	return Jwts.builder().setId(id).setExpiration(getExpirationDate()).claim("user", user).signWith(key).compact();
    }

    /*
     * Fonction qui genere la date de fin de vie du token
     * @return : le jour et l'heure actuelle + 30 min
     */
    private Date getExpirationDate() {
	long currentTimeInMillis = System.currentTimeMillis();
	long expMilliSeconds = TimeUnit.MINUTES.toMillis(EXPIRATION_LIMIT);
	return new Date(currentTimeInMillis + expMilliSeconds);
    }

    /*
     * Fonction qui retourne l'id dans le token
     * @token : le token
     * @return : l'id contenu dans le token
     */
    public String getIdFromToken(String token) {
	return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().getId();
    }

    /*
     * Fonction qui retourne si l'utilisateur est un pilot ou un passenger
     * @token : le token
     * @return : "pilot" ou "passenger"
     */
    public String getUserType(String token) {
	return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().get("user").toString();
    }

    /*
     * Fonction qui dit si le token est encore valide ou pas
     * @token : le token
     * @return : true si le token est valide, false sinon
     */
    public boolean isTokenInvalid(String token) {
	try {
	    Jwts.parser().setSigningKey(key).parseClaimsJws(token);
	    return false;
	} catch (Exception e) {
	    return true;
	}
    }
}