package com.example.stories.controllers;

import com.example.stories.entities.Histoire;
import com.example.stories.entities.Utilisateur;
import com.example.stories.service.File.FileService;
import com.example.stories.service.HistoireService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.example.stories.service.File.FileService.speak;

public class StoryGeneratorController {
    @FXML
    private Label titleLabel;
    @FXML
    private Label contentLabel;
    private Thread soundThread;


    private HistoireService histoireService = new HistoireService();
    private List<Histoire> histoireList = histoireService.getAllHistoires();
    private static int i = 0;

    public void generateStory() {
        // Vérifier si la liste d'histoires n'est pas vide après récupération
        if (!histoireList.isEmpty()) {
            // Générer un index aléatoire pour sélectionner une histoire de la liste
            Random random = new Random();
            int index = random.nextInt(histoireList.size());
            i = index;
            // Récupérer l'histoire sélectionnée
            Histoire histoire = histoireList.get(index);
            // Obtenir le titre et le contenu de l'histoire
            String title = histoire.getTitre();
            String content = histoire.getContenu();

            // Afficher le titre et le contenu de l'histoire
            titleLabel.setText(title);
            contentLabel.setText(content);
        } else {
            // Si la liste d'histoires est toujours vide, afficher un message d'erreur
            titleLabel.setText("Aucune histoire disponible");
            contentLabel.setText("");
        }
    }
    public void playSound(ActionEvent actionEvent) {
        if (soundThread == null || !soundThread.isAlive()) {
            System.out.println("Lecture du contenu de l'histoire...");
            soundThread = new Thread(() -> {
                speak(histoireList.get(i).getContenu());
            });
            soundThread.start();
        }
    }

    public void afficherInterfaceConnexion(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/stories/connexion.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 900, 700);
            Stage stage = (Stage) titleLabel.getScene().getWindow(); // Obtient la fenêtre actuelle
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}