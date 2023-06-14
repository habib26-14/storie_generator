package com.example.stories.service.File;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.example.stories.entities.Categorie;
import com.example.stories.entities.Histoire;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.example.stories.service.CategorieService;
import com.example.stories.service.HistoireService;

import javax.speech.AudioException;
import javax.speech.Central;
import javax.speech.EngineException;
import javax.speech.synthesis.JSMLException;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class FileService implements FileInterface{
    private CategorieService cs = new CategorieService();
    private HistoireService hs = new HistoireService();
    private List<Histoire> histoires ;
    public FileService() {
        histoires = new ArrayList<>();
    }
    public void ajouterHistoire(Histoire histoire) {
        histoires.add(histoire);
    }
    public List<Histoire> getHistoires() {
        return histoires;
    }
    private Thread thread;;

    public void setHistoires(List<Histoire> histoires) {
        this.histoires = histoires;
    }
    public static void speak(String text) {
        System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
        try {
            Central.registerEngineCentral("com.sun.speech.freetts"+".jsapi.FreeTTSEngineCentral");
            Synthesizer synthesizer = Central.createSynthesizer(new SynthesizerModeDesc(Locale.US));
            synthesizer.allocate();
            synthesizer.resume();
            synthesizer.speak(text, null);
            synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
            synthesizer.deallocate();
        } catch (EngineException | InterruptedException | JSMLException | AudioException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void lireFichierTexte(String nomFichier, int idUser) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(nomFichier));
            String line = reader.readLine();
            while (line != null) {
                String[] parts = line.split(",");
                try {
                    if (cs.findByName(parts[5], idUser)!=null && !storieExist(new Histoire(parts[0], parts[1], parts[2], Integer.parseInt(parts[3]), Double.parseDouble(parts[4]), cs.findByName(parts[5], idUser),new SimpleDateFormat("dd/MM/yyyy").parse(parts[6])), idUser)) {
                        addStorie(parts[0], parts[1], parts[2], Integer.parseInt(parts[3]), Double.parseDouble(parts[4]), cs.findByName(parts[5], idUser),new SimpleDateFormat("dd/MM/yyyy").parse(parts[6]), idUser);
                    }
                } catch (Exception e){
                    System.out.println("Erreur de Saisie ");
                }
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Méthode pour écrire les informations d'une List<Histoire> dans un fichier texte
    @Override
    public void ecrireFichierTexte(String nomFichier, int idUser) {
        try {
            histoires = hs.findAll(idUser);
            BufferedWriter writer = new BufferedWriter(new FileWriter(nomFichier));
            for (Histoire histoire : histoires) {
                String line = histoire.getTitre() + "," + histoire.getAuteur() + "," + histoire.getContenu() + "," + histoire.getNbPages() + "," + histoire.getPrix() + "," + histoire.getCategories().getNom() + "," + histoire.getDate();
                writer.write(line);
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Méthode pour lire les informations d'un fichier Excel et les stocker dans une List<Histoire>
    @Override
    public void lireFichierExcel(String nomFichier, int idUser) {
        try(FileInputStream file = new FileInputStream(new java.io.File(nomFichier))) {
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            List<Histoire> histoires = new ArrayList<>();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();
                String titre = "";
                String auteur = "";
                String contenu = "";
                int nbPages = 0;
                double prix=0;
                String categorie = "";
                String date = "";
                cellIterator.hasNext();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    int columnIndex = cell.getColumnIndex();
                    switch (columnIndex) {
                        case 0:
                            titre = cell.getStringCellValue();
                            break;
                        case 1:
                            auteur = cell.getStringCellValue();
                            break;
                        case 2:
                            contenu = cell.getStringCellValue();
                            break;
                        case 3:
                            if(cell.getCellType() == CellType.NUMERIC) {
                                nbPages = (int) cell.getNumericCellValue();
                            } else if(cell.getCellType() == CellType.STRING) {
                                nbPages = Integer.parseInt(cell.getStringCellValue());
                            }
                            break;
                        case 4:
                            if(cell.getCellType() == CellType.NUMERIC) {
                                prix = cell.getNumericCellValue();
                            } else if(cell.getCellType() == CellType.STRING) {
                                prix = Double.parseDouble(cell.getStringCellValue());
                            }
                            break;
                        case 5:
                            categorie = cell.getStringCellValue();
                            break;
                        case 6:
                            if(cell.getCellType() == CellType.NUMERIC) {
                                date = new SimpleDateFormat("dd/MM/yyyy").format(cell.getDateCellValue());
                            } else if(cell.getCellType() == CellType.STRING) {
                                date = cell.getStringCellValue();
                            }
                            break;
                    }
                }
                if (cs.findByName(categorie, idUser) != null && !storieExist(new Histoire(titre, auteur, contenu, nbPages, prix, cs.findByName(categorie, idUser),new SimpleDateFormat("dd/MM/yyyy").parse(date)), idUser))
                    addStorie(titre, auteur, contenu, nbPages, prix, cs.findByName(categorie, idUser),new SimpleDateFormat("dd/MM/yyyy").parse(date), idUser);
                continue;
            }
            this.histoires = histoires;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            throw new RuntimeException(e);
        }
    }
    // Méthode pour écrire les informations d'une List<Histoire> dans un fichier Excel
    @Override
    public void ecrireFichierExcel(String nomFichier, int idUser) {
        histoires = hs.findAll(idUser);
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Histoires");

        // Créer un TreeMap pour trier les histoires par ordre alphabétique du titre
        Map<String, Histoire> sortedHistoires = new TreeMap<>();
        for (Histoire histoire : histoires) {
            sortedHistoires.put(histoire.getTitre(), histoire);
        }

        Set<String> keySet = sortedHistoires.keySet();
        int rownum = 0;
        for (String key : keySet) {
            XSSFRow row = sheet.createRow(rownum++);
            Histoire histoire = sortedHistoires.get(key);

            Cell titreCell = row.createCell(0);
            titreCell.setCellValue(histoire.getTitre());

            Cell auteurCell = row.createCell(1);
            auteurCell.setCellValue(histoire.getAuteur());

            Cell contenuCell = row.createCell(2);
            contenuCell.setCellValue(histoire.getContenu());

            Cell nbPagesCell = row.createCell(3);
            nbPagesCell.setCellValue(histoire.getNbPages());

            Cell prixCell = row.createCell(4);
            prixCell.setCellValue(histoire.getPrix());

            Cell categorieCell = row.createCell(5);
            categorieCell.setCellValue(histoire.getCategories().getNom());

            Cell dateCell = row.createCell(6);
            dateCell.setCellValue(histoire.getDate().toString());
        }

        try {
            FileOutputStream out = new FileOutputStream(new java.io.File(nomFichier));
            workbook.write(out);
            out.close();
            System.out.println("Fichier Excel écrit avec succès !");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Méthode pour lire les informations d'un fichier Json et les stocker dans une List<Histoire>
    @Override
    public void lireFichierJson(String nomFichier, int idUser) {
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(nomFichier));
            JSONArray jsonArray = (JSONArray) obj;
            Iterator<?> iterator = jsonArray.iterator();
            while (iterator.hasNext()) {
                JSONObject jsonObject = (JSONObject) iterator.next();
                String titre = (String) jsonObject.get("titre");
                String auteur = (String) jsonObject.get("auteur");
                String contenu = (String) jsonObject.get("contenu");
                String nbPages = (String) jsonObject.get("nbPages");
                String prix= (String) jsonObject.get("prix");
                String categorie = (String) jsonObject.get("categorie");
                String date = (String) jsonObject.get("date");
                try {
                    if (cs.findByName(categorie, idUser) != null && !storieExist(new Histoire(titre, auteur, contenu, Integer.parseInt(nbPages), Double.parseDouble(prix), cs.findByName(categorie, idUser),new SimpleDateFormat("dd/MM/yyyy").parse(date)), idUser))
                        addStorie(titre, auteur, contenu, Integer.parseInt(nbPages), Double.parseDouble(prix), cs.findByName(categorie, idUser),new SimpleDateFormat("dd/MM/yyyy").parse(date), idUser);
                    continue;
                } catch (Exception e){
                    System.out.println("Erreur de Saisie ");
                }
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
    }
    // Méthode pour écrire les informations d'une List<Histoire> dans un fichier Json
    @Override
    public void ecrireFichierJson(String nomFichier, int idUser) {
        try {
            histoires = hs.findAll(idUser);
            // Création d'un objet Gson pour la sérialisation en JSON
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            // Ouverture du fichier en écriture
            FileWriter writer = new FileWriter(nomFichier);

            // Sérialisation de la liste d'histoires en JSON et écriture dans le fichier
            gson.toJson(histoires, writer);

            // Fermeture du fichier
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void addStorie(String titre, String auteur, String contenu,int nbPages,double prix, Categorie categorie,Date date, int idUser) {
        try {
            Histoire histoire = new Histoire(titre, auteur, contenu, nbPages, prix,categorie,date);
            if(!storieExist(histoire, idUser)) {
                hs.save(histoire, idUser);
                histoires.add(histoire);
            } else {
                hs.update(histoire, getId(histoire,idUser), idUser);
            }
        } catch (Exception e){
            System.out.println(e);
        }
    }
    public boolean storieExist(Histoire histoire, int idUser) {
        for (Histoire histoire1: hs.findAll(idUser)){
            if (histoire1.equals(histoire)) return true;
        }
        return false;
    }
    public int getId(Histoire histoire, int idUser){
        for (Histoire histoire1: hs.findAll(idUser)){
            if (histoire1.equals(histoire)) return histoire1.getId();
        }
        return -1;
    }
}
