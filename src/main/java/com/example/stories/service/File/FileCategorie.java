package com.example.stories.service.File;

import com.example.stories.entities.Categorie;
import com.example.stories.entities.Histoire;
import com.example.stories.service.CategorieService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class FileCategorie implements FileInterface{
    private CategorieService cs = new CategorieService();
    @Override
    public void lireFichierTexte(String nomFichier, int idUser) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(nomFichier));
            String line = reader.readLine();
            while (line != null) {
                String[] parts = line.split(",");
                try {
                    if (cs.findByName(parts[0], idUser)==null && !categorieExist(new Categorie(parts[0], parts[1]), idUser)) {
                        addCategorie(parts[0], parts[1], idUser);
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

    @Override
    public void ecrireFichierTexte(String nomFichier, int idUser) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(nomFichier));
            for (Categorie categorie : cs.findAll(idUser)) {
                String line = categorie.getNom() + "," + categorie.getDescription() ;
                writer.write(line);
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void lireFichierExcel(String nomFichier, int idUser) {
        try(FileInputStream file = new FileInputStream(new java.io.File(nomFichier))) {
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            List<Categorie> categories = new ArrayList<>();
            rowIterator.hasNext();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();
                String nom = "";
                String description = "";
                cellIterator.hasNext();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    int columnIndex = cell.getColumnIndex();
                    switch (columnIndex) {
                        case 0:
                            nom = cell.getStringCellValue();
                            break;
                        case 1:
                            description = cell.getStringCellValue();
                            break;
                    }
                }
                if (cs.findByName(nom, idUser) == null && !categorieExist(new Categorie(nom, description), idUser))
                    addCategorie(nom, description, idUser);
                continue;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void ecrireFichierExcel(String nomFichier, int idUser) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Histoires");

        // Créer un TreeMap pour trier les histoires par ordre alphabétique du titre
        Map<String, Categorie> sortedCategorie = new TreeMap<>();
        for (Categorie categorie : cs.findAll(idUser)) {
            sortedCategorie.put(categorie.getNom(), categorie);
        }

        Set<String> keySet = sortedCategorie.keySet();
        int rownum = 0;
        for (String key : keySet) {
            XSSFRow row = sheet.createRow(rownum++);
            Categorie categorie = sortedCategorie.get(key);

            Cell nomCell = row.createCell(0);
            nomCell.setCellValue(categorie.getNom());

            Cell descriptionCell = row.createCell(1);
            descriptionCell.setCellValue(categorie.getDescription());
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

    @Override
    public void lireFichierJson(String nomFichier, int idUser) {
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(nomFichier));
            JSONArray jsonArray = (JSONArray) obj;
            Iterator<?> iterator = jsonArray.iterator();
            while (iterator.hasNext()) {
                JSONObject jsonObject = (JSONObject) iterator.next();
                String nom = (String) jsonObject.get("nom");
                String description = (String) jsonObject.get("description");
                System.out.println(nom + description);
                try {
                    if (cs.findByName(nom, idUser) == null && !categorieExist(new Categorie(nom, description), idUser))
                        addCategorie(nom, description, idUser);
                    continue;
                } catch (Exception e){
                    System.out.println("Erreur de Saisie ");
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void ecrireFichierJson(String nomFichier, int idUser) {
        try {
            // Création d'un objet Gson pour la sérialisation en JSON
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            // Ouverture du fichier en écriture
            FileWriter writer = new FileWriter(nomFichier);
            // Sérialisation de la liste d'histoires en JSON et écriture dans le fichier
            gson.toJson(cs.findAll(idUser), writer);
            // Fermeture du fichier
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addCategorie(String nom, String description, int idUser) {
        try {
            Categorie categorie = new Categorie(nom, description);
            if(!categorieExist(categorie, idUser)) {
                cs.save(categorie, idUser);
            } else {
                cs.update(categorie, getId(categorie,idUser), idUser);
            }
        } catch (Exception e){
            System.out.println(e);
        }
    }
    public boolean categorieExist(Categorie categorie, int idUser) {
        for (Categorie categorie1: cs.findAll(idUser)){
            if (categorie1.equals(categorie)) return true;
        }
        return false;
    }
    public int getId(Categorie categorie, int idUser){
        for (Categorie categorie1: cs.findAll(idUser)){
            if (categorie1.equals(categorie)) return categorie1.getId();
        }
        return -1;
    }
}
