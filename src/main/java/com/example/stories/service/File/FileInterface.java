package com.example.stories.service.File;

public interface FileInterface {
    void lireFichierTexte(String nomFichier, int idUser);
    void ecrireFichierTexte(String nomFichier, int idUser);
    void lireFichierExcel(String nomFichier, int idUser);
    void ecrireFichierExcel(String nomFichier, int idUser);
    void lireFichierJson(String nomFichier, int idUser);
    void ecrireFichierJson(String nomFichier, int idUser);
}
