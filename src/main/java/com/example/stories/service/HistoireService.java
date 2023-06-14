package com.example.stories.service;

import com.example.stories.Dao.HistoireDao;
import com.example.stories.Dao.impl.HistoireDaoImpl;
import com.example.stories.entities.Categorie;
import com.example.stories.entities.Histoire;

import java.util.List;

public class HistoireService {
    private HistoireDao histoireDao = new HistoireDaoImpl();

    public List<Histoire> findAll(int idUser) {
        return histoireDao.findAll(idUser);
    }
    public List<Histoire> findByCategorie(Categorie c, int idUser) {
        return histoireDao.findByCategorie(c, idUser);
    }
    public Histoire findById(int id, int idUser) {
        return histoireDao.findById(id, idUser);
    }

    public void save(Histoire histoire, int idUser) {
        histoireDao.save(histoire, idUser);
    }
    public void update(Histoire histoire, int id, int idUser) {
        histoireDao.update(histoire, id, idUser);
    }
    public void remove(int i, int idUser) {
        histoireDao.delete(i, idUser);
    }

    public List<Histoire> getAllHistoires() {
        return histoireDao.AllStories();
    }
}
