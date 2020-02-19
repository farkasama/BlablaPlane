package com.example.jetty_jersey.model;

/*
 * Class qui parse les information pour les pilots
 */
public class Pilot {
    
    private String firstName;
    private String lastName;
    private String mail;
    private String password;
    private int experience;
    private String certificate;

    public Pilot(String firstName, String lastName, String mail, String password, int experience, String certificate) {
	super();
	this.firstName = firstName;
	this.lastName = lastName;
	this.mail = mail;
	this.password = password;
	this.experience = experience;
	this.certificate = certificate;
    }

    public Pilot() {
	// TODO Auto-generated constructor stub
    }

    public int getExperience() {
	return experience;
    }

    public void setExperience(int experience) {
	this.experience = experience;
    }

    public String getCertificate() {
	return certificate;
    }

    public void setCertificate(String certificate) {
	this.certificate = certificate;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}