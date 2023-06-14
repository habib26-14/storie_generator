package com.example.stories.controllers;

import com.example.stories.entities.Utilisateur;
import com.example.stories.service.UtilisateurService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.IOException;

public class UtilisateurController {
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField motDePasseField;
    @FXML
    private TextField nomField;
    @FXML
    private TextField prenomField;
    @FXML
    private TextField pseudoField;
    @FXML
    private ToggleGroup group =  new ToggleGroup();;

    private Utilisateur utilisateur;
    private UtilisateurService utilisateurService = new UtilisateurService();


    @FXML
    private void initialize() {

            }


    @FXML
    private void connecterUtilisateur() {
        String email = emailField.getText();
        String motDePasse = motDePasseField.getText();

        // Vérifiez si l'utilisateur existe dans la base de données ou autre logique de vérification
        if (checkCredentials(email, motDePasse)) {
            utilisateur = getUserFromDatabase(email,motDePasse); // Obtenez l'utilisateur à partir de la base de données
            utilisateur.setEstConnecte(true);
            // Ouvrez la vue du contrôleur JoueurController avec l'utilisateur connecté
            openJoueurView(utilisateur);
        } else {
            showAlert(AlertType.ERROR, "Erreur de connexion", "Identifiants invalides");
        }
        // Effacez les champs de texte après la tentative de connexion
        emailField.clear();
        motDePasseField.clear();
    }

    @FXML
    private void sInscrire() {
        String email = emailField.getText();
        String motDePasse = motDePasseField.getText();
        String nom = nomField.getText();
        String prenom = prenomField.getText();
        String pseudo = pseudoField.getText();
        String genre = getSelectedGenre();

        if (email.isEmpty() || motDePasse.isEmpty() || nom.isEmpty() || prenom.isEmpty() || pseudo.isEmpty()) {
            showAlert(AlertType.ERROR, "Erreur d'inscription", "Veuillez remplir tous les champs");
            return;
        }

        Utilisateur nouvelUtilisateur = new Utilisateur(email, nom, prenom, pseudo, genre);
        nouvelUtilisateur.setMotDePasse(motDePasse);

        utilisateurService.inscrire(nouvelUtilisateur);

        showAlert(AlertType.INFORMATION, "Inscription réussie", "Vous êtes maintenant inscrit");

        clearFields();
    }

    private String getSelectedGenre() {
        RadioButton selectedRadioButton = (RadioButton) group.getSelectedToggle();
        if (selectedRadioButton != null) {
            return selectedRadioButton.getText();
        }
        return "";
    }

    private void clearFields() {
        emailField.clear();
        motDePasseField.clear();
        nomField.clear();
        prenomField.clear();
        pseudoField.clear();
        group.selectToggle(null);
    }



    // Méthodes auxiliaires

    private boolean checkCredentials(String email, String motDePasse) {
        return utilisateurService.seconnecter(email, motDePasse) != null;
    }

    private Utilisateur getUserFromDatabase(String email, String motDePasse) {
        return utilisateurService.seconnecter(email, motDePasse);
    }

    private void openJoueurView(Utilisateur utilisateur) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/stories/histoire.fxml"));
            Parent root = loader.load();
            HistoireController histoireController = loader.getController(); // Obtenez le contrôleur JoueurController à partir du loader
            histoireController.setUtilisateur(utilisateur); // Passer l'utilisateur connecté au contrôleur JoueurController
            Stage stage = new Stage();
            stage.setTitle("Vue Histoire");
            Scene scene = new Scene(root, 900, 700);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void inscrire(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/stories/inscription.fxml"));
            //Parent root = loader.load();
            Scene scene = new Scene(loader.load(), 900, 700);
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void afficherInterfaceConnexion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/stories/connexion.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 900, 700);
            Stage stage = (Stage) emailField.getScene().getWindow(); // Obtient la fenêtre actuelle
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
