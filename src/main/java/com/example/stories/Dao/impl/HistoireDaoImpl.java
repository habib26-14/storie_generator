package com.example.stories.Dao.impl;

import com.example.stories.Dao.HistoireDao;
import com.example.stories.entities.Categorie;
import com.example.stories.entities.Histoire;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoireDaoImpl implements HistoireDao {
    private Connection conn= DB.getConnection();
    @Override
    public void save(Histoire histoire, int idUser) {
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement(
                    "INSERT INTO Histoire (Titre, Auteur, Contenu, nbPages, Prix, CategorieId, Dates, utilisateurId) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, histoire.getTitre());
            ps.setString(2, histoire.getAuteur());
            ps.setString(3, histoire.getContenu());
            ps.setInt(4, histoire.getNbPages());
            ps.setDouble(5, histoire.getPrix());
            ps.setInt(6, histoire.getCategories().getId());
            ps.setDate(7, new Date(histoire.getDate().getTime()));
            ps.setInt(8, idUser);


            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet rs = ps.getGeneratedKeys();

                if (rs.next()) {
                    int id = rs.getInt(1);

                    histoire.setId(id);
                }

                DB.closeResultSet(rs);
            } else {
                System.out.println("Aucune ligne renvoyé");;
            }
        } catch (SQLException e) {
            System.err.println("problème d'insertion d'une Histoire " + e);;
        } finally {
            DB.closeStatement(ps);
        }
    }

    @Override
    public void update(Histoire histoire, int id, int idUser) {
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement(
                    "UPDATE Histoire SET TITRE = ?, AUTEUR = ?, CONTENU = ?, NBPAGES = ?, PRIX = ?, CategorieID = ?, DATES = ? WHERE Id = ? AND utilisateurId = ?");

            ps.setString(1, histoire.getTitre());
            ps.setString(2, histoire.getAuteur());
            ps.setString(3, histoire.getContenu());
            ps.setInt(4, histoire.getNbPages());
            ps.setDouble(5, histoire.getPrix());
            ps.setInt(6, histoire.getCategories().getId());
            ps.setDate(7, new Date(histoire.getDate().getTime()));
            ps.setInt(8, id);
            ps.setInt(9, idUser);

            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("problème de mise à jour d'une histoire " + e);;
        } finally {
            DB.closeStatement(ps);
        }
    }

    @Override
    public void delete(int id, int idUser) {
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("DELETE FROM Histoire WHERE Histoire.id = ? AND Histoire.utilisateurId = ?");

            ps.setInt(1, id);
            ps.setInt(2, idUser);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("problème de suppression d'une Histoire");;
        } finally {
            DB.closeStatement(ps);
        }
    }

    @Override
    public Histoire findById(int id, int idUser) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(
                    "SELECT h.*, c.nom AS CateName, c.Description as CatDes FROM Histoire h INNER JOIN Categorie c ON h.CategorieId = c.Id WHERE h.id = ? AND h.utilisateurId = ?");

            ps.setInt(1, id);
            ps.setInt(2, idUser);

            rs = ps.executeQuery();

            if (rs.next()) {
                Categorie categories = instantiateCategorie(rs);
                Histoire histoire = instantiateHistoire(rs, categories);

                return histoire;
            }

            return null;
        } catch (SQLException e) {
            System.err.println("problème de requête pour trouver l'histoire" + e);
            return null;
        } finally {
            DB.closeStatement(ps);
            DB.closeResultSet(rs);
        }
    }

    @Override
    public List<Histoire> findAll(int idUser) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(
                    "SELECT h.*, c.Nom as CateName, c.Description as CatDes FROM Histoire h INNER JOIN Categorie c ON c.Id = h.CategorieId WHERE h.utilisateurId = ?");
            ps.setInt(1, idUser);
            rs = ps.executeQuery();
            List<Histoire> list = new ArrayList<>();
            Map<Integer, Categorie> map = new HashMap<>();

            while (rs.next()) {
                Categorie cate = map.get(rs.getInt("Id"));

                if (cate == null) {
                    cate = instantiateCategorie(rs);
                    map.put(rs.getInt("Id"), cate);
                }

                Histoire histoire = instantiateHistoire(rs, cate);

                list.add(histoire);
            }

            return list;
        } catch (SQLException e) {
            System.err.println("problème de requête pour sélectionner les histoires" + e);
            return null;
        } finally {
            DB.closeStatement(ps);
            DB.closeResultSet(rs);
        }
    }

    public List<Histoire> AllStories() {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(
                    "SELECT h.*, c.Nom as CateName, c.Description as CatDes FROM Histoire h INNER JOIN Categorie c ON c.Id = h.CategorieId");
            rs = ps.executeQuery();
            List<Histoire> list = new ArrayList<>();
            Map<Integer, Categorie> map = new HashMap<>();

            while (rs.next()) {
                Categorie cate = map.get(rs.getInt("Id"));

                if (cate == null) {
                    cate = instantiateCategorie(rs);
                    map.put(rs.getInt("Id"), cate);
                }

                Histoire histoire = instantiateHistoire(rs, cate);

                list.add(histoire);
            }

            return list;
        } catch (SQLException e) {
            System.err.println("problème de requête pour sélectionner les histoires" + e);
            return null;
        } finally {
            DB.closeStatement(ps);
            DB.closeResultSet(rs);
        }
    }

    @Override
    public List<Histoire> findByCategorie(Categorie categorie, int idUser) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(
                    "SELECT h.*, c.Nom as CateName, c.Description as CatDes FROM Histoire h INNER JOIN Categorie c ON h.CategorieId = c.Id WHERE c.Nom = ? AND h.utilisateurId = ? ORDER BY h.Titre");

            ps.setString(1, categorie.getNom());
            ps.setInt(2, idUser);

            rs = ps.executeQuery();
            List<Histoire> list = new ArrayList<>();
            Map<Integer, Categorie> map = new HashMap<>();

            while (rs.next()) {
                Categorie cate = map.get(rs.getInt("CategoriesId"));

                if (cate == null) {
                    cate = instantiateCategorie(rs);

                    map.put(rs.getInt("CategoriesId"), cate);
                }

                Histoire histoire = instantiateHistoire(rs, cate);

                list.add(histoire);
            }

            return list;
        } catch (SQLException e) {
            System.err.println("problème de requête pour sélectionner les histoires d'une categorie donnée" + e);
            return null;
        } finally {
            DB.closeStatement(ps);
            DB.closeResultSet(rs);
        }
    }

    private Histoire instantiateHistoire(ResultSet rs, Categorie categorie) throws SQLException {
        Histoire histoire = new Histoire();

        histoire.setId(rs.getInt("Id"));
        histoire.setTitre(rs.getString("Titre"));
        histoire.setAuteur(rs.getString("Auteur"));
        histoire.setContenu(rs.getString("Contenu"));
        histoire.setNbPages(rs.getInt("NbPages"));
        histoire.setPrix(rs.getDouble("Prix"));
        histoire.setDate(new java.util.Date(rs.getTimestamp("Dates").getTime()));
        histoire.setCategories(categorie);

        return histoire;
    }
    private Categorie instantiateCategorie(ResultSet rs) throws SQLException {
        Categorie categorie = new Categorie();

        categorie.setId(rs.getInt("CategorieId"));
        categorie.setNom(rs.getString("CateName"));
        categorie.setDescription(rs.getString("CatDes"));

        return categorie;
    }
}
