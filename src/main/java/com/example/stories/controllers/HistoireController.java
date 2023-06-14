package com.example.stories.controllers;

import com.example.stories.entities.Categorie;
import com.example.stories.entities.Histoire;
import com.example.stories.entities.Utilisateur;
import com.example.stories.service.CategorieService;
import com.example.stories.service.File.FileService;
import com.example.stories.service.HistoireService;
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
import javafx.util.StringConverter;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class HistoireController {
    @FXML
    private TextField titreField;
    @FXML
    private TextField auteurField;
    @FXML
    private TextArea contenuArea;
    @FXML
    private TextField nbPagesField;
    @FXML
    private TextField prixField;
    @FXML
    private ComboBox<Categorie> categorieComboBox;
    @FXML
    private DatePicker dateDatePicker;
    @FXML
    private Button ajouterButton;
    @FXML
    private Button modifierButton;
    @FXML
    private Button supprimerButton;
    @FXML
    private TableView<Histoire> histoireTable;
    @FXML
    private TableColumn<Histoire, String> titreColumn;
    @FXML
    private TableColumn<Histoire, String> auteurColumn;
    @FXML
    private TableColumn<Histoire, Integer> nbPagesColumn;
    @FXML
    private TableColumn<Histoire, Double> prixColumn;
    @FXML
    private TableColumn<Histoire, Categorie> categorieColumn;
    @FXML
    private TableColumn<Histoire, Date> dateColumn;
    @FXML
    private Label messageLabel;
    private CategorieService categorieService = new CategorieService();
    private Utilisateur utilisateur = new Utilisateur();
    private HistoireService histoireService = new HistoireService();
    private List<Categorie> categorieList = new ArrayList<>();
    private List<Histoire> histoireList = new ArrayList<>();
    private FileService fileService = new FileService();
    @FXML
    private void initialize() {
        titreColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getTitre()));
        auteurColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getAuteur()));
        nbPagesColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getNbPages()));
        prixColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPrix()));
        categorieColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getCategories()));
        dateColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDate()));

        categorieList = FXCollections.observableArrayList(categorieService.findAll(utilisateur.getId()));
        //System.out.println(categorieService.findAll(utilisateur.getId()));
        categorieComboBox.setItems((ObservableList<Categorie>) categorieList);
        categorieComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Categorie item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNom()); // Remplacez "getNom()" par l'attribut que vous souhaitez afficher
                }
            }
        });
        categorieComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Categorie categorie) {
                if (categorie == null) {
                    return null;
                } else {
                    return categorie.getNom(); // Remplacez "getNom()" par l'attribut que vous souhaitez afficher
                }
            }

            @Override
            public Categorie fromString(String string) {
                if (string == null || string.trim().isEmpty()) {
                    return null;
                }

                // Parcourez votre liste de catégories et renvoyez l'objet Categorie correspondant au nom fourni
                List<Categorie> categories = categorieService.findAll(utilisateur.getId());// Récupérez votre liste de catégories à partir de votre source de données
                for (Categorie categorie : categories) {
                    if (categorie.getNom().equals(string)) { // Remplacez "getNom()" par l'attribut que vous utilisez pour afficher dans la ComboBox
                        return categorie;
                    }
                }

                return null; // Si aucune correspondance n'est trouvée
            }
        });


        histoireTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                fillFieldsWithSelectedHistoire(newSelection);
            } else {
                clearFields();
            }
        });

        // Chargement initial des histoires (à remplacer avec les données réelles)
        histoireList = FXCollections.observableArrayList(histoireService.findAll(utilisateur.getId()));
        histoireTable.setItems(FXCollections.observableArrayList(histoireList));
    }
    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    @FXML
    private void handleAjouter(ActionEvent event) throws ParseException {
        String titre = titreField.getText();
        String auteur = auteurField.getText();
        String contenu = contenuArea.getText();
        int nbPages = Integer.parseInt(nbPagesField.getText());
        double prix = Double.parseDouble(prixField.getText());
        Categorie categorie = categorieComboBox.getValue();
        Date date = java.sql.Date.valueOf(dateDatePicker.getValue());
        if (validateFields(titre, auteur, contenu, nbPages, prix, categorie, date)) {
            Histoire nouvelleHistoire = new Histoire(titre, auteur, contenu, nbPages, prix, categorie, date);
            histoireService.save(nouvelleHistoire, utilisateur.getId());
            histoireList.add(nouvelleHistoire);
            histoireTable.setItems(FXCollections.observableArrayList(histoireList));
            clearFields();
            showMessage("Histoire ajoutée avec succès.", MessageType.SUCCESS);
        } else {
            showMessage("Veuillez remplir tous les champs.", MessageType.ERROR);
        }
    }

    @FXML
    private void handleModifier(ActionEvent event) {
        Histoire histoireSelectionnee = histoireTable.getSelectionModel().getSelectedItem();
        if (histoireSelectionnee != null) {
            String titre = titreField.getText();
            String auteur = auteurField.getText();
            String contenu = contenuArea.getText();
            int nbPages = Integer.parseInt(nbPagesField.getText());
            double prix = Double.parseDouble(prixField.getText());
            Categorie categorie = categorieComboBox.getValue();
            Date date = java.sql.Date.valueOf(dateDatePicker.getValue());
            if (validateFields(titre, auteur, contenu, nbPages, prix, categorie, date)) {
                histoireSelectionnee.setTitre(titre);
                histoireSelectionnee.setAuteur(auteur);
                histoireSelectionnee.setContenu(contenu);
                histoireSelectionnee.setNbPages(nbPages);
                histoireSelectionnee.setPrix(prix);
                histoireSelectionnee.setCategories(categorie);
                histoireSelectionnee.setDate(date);
                histoireService.update(histoireSelectionnee, histoireSelectionnee.getId(), utilisateur.getId());
                histoireTable.refresh();
                showMessage("Histoire modifiée avec succès.", MessageType.SUCCESS);
                clearFields();
            } else {
                showMessage("Veuillez remplir tous les champs.", MessageType.ERROR);
            }
        } else {
            showMessage("Aucune histoire sélectionnée.", MessageType.ERROR);
        }
    }

    @FXML
    private void handleSupprimer(ActionEvent event) {
        Histoire histoireSelectionnee = histoireTable.getSelectionModel().getSelectedItem();
        if (histoireSelectionnee != null) {
            Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationDialog.setTitle("Confirmation");
            confirmationDialog.setHeaderText("Supprimer l'histoire ?");
            confirmationDialog.setContentText("Êtes-vous sûr de vouloir supprimer l'histoire sélectionnée ?");

            Optional<ButtonType> result = confirmationDialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                histoireService.remove(histoireSelectionnee.getId(), utilisateur.getId());
                histoireList.remove(histoireSelectionnee);
                histoireTable.setItems(FXCollections.observableArrayList(histoireList));
                clearFields();
                showMessage("Histoire supprimée avec succès.", MessageType.SUCCESS);
            }
        } else {
            showMessage("Aucune histoire sélectionnée.", MessageType.ERROR);
        }
    }

    private void fillFieldsWithSelectedHistoire(Histoire histoire) {
        titreField.setText(histoire.getTitre());
        auteurField.setText(histoire.getAuteur());
        contenuArea.setText(histoire.getContenu());
        nbPagesField.setText(String.valueOf(histoire.getNbPages()));
        prixField.setText(String.valueOf(histoire.getPrix()));
        categorieComboBox.setValue(histoire.getCategories());
        dateDatePicker.setValue(LocalDate.parse(convertDateString(histoire.getDate().toString())));
    }

    public static String convertDateString(String dateString) {
        // Tableaux de conversion pour les noms des mois et des jours de la semaine
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        String[] daysOfWeek = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

        // Extraire les parties de la date de la chaîne de texte d'origine
        String[] parts = dateString.split(" ");
        String dayOfWeek = parts[0];
        String month = parts[1];
        String day = parts[2];
        String year = parts[5];

        // Trouver l'index du mois dans le tableau des mois
        int monthIndex = 0;
        for (int i = 0; i < months.length; i++) {
            if (month.equals(months[i])) {
                monthIndex = i + 1; // Ajouter 1 pour correspondre aux mois numérotés de 1 à 12
                break;
            }
        }

        // Formater la date dans le format "yyyy-MM-dd"
        String formattedDate = year + "-" + padZero(monthIndex) + "-" + padZero(Integer.parseInt(day));

        return formattedDate;
    }

    // Fonction utilitaire pour ajouter un zéro devant les nombres inférieurs à 10
    private static String padZero(int number) {
        return (number < 10) ? "0" + number : String.valueOf(number);
    }

    @FXML
    private void clearFields() {
        titreField.clear();
        auteurField.clear();
        contenuArea.clear();
        nbPagesField.clear();
        prixField.clear();
        categorieComboBox.getSelectionModel().clearSelection();
        dateDatePicker.setValue(null);
    }
    private boolean validateFields(String titre, String auteur, String contenu, int nbPages, double prix, Categorie categorie, Date date) {
        return !titre.isEmpty() && !auteur.isEmpty() && !contenu.isEmpty() && nbPages > 0 && prix > 0 && categorie != null && date != null;
    }
    private void showMessage(String message, MessageType messageType) {
        Alert alert = new Alert(messageType == MessageType.SUCCESS ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setTitle("Message");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void categoriesH(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/stories/interface_categorie.fxml"));
            Parent root = loader.load();
            CategorieController categorieController = loader.getController();
            categorieController.setUtilisateur(utilisateur);
            Stage stage = new Stage();
            stage.setTitle("Vue Categories");
            stage.setScene(new Scene(root, 900, 700));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void Deconnexion(ActionEvent actionEvent) {
        // Fermer la fenêtre actuelle
        Stage stage = (Stage) titreField.getScene().getWindow();
        stage.close();
    }

    private enum MessageType {
        SUCCESS,
        ERROR
    }
    public void exporterEnJSON(ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Exporter un fichier JSON");
        dialog.setHeaderText(null);
        dialog.setContentText("Entrez le nom du fichier JSON :");
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(nomFichier -> {
            // Appel à la méthode du service pour lire le fichier JSON
            fileService.ecrireFichierJson(nomFichier, utilisateur.getId());
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
            fileService.ecrireFichierExcel(nomFichier, utilisateur.getId());
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
            fileService.ecrireFichierTexte(nomFichier, utilisateur.getId());
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
            fileService.lireFichierTexte(nomFichier, utilisateur.getId());
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
            fileService.lireFichierJson(nomFichier, utilisateur.getId());
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
            fileService.lireFichierExcel(nomFichier, utilisateur.getId());
        });
    }
    @FXML
    private void handleActualiser(ActionEvent event) {
        histoireList = FXCollections.observableArrayList(histoireService.findAll(utilisateur.getId()));
        histoireTable.setItems((ObservableList<Histoire>) histoireList);
        actualiserComboBox(event);
    }
    private void actualiserComboBox(ActionEvent event) {
        categorieList = FXCollections.observableArrayList(categorieService.findAll(utilisateur.getId()));
        //System.out.println(categorieService.findAll(utilisateur.getId()));
        categorieComboBox.setItems((ObservableList<Categorie>) categorieList);
        categorieComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Categorie item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNom()); // Remplacez "getNom()" par l'attribut que vous souhaitez afficher
                }
            }
        });
        categorieComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Categorie categorie) {
                if (categorie == null) {
                    return null;
                } else {
                    return categorie.getNom(); // Remplacez "getNom()" par l'attribut que vous souhaitez afficher
                }
            }

            @Override
            public Categorie fromString(String string) {
                if (string == null || string.trim().isEmpty()) {
                    return null;
                }

                // Parcourez votre liste de catégories et renvoyez l'objet Categorie correspondant au nom fourni
                List<Categorie> categories = categorieService.findAll(utilisateur.getId());// Récupérez votre liste de catégories à partir de votre source de données
                for (Categorie categorie : categories) {
                    if (categorie.getNom().equals(string)) { // Remplacez "getNom()" par l'attribut que vous utilisez pour afficher dans la ComboBox
                        return categorie;
                    }
                }

                return null; // Si aucune correspondance n'est trouvée
            }
        });

    }
}