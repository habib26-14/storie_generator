package com.example.stories.entities;

import java.util.Objects;

public class Categorie {
    private int id;
    private String nom;
    private String Description;

    public Categorie() {
    }

    public Categorie(String nom, String description) {
        this.id = id;
        this.nom = nom;
        Description = description;
    }
    public Categorie(int id, String nom, String description) {
        this.id = id;
        this.nom = nom;
        Description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    public String getDescription() {
        return Description;
    }
    public void setDescription(String description) {
        Description = description;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Categorie)) return false;
        Categorie categorie = (Categorie) o;
        return Objects.equals(getNom(), categorie.getNom()) && Objects.equals(getDescription(), categorie.getDescription());
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, getNom(), getDescription());
    }

    @Override
    public String toString() {
        return  nom;
    }
}
