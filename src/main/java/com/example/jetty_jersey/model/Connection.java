package com.example.jetty_jersey.model;

/*
 * Class qui parse les information pour la connexion
 */
public class Connection {
    private String mail;
    private String password;

    public Connection() {

    }

    public Connection(String mail, String password) {
	this.mail = mail;
	this.password = password;
    }

    public String getMail() {
	return this.mail;
    }

    public void setMail(String mail) {
	this.mail = mail;
    }

    public String getPassword() {
	return this.password;
    }

    public void setPassword(String password) {
	this.password = password;
    }

}
