package com.example.stories.Dao;

import com.example.stories.entities.Categorie;
import com.example.stories.entities.Histoire;

import java.util.List;

public interface HistoireDao {
    void save(Histoire histoire, int idUser);
    void update(Histoire histoire, int id, int idUser);
    void delete(int id, int idUser);
    Histoire findById(int id, int idUser);
    List<Histoire> findAll(int idUser);
    List<Histoire> findByCategorie(Categorie categorie, int idUser);
    List<Histoire> AllStories();
}
