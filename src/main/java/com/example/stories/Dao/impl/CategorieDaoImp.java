package com.example.stories.Dao.impl;

import com.example.stories.Dao.CategorieDao;
import com.example.stories.entities.Categorie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategorieDaoImp implements CategorieDao {
    private Connection conn= DB.getConnection();
    @Override
    public void save(Categorie categorie, int idUser) {
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement("INSERT INTO Categorie (Nom, Description, utilisateurId) VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, categorie.getNom());
            ps.setString(2, categorie.getDescription());
            ps.setInt(3, idUser);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet rs = ps.getGeneratedKeys();

                if (rs.next()) {
                    int id = rs.getInt(1);

                    categorie.setId(id);
                }

                DB.closeResultSet(rs);
            } else {
                System.out.println("Aucune ligne renvoyée");
            }
        } catch (SQLException e) {
            System.err.println("problème d'insertion d'une Categorie " + e);;
        } finally {
            DB.closeStatement(ps);
        }
    }

    @Override
    public void update(Categorie categorie, int id, int idUser) {
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement("UPDATE Categorie SET Nom = ?, Description = ? WHERE Id = ? AND utilisateurId = ?");

            ps.setString(1, categorie.getNom());
            ps.setString(2, categorie.getDescription());
            ps.setInt(3, id);
            ps.setInt(4, idUser);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("problème de mise à jour d'une categorie " + e);;
        } finally {
            DB.closeStatement(ps);
        }
    }

    @Override
    public void delete(String nom, int idUser) {
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement("DELETE FROM Categorie WHERE Nom = ? AND utilisateurId = ?");

            ps.setString(1, nom);
            ps.setInt(2, idUser);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("problème de suppression d'un categorie " + e);
        } finally {
            DB.closeStatement(ps);
        }
    }

    @Override
    public Categorie findById(int id, int idUser)  {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement("SELECT * FROM Categorie WHERE id = ? AND utilisateurId = ?");

            ps.setInt(1, id);
            ps.setInt(2, idUser);

            rs = ps.executeQuery();

            if (rs.next()) {
                Categorie department = new Categorie();
                department.setId(rs.getInt("Id"));
                department.setNom(rs.getString("Nom"));
                department.setDescription(rs.getString("Description"));
                return department;
            }

            return null;
        } catch (SQLException e) {
            System.err.println("problème de requête pour trouver une Categorie" + e);;
            return null;
        } finally {
            DB.closeResultSet(rs);
            DB.closeStatement(ps);
        }
    }

    @Override
    public Categorie findByName(String nom, int idUser) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement("SELECT * FROM Categorie WHERE Nom = ? AND utilisateurId = ?");

            ps.setString(1, nom);
            ps.setInt(2, idUser);

            rs = ps.executeQuery();

            if (rs.next()) {
                Categorie department = new Categorie();
                department.setId(rs.getInt("Id"));
                department.setNom(rs.getString("Nom"));
                department.setDescription(rs.getString("Description"));
                return department;
            }

            return null;
        } catch (SQLException e) {
            System.err.println("problème de requête pour trouver une Categorie " + e);
            return null;
        } finally {
            DB.closeResultSet(rs);
            DB.closeStatement(ps);
        }
    }
    @Override
    public List<Categorie> findAll(int idUser) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement("SELECT * FROM Categorie WHERE Categorie.utilisateurId = ?");
            ps.setInt(1, idUser);
            rs = ps.executeQuery();

            List<Categorie> listCategorie = new ArrayList<>();

            while (rs.next()) {
                Categorie categorie = new Categorie();

                categorie.setId(rs.getInt("Id"));
                categorie.setNom(rs.getString("Nom"));
                categorie.setDescription(rs.getString("Description"));

                listCategorie.add(categorie);
            }

            return listCategorie;
        } catch (SQLException e) {
            System.err.println("problème de requête pour sélectionner une categorie " + e);
            return null;
        } finally {
            DB.closeResultSet(rs);
            DB.closeStatement(ps);
        }

    }
}
