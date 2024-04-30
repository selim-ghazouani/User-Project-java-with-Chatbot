package services;

import models.Doctor;
import org.json.JSONArray;
import utils.DataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.sql.PreparedStatement;

public class DoctorService implements UseService<Doctor> {

    private Connection connection = DataSource.getInstance().getConnection();

    @Override
    public void ajouter(Doctor doctor) {
        String roleJson = "[\"medecin\"]";
        String sql = "INSERT INTO `medecin` (`username`, `email`, `phone`, `password`, `token`,`photo`, `role`, `specialite`, `adress`, `fullname`) VALUES (?, ?, ?, ?,?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, doctor.getUsername());
            statement.setString(2, doctor.getEmail());
            statement.setString(3, doctor.getPhone());
            statement.setString(4, doctor.getPassword());
            statement.setString(5, doctor.getToken());
            statement.setString(6, doctor.getPhoto());
            statement.setString(7, roleJson);
            statement.setString(8, doctor.getSpecialite());
            statement.setString(9, doctor.getAddress());
            statement.setString(10, doctor.getFullname());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Doctor ajouté avec succès !");
            } else {
                System.out.println("Échec de l'ajout du doctor.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du doctor : " + e.getMessage());
            throw new RuntimeException("Erreur lors de l'ajout du doctor", e);
        }
    }

    @Override
    public void supprimer(Doctor doctor) {
        String query = "DELETE FROM medecin WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, doctor.getId()); // Assuming getId() returns the patient's ID
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Doctor deleted successfully!");
            } else {
                System.out.println("No Doctor with the specified id found.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting Doctor: " + e.getMessage());
            throw new RuntimeException("Error deleting Doctor", e);
        }
    }

    @Override
    public void modifier(Doctor doctor) {
        String req = "UPDATE `medecin` SET `username` = ?, `email` = ?, `phone` = ?, `password` = ?, `token` = ?, `photo` = ?, `role` = ?, `fullname` = ?, `specialite` = ?, `adress` = ? WHERE `medecin`.`id` = ?";
        try (PreparedStatement st = connection.prepareStatement(req)) {
            // Set parameters for the prepared statement
            st.setString(1, doctor.getUsername());
            st.setString(2, doctor.getEmail());
            st.setString(3, doctor.getPhone());
            st.setString(4, doctor.getPassword());
            st.setString(5, doctor.getToken());
            st.setString(6, doctor.getPhoto());
            // Convert the role array to JSON format
            st.setString(7, new JSONArray(Arrays.asList(doctor.getRole())).toString());
            st.setString(8, doctor.getFullname());
            st.setString(9, doctor.getSpecialite());
            st.setString(10, doctor.getAddress());
            st.setInt(11, doctor.getId());

            // Execute the update
            int rowsUpdated = st.executeUpdate();

            // Check if the update was successful
            if (rowsUpdated > 0) {
                System.out.println("Doctor modifié avec succès !");
            } else {
                System.out.println("Aucune modification effectuée pour le doctor.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour du doctor : " + e.getMessage());
        }
    }


   /* @Override
    public List<Doctor> afficher() throws SQLException {
        List<Doctor> doctors = new ArrayList<>();

        String req = "SELECT * FROM medecin WHERE role = 'doctor'";
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                Doctor doctor = new Doctor(); // Create a new Doctor object

                // Set the attributes inherited from User
                doctor.setId(rs.getInt("id"));
                doctor.setUsername(rs.getString("username"));
                doctor.setEmail(rs.getString("email"));
                doctor.setPhone(rs.getString("phone"));
                doctor.setPhoto(rs.getString("photo"));
                doctor.setFullname(rs.getString("fullname"));

                // Set the attributes specific to Doctor
                doctor.setSpecialite(rs.getString("specialite"));
                doctor.setAddress(rs.getString("adress"));

                doctors.add(doctor); // Add the doctor to the list
            }
        } catch (SQLException e) {
            // Handle SQL exceptions
            System.out.println("Error fetching doctor data: " + e.getMessage());
            throw e; // Re-throw the exception to propagate it
        }

        return doctors;
    }*/


    public List<Doctor> afficher() {
        List<Doctor> doctors = new ArrayList<>();
        try {
            String req = "SELECT * FROM medecin WHERE JSON_CONTAINS(role, '[\"medecin\"]')";
            PreparedStatement statement = connection.prepareStatement(req);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Doctor p = new Doctor();
                p.setId(rs.getInt("id"));
                p.setUsername(rs.getString("username"));
                p.setEmail(rs.getString("email"));
                p.setPhone(rs.getString("phone"));
                p.setPhoto(rs.getString("photo"));
                p.setSpecialite(rs.getString("specialite"));
                p.setAddress(rs.getString("adress"));
                p.setFullname(rs.getString("fullname"));
                doctors.add(p);
            }
        } catch(SQLException ex){
            System.out.println(ex.getMessage());
        }
        return doctors;
    }



    @Override
    public void block(Doctor doctor) {

    }

    @Override
    public void unblock(Doctor doctor){

    }
    public void switchToUser(Doctor doctor){

    }
    public void switchToAdmin(Doctor doctor){

    }


}

