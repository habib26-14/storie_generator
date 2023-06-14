package com.example.stories.service;


import com.example.stories.Dao.UtilisateurDao;
import com.example.stories.Dao.impl.ConnextionImpl;
import com.example.stories.entities.Utilisateur;

public class UtilisateurService {
    private UtilisateurDao utilisateurDao = new ConnextionImpl();
    public void inscrire(Utilisateur utilisateur) { utilisateurDao.sInscrire(utilisateur);}
    public Utilisateur seconnecter(String email, String motDePasse) {return  utilisateurDao.seConnecter(email, motDePasse);}
}
