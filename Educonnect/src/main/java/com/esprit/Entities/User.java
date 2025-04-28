package com.esprit.Entities;
import com.esprit.Models.Cours;
import com.esprit.Models.Event;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class User {
    private String photoUrl;
    private int id;
    private String status;
    private String nom;
    private String prenom;
    private int CIN ;
    private String email;
    private LocalDateTime createdAt;
    private String password;
    private Date DateNss ;
    private String lieu;
    private String adresse;
    private int telephone;
    private UserRole role ;
    private float salaire;
    private String specialite;
    private Set<Event> registeredEvents = new HashSet<>(); // New field for events
    private Set<Cours> registeredCourses = new HashSet<>(); // Add this field


    // Default constructor
    public User() {}
    // Constructor without id (for new users where id is auto-generated)
    public User(String nom, String email, String password, Date DateNss, UserRole role) {
        this.nom = nom;
        this.email = email;
        this.password = password;
        this.DateNss = DateNss;
        this.role = role;
    }
    // Constructor for Google User Data

    public String getPhotoUrl() {
        return photoUrl;
    }
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    // Constructor with id (for users already persisted)
    public User(int id, String photoUrl, String status, String nom, String prenom, int CIN, String email, Date DateNss, int telephone, String lieu, String adresse , UserRole role, float salaire, String specialite ){
        this.id=id;
        this.photoUrl=photoUrl;
        this.status=status;
        this.nom=nom;
        this.prenom=prenom;
        this.CIN=CIN;
        this.email=email;
        this.DateNss=DateNss;
        this.telephone=telephone;
        this.lieu=lieu;
        this.adresse=adresse;
        this.role=role;
        this.salaire=salaire;
        this.specialite=specialite;

    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getNom() {
        return nom;

    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    public String getPrenom() {
        return prenom;
    }
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }
    public int getCIN() {
        return CIN;
    }
    public void setCIN(int CIN) {
        this.CIN = CIN;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public Date getDateNss() {
        return DateNss;
    }
    public void setDateNss(Date dateNss) {
        this.DateNss = dateNss;
    }
    public String getLieu() {
        return lieu;
    }
    public void setLieu(String lieu) {
        this.lieu = lieu;
    }
    public String getAdresse() {
        return adresse;

    }
    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }
    public int getTelephone() {
        return telephone;
    }
    public void setTelephone(int telephone) {
        this.telephone = telephone;
    }

    public float getSalaire() {
        return salaire;
    }
    public void setSalaire(float salaire) {
        this.salaire = salaire;
    }
    public String getSpecialite() {
        return specialite;
    }
    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
    public void registerForEvent(Event event) {
        this.registeredEvents.add(event);
    }

    // Add this new method to unregister from an event
    public void unregisterFromEvent(Event event) {
        this.registeredEvents.remove(event);
    }

    // Add this getter for registered events
    public Set<Event> getRegisteredEvents() {
        return registeredEvents;
    }
    // Add these methods
    public void registerForCourse(Cours cours) {
        this.registeredCourses.add(cours);
    }

    public void unregisterFromCourse(Cours cours) {
        this.registeredCourses.remove(cours);
    }

    public Set<Cours> getRegisteredCourses() {
        return registeredCourses;
    }

    public boolean isRegisteredForCourse(Cours cours) {
        return registeredCourses.contains(cours);
    }

    public boolean hasCourseWithSameCategory(Cours cours) {
        if (cours.getCategorie() == null) return false;
        return registeredCourses.stream()
                .anyMatch(registered -> registered.getCategorie() != null &&
                        registered.getCategorie().getId() == cours.getCategorie().getId());
    }
    @Override
    public String toString(){
        return "User{"+"id="+id+ ", nom="+nom+", prenom="+prenom+", CIN="+CIN+ ",lieu="+lieu+ ",adresse="+adresse +  ", email="+email+", password="+password+", Date de naissance ="+DateNss+", telephone ="+telephone+", role="+role+ '}';

    }
    // Event registration methods


    public boolean isRegisteredForEvent(Event event) {
        return registeredEvents.contains(event);
    }

    public boolean hasTimeConflictWithRegisteredEvents(Event newEvent) {
        return registeredEvents.stream()
                .anyMatch(existingEvent -> existingEvent.hasTimeConflict(newEvent));
    }
}