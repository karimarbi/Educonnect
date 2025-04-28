package com.esprit.Models;

import java.util.ArrayList;
import java.util.List;

public class Categorie {
    private int id;
    private String nomCategorie;
    private List<Cours> cours = new ArrayList<>();

    // Constructors
    public Categorie() {}

    public Categorie(String nomCategorie) {
        this.nomCategorie = nomCategorie;
    }

    public Categorie(int id, String nomCategorie) {
        this.id = id;
        this.nomCategorie = nomCategorie;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomCategorie() {
        return nomCategorie;
    }

    public void setNomCategorie(String nomCategorie) {
        this.nomCategorie = nomCategorie;
    }

    public List<Cours> getCours() {
        return cours;
    }

    public void setCours(List<Cours> cours) {
        this.cours = cours;
    }

    @Override
    public String toString() {
        return nomCategorie;
    }
}