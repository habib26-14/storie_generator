package com.example.stories.Dao;

import com.example.stories.entities.Categorie;

import java.util.List;

public interface CategorieDao {
    void save(Categorie story, int idUser);
    void update(Categorie story, int id, int idUser);
    void delete(String nom, int idUser);
    Categorie findById(int id, int idUser);
    Categorie findByName(String nom, int idUser);
    List<Categorie> findAll(int idUser);
}
