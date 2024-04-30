package services;


import models.Admin;
import org.json.JSONArray;
import utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdminService implements UseService<Admin> {

    private  Connection connection = DataSource.getInstance().getConnection();

   /* @Override
    public void ajouter(Admin admin) {
        String req = "INSERT INTO `medecin`(`username`, `email`, `phone`, `password`, `token`, `role`, `fullname`) VALUES ('" + admin.getUsername() + "', '" + admin.getEmail() + "', '" + admin.getPhone() + "', '" + admin.getPassword() + "', '" + admin.getToken() + "', '" + admin.getRole() + "', '" + admin.getFullname() + "')";

        try {
            Statement st = connection.createStatement();
            st.executeUpdate(req);
            System.out.println("Admin ajouté avec succès !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }*/
    public void ajouter(Admin admin) {
        String roleJson = "[\"admin\"]";
        String sql = "INSERT INTO medecin (username, email, phone, password, token,photo, role, fullname) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, admin.getUsername());
            statement.setString(2, admin.getEmail());
            statement.setString(3, admin.getPhone());
            statement.setString(4, admin.getPassword());
            statement.setString(5, admin.getToken());
            statement.setString(6, admin.getPhoto());
            statement.setString(7, roleJson);

            statement.setString(8, admin.getFullname());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Admin ajouté avec succès !");
            } else {
                System.out.println("Échec de l'ajout du Admin.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du Admin : " + e.getMessage());
            throw new RuntimeException("Erreur lors de l'ajout du Admin", e);
        }
    }


    @Override
    public void supprimer(Admin admin) {
        String query = "DELETE FROM medecin WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, admin.getId()); // Assuming getId() returns the patient's ID
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Admin deleted successfully!");
            } else {
                System.out.println("No Admin with the specified id found.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting Admin: " + e.getMessage());
            throw new RuntimeException("Error deleting Admin", e);
        }
    }

    @Override
    public void modifier(Admin admin) {
        String req = "UPDATE `medecin` SET `username` = ?, `email` = ?, `phone` = ?, `password` = ?, `token` = ?, `photo` = ?, `role` = ?, `fullname` = ? WHERE `medecin`.`id` = ?";
        try (PreparedStatement st = connection.prepareStatement(req)) {
            // Set parameters for the prepared statement
            st.setString(1, admin.getUsername());
            st.setString(2, admin.getEmail());
            st.setString(3, admin.getPhone());
            st.setString(4, admin.getPassword());
            st.setString(5, admin.getToken());
            st.setString(6, admin.getPhoto());
            // Convert the role array to JSON format
            st.setString(7, new JSONArray(Arrays.asList(admin.getRole())).toString());
            st.setString(8, admin.getFullname());
            st.setInt(9, admin.getId());

            // Execute the update
            int rowsUpdated = st.executeUpdate();

            // Check if the update was successful
            if (rowsUpdated > 0) {
                System.out.println("Admin modifié avec succès !");
            } else {
                System.out.println("Aucune modification effectuée pour le admin.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour du admin : " + e.getMessage());
        }
    }
    @Override
    public List<Admin> afficher() {
        List<Admin> admins = new ArrayList<>();
        String query = "SELECT * FROM medecin WHERE JSON_CONTAINS(role, '[\"admin\"]')";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Admin p = new Admin();
                p.setId(rs.getInt(1));
                p.setUsername(rs.getString("username"));
                p.setEmail(rs.getString("email"));
                p.setPhone(rs.getString("phone"));
                p.setToken(rs.getString("token"));
                p.setPhoto(rs.getString("photo"));
                p.setFullname(rs.getString("fullname"));
                admins.add(p);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return admins;
    }


    @Override
    public void block(Admin admin) {
        // Set the token to '0'
        admin.setToken("0");

        // Using a prepared statement to prevent SQL injection
        String query = "UPDATE medecin SET token = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            // Set parameters for the prepared statement
            statement.setString(1, admin.getToken());
            statement.setInt(2, admin.getId());

            // Execute the update
            int rowsUpdated = statement.executeUpdate();

            // Check if the update was successful
            if (rowsUpdated > 0) {
                System.out.println("Admin Block Sucssessfully !");
            } else {
                System.out.println("Aucune modification effectuée pour le admin.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour du admin : " + e.getMessage());
        }
    }

    @Override
    public void unblock(Admin admin){
        // Set the token to '0'
        admin.setToken("1");

        // Using a prepared statement to prevent SQL injection
        String query = "UPDATE medecin SET token = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            // Set parameters for the prepared statement
            statement.setString(1, admin.getToken());
            statement.setInt(2, admin.getId());

            // Execute the update
            int rowsUpdated = statement.executeUpdate();

            // Check if the update was successful
            if (rowsUpdated > 0) {
                System.out.println("Admin Unblock Sucssessfully !");
            } else {
                System.out.println("Aucune modification effectuée pour le admin.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour du admin : " + e.getMessage());
        }
    }

    public void switchToUser(Admin admin) {
        // Create a JSON array containing the new role "patient"
        JSONArray roleArray = new JSONArray();
        roleArray.put("user");

        // Convert the JSON array to its string representation
        String roleJson = roleArray.toString();

        // Using a prepared statement to prevent SQL injection
        String query = "UPDATE medecin SET role = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            // Set parameters for the prepared statement
            statement.setString(1, roleJson);
            statement.setInt(2, admin.getId());

            // Execute the update
            int rowsUpdated = statement.executeUpdate();

            // Check if the update was successful
            if (rowsUpdated > 0) {
                System.out.println("Admin switched to Patient!");
            } else {
                System.out.println("No modification made for the admin.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating the admin: " + e.getMessage());
        }
    }




    public void switchToAdmin(Admin admin){


        JSONArray roleArray = new JSONArray();
        roleArray.put("admin");

        // Convert the JSON array to its string representation
        String roleJson = roleArray.toString();

        // Using a prepared statement to prevent SQL injection
        String query = "UPDATE medecin SET role = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            // Set parameters for the prepared statement
            statement.setString(1, roleJson);
            statement.setInt(2, admin.getId());

            // Execute the update
            int rowsUpdated = statement.executeUpdate();

            // Check if the update was successful
            if (rowsUpdated > 0) {
                System.out.println("Patient switched to Admin!");
            } else {
                System.out.println("No modification made for the admin.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating the admin: " + e.getMessage());
        }
    }


}