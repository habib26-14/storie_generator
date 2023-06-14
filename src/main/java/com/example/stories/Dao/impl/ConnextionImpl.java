package com.example.stories.Dao.impl;

import com.example.stories.Dao.UtilisateurDao;
import com.example.stories.entities.Utilisateur;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class ConnextionImpl implements UtilisateurDao {
    private Connection conn= DB.getConnection();
    @Override
    public Utilisateur seConnecter(String email, String motDePasse) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(
                    "SELECT * FROM utilisateur WHERE email = ? AND motDePasse = ?");

            ps.setString(1, email);
            ps.setString(2, hashPassword(motDePasse));
            rs = ps.executeQuery();
            System.out.println(rs.next());
            return instantiateUtilisateur(rs);
        } catch (SQLException e) {
            System.err.println("problème de requête pour sélectionner l'utilisteur" + e);
            return null;
        } finally {
            DB.closeStatement(ps);
            DB.closeResultSet(rs);
        }
    }
    private Utilisateur instantiateUtilisateur(ResultSet rs) throws SQLException {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(rs.getInt("Id"));
        utilisateur.setEmail(rs.getString("email"));
        utilisateur.setNom(rs.getString("nom"));
        utilisateur.setPrenom(rs.getString("prenom"));
        utilisateur.setGenre(rs.getString("genre"));
        utilisateur.setEstConnecte(true);
        return utilisateur;
    }
    @Override
    public void sInscrire(Utilisateur utilisateur) {
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(
                    "INSERT INTO utilisateur(email, nom, prenom, pseudo, genre, motDePasse, estConnecte) VALUES (?, ?, ?, ?, ?, ?,?)",
                    Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, utilisateur.getEmail());
            ps.setString(2, utilisateur.getNom());
            ps.setString(3, utilisateur.getPrenom());
            ps.setString(4, utilisateur.getPseudo());
            ps.setString(5, utilisateur.getGenre());
            ps.setString(6, hashPassword(utilisateur.getMotDePasse()));
            ps.setBoolean(7, utilisateur.getEstConnecte());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    utilisateur.setId(id);
                }
                DB.closeResultSet(rs);
            } else {
                System.out.println("Aucune ligne renvoyé");;
            }
        } catch (SQLException e) {
            System.err.println("problème d'insertion d'un utilisateur " + e);
        } finally {
            DB.closeStatement(ps);
        }
    }
    public static String hashPassword(String password) {
        try {
            // Créer une instance de l'algorithme MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // Convertir le mot de passe en tableau de bytes
            byte[] passwordBytes = password.getBytes();

            // Calculer le hachage du mot de passe
            byte[] hashedBytes = md.digest(passwordBytes);

            // Convertir le tableau de bytes en une représentation hexadécimale
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }

            // Retourner le mot de passe haché
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
