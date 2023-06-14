package com.example.stories.service;

import com.example.stories.Dao.CategorieDao;
import com.example.stories.Dao.impl.CategorieDaoImp;
import com.example.stories.entities.Categorie;

import java.util.List;

public class CategorieService {
    private CategorieDao categorieDao = new CategorieDaoImp();

    public List<Categorie> findAll(int idUser) {
        return categorieDao.findAll(idUser);
    }
    public Categorie findById(int id, int idUser) {
        return categorieDao.findById(id, idUser);
    }
    public Categorie findByName(String nom, int idUser) {
        return categorieDao.findByName(nom, idUser);
    }
    public void save(Categorie categorie, int idUser) {
        categorieDao.save(categorie, idUser);
    }
    public void update(Categorie categorie, int i, int idUser) {
        categorieDao.update(categorie, i, idUser);
    }
    public void delete(Categorie categorie, int idUser) {
        categorieDao.delete(categorie.getNom(), idUser);
    }
}
