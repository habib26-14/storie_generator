package com.example.stories.controllers;

import com.example.stories.entities.Categorie;
import com.example.stories.entities.Utilisateur;
import com.example.stories.service.CategorieService;
import com.example.stories.service.File.FileCategorie;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CategorieController {
    @FXML
    private TextField idField;
    @FXML
    private TextField nomField;
    @FXML
    private TextField descriptionField;
    @FXML
    private Button ajouterButton;
    @FXML
    private Button modifierButton;
    @FXML
    private Button supprimerButton;
    @FXML
    private TableView<Categorie> categorieTable;
    @FXML
    private TableColumn<Categorie, Integer> idColumn;
    @FXML
    private TableColumn<Categorie, String> nomColumn;
    @FXML
    private TableColumn<Categorie, String> descriptionColumn;
    @FXML
    private Label messageLabel;

    private List<Categorie> categorieList = new ArrayList<>();
    private CategorieService categorieService = new CategorieService();
    private Utilisateur utilisateur = new Utilisateur();
    private FileCategorie fileCategorie = new FileCategorie();

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getId()));
        nomColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getNom()));
        descriptionColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDescription()));

        categorieTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                fillFieldsWithSelectedCategorie(newSelection);
            } else {
                clearFields();
            }
        });

        // Exemple de données de catégorie pour les tests
        categorieList = FXCollections.observableArrayList(categorieService.findAll(utilisateur.getId()));

        categorieTable.setItems((ObservableList<Categorie>)categorieList);
    }

    @FXML
    private void handleAjouter(ActionEvent event) {
        String nom = nomField.getText();
        String description = descriptionField.getText();

        Categorie categorie = new Categorie(nom, description);
        categorieService.save(categorie, utilisateur.getId());
        categorieList.add(categorie);
        clearFields();
        messageLabel.setText("Catégorie ajoutée avec succès.");
    }

    @FXML
    private void handleModifier(ActionEvent event) {
        Categorie selectedCategorie = categorieTable.getSelectionModel().getSelectedItem();
        if (selectedCategorie != null) {
            String nom = nomField.getText();
            String description = descriptionField.getText();
            selectedCategorie.setNom(nom);
            selectedCategorie.setDescription(description);

            categorieService.update(selectedCategorie, selectedCategorie.getId(), utilisateur.getId());
            categorieTable.refresh();
            clearFields();
            messageLabel.setText("Catégorie modifiée avec succès.");
        } else {
            messageLabel.setText("Aucune catégorie sélectionnée.");
        }
    }

    @FXML
    private void handleSupprimer(ActionEvent event) {
        Categorie selectedCategorie = categorieTable.getSelectionModel().getSelectedItem();
        if (selectedCategorie != null) {
            categorieService.delete(selectedCategorie, utilisateur.getId());
            categorieList.remove(selectedCategorie);
            clearFields();
            messageLabel.setText("Catégorie supprimée avec succès.");
        } else {
            messageLabel.setText("Aucune catégorie sélectionnée.");
        }
    }

    @FXML
    private void handleSelection(ActionEvent event) {
        Categorie selectedCategorie = categorieTable.getSelectionModel().getSelectedItem();
        if (selectedCategorie != null) {
            fillFieldsWithSelectedCategorie(selectedCategorie);
        }else {
            clearFields();
            messageLabel.setText("Aucun joueur sélectionné.");
        }
    }

    private void fillFieldsWithSelectedCategorie(Categorie categorie) {
        nomField.setText(categorie.getNom());
        descriptionField.setText(categorie.getDescription());
    }

    @FXML
    private void clearFields() {
        nomField.clear();
        descriptionField.clear();
    }

    @FXML
    private void handleActualiser(ActionEvent event) {
        categorieList = FXCollections.observableArrayList(categorieService.findAll(utilisateur.getId()));
        categorieTable.setItems((ObservableList<Categorie>) categorieList);
    }
    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public void Fermer(ActionEvent actionEvent) {
        // Fermer la fenêtre actuelle
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }

    public void Generateurs(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/stories/Generateur.fxml"));
            //Parent root = loader.load();
            StoryGeneratorController histoireController = loader.getController(); // Obtenez le contrôleur JoueurController à partir du loader
            // Passer l'utilisateur connecté au contrôleur JoueurController
            Stage stage = new Stage();
            stage.setTitle("Generateur Aleatoire d'histoire");
            Scene scene = new Scene(loader.load(), 900, 700);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Histoire(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/stories/histoire.fxml"));
            Parent root = loader.load();
            HistoireController histoireController = loader.getController(); // Obtenez le contrôleur JoueurController à partir du loader
            histoireController.setUtilisateur(utilisateur); // Passer l'utilisateur connecté au contrôleur JoueurController
            Stage stage = new Stage();
            stage.setTitle("Vue Histoire");
            Scene scene = new Scene(root, 900, 700);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exporterEnJSON(ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Exporter un fichier JSON");
        dialog.setHeaderText(null);
        dialog.setContentText("Entrez le nom du fichier JSON :");
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(nomFichier -> {
            // Appel à la méthode du service pour lire le fichier JSON
            fileCategorie.ecrireFichierJson(nomFichier, utilisateur.getId());
        });
    }
    public void exporterEnExcel(ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Exporter un fichier Excel");
        dialog.setHeaderText(null);
        dialog.setContentText("Entrez le nom du fichier Excel :");
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(nomFichier -> {
            // Appel à la méthode du service pour lire le fichier Excel
            fileCategorie.ecrireFichierExcel(nomFichier, utilisateur.getId());
        });
    }
    public void exporterEnTexte(ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Importer un fichier texte");
        dialog.setHeaderText(null);
        dialog.setContentText("Entrez le nom du fichier texte :");
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(nomFichier -> {
            // Appel à la méthode du service pour lire le fichier texte
            fileCategorie.ecrireFichierTexte(nomFichier, utilisateur.getId());
        });
    }
    @FXML
    private void importerFichierTexte() {
        TextInputDialog dialog = new TextInputDialog("C:\\Users\\htano\\Downloads");
        dialog.setTitle("Importer un fichier texte");
        dialog.setHeaderText(null);
        dialog.setContentText("Entrez le nom du fichier texte :");
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(nomFichier -> {
            // Appel à la méthode du service pour lire le fichier texte
            fileCategorie.lireFichierTexte(nomFichier, utilisateur.getId());
        });
    }
    @FXML
    private void importerFichierJSON() {
        TextInputDialog dialog = new TextInputDialog("C:\\Users\\htano\\Downloads");
        dialog.setTitle("Importer un fichier JSON");
        dialog.setHeaderText(null);
        dialog.setContentText("Entrez le nom du fichier JSON :");
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(nomFichier -> {
            // Appel à la méthode du service pour lire le fichier JSON
            fileCategorie.lireFichierJson(nomFichier, utilisateur.getId());
        });
    }
    @FXML
    private void importerFichierExcel() {
        TextInputDialog dialog = new TextInputDialog("C:\\Users\\htano\\Downloads");
        dialog.setTitle("Importer un fichier Excel");
        dialog.setHeaderText(null);
        dialog.setContentText("Entrez le nom du fichier Excel :");
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(nomFichier -> {
            // Appel à la méthode du service pour lire le fichier Excel
            fileCategorie.lireFichierExcel(nomFichier, utilisateur.getId());
        });
    }
}