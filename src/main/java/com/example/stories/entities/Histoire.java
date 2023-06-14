package com.example.stories.entities;

import java.util.Date;
import java.util.Objects;

public class Histoire {
    private int id;
    private String titre;
    private String auteur;
    private String contenu;
    private int nbPages;
    private double prix;
    private Categorie categories;
    private Date dates;

    public Histoire() {
    }

    public Histoire(String titre, String auteur, String contenu, int nbPages, double prix, Categorie categories, Date dates) {
        this.titre = titre;
        this.auteur = auteur;
        this.contenu = contenu;
        this.nbPages = nbPages;
        this.prix = prix;
        this.categories = categories;
        this.dates = dates;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public int getNbPages() {
        return nbPages;
    }

    public void setNbPages(int nbPages) {
        this.nbPages = nbPages;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public Categorie getCategories() {
        return categories;
    }

    public void setCategories(Categorie categories) {
        this.categories = categories;
    }

    public Date getDate() {
        return dates;
    }

    public void setDate(Date dates) {
        this.dates = dates;
    }

    @Override
    public String toString() {
        return "Histoire{" +
                "titre='" + titre + '\'' +
                ", auteur='" + auteur + '\'' +
                ", contenu='" + contenu + '\'' +
                ", nbPages=" + nbPages +
                ", prix=" + prix +
                ", categories=" + categories +
                ", date=" + dates +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Histoire)) return false;
        Histoire histoire = (Histoire) o;
        return getNbPages() == histoire.getNbPages() && Double.compare(histoire.getPrix(), getPrix()) == 0 && Objects.equals(getTitre(), histoire.getTitre()) && Objects.equals(getAuteur(), histoire.getAuteur()) && Objects.equals(getContenu(), histoire.getContenu()) && Objects.equals(getCategories(), histoire.getCategories()) && Objects.equals(getDate(), histoire.getDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, getTitre(), getAuteur(), getContenu(), getNbPages(), getPrix(), getCategories(), getDate());
    }
}
