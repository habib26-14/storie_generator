package com.example.stories.Dao;

import com.example.stories.entities.Utilisateur;

public interface UtilisateurDao {
    Utilisateur seConnecter(String email, String motDePasse);
    void sInscrire(Utilisateur utilisateur);
}
