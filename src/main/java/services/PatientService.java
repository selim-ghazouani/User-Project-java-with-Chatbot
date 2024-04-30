package services;


import models.Patient;
import org.json.JSONArray;
import utils.DataSource;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PatientService implements UseService<Patient> {

    private  Connection connection = DataSource.getInstance().getConnection();

    public void ajouter(Patient patient) {
        String roleJson = "[\"user\"]"; // Construct JSON object with role information
        String sql = "INSERT INTO medecin (username, email, phone, password, token, photo, role, fullname) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, patient.getUsername());
            statement.setString(2, patient.getEmail());
            statement.setString(3, patient.getPhone());
            statement.setString(4, patient.getPassword());
            statement.setString(5, patient.getToken());
            statement.setString(6, patient.getPhoto());
            statement.setString(7, roleJson); // Insert JSON object into role column
            statement.setString(8, patient.getFullname());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Patient ajouté avec succès !");
            } else {
                System.out.println("Échec de l'ajout du patient.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du patient : " + e.getMessage());
            throw new RuntimeException("Erreur lors de l'ajout du patient", e);
        }
    }

    @Override
    public void supprimer(Patient patient) {
        String query = "DELETE FROM medecin WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, patient.getId()); // Assuming getId() returns the patient's ID
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Patient deleted successfully!");
            } else {
                System.out.println("No patient with the specified id found.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting patient: " + e.getMessage());
            throw new RuntimeException("Error deleting patient", e);
        }
    }



    @Override
    public void modifier(Patient patient) {
        String req = "UPDATE `medecin` SET `username` = ?, `email` = ?, `phone` = ?, `password` = ?, `token` = ?, `photo` = ?, `role` = ?, `fullname` = ? WHERE `medecin`.`id` = ?";
        try (PreparedStatement st = connection.prepareStatement(req)) {
            // Set parameters for the prepared statement
            st.setString(1, patient.getUsername());
            st.setString(2, patient.getEmail());
            st.setString(3, patient.getPhone());
            st.setString(4, patient.getPassword());
            st.setString(5, patient.getToken());
            st.setString(6, patient.getPhoto());
            // Convert the role array to JSON format
            st.setString(7, new JSONArray(Arrays.asList(patient.getRole())).toString());
            st.setString(8, patient.getFullname());
            st.setInt(9, patient.getId());

            // Execute the update
            int rowsUpdated = st.executeUpdate();

            // Check if the update was successful
            if (rowsUpdated > 0) {
                System.out.println("Patient modifié avec succès !");
            } else {
                System.out.println("Aucune modification effectuée pour le patient.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour du patient : " + e.getMessage());
        }
    }

    @Override
    public void block(Patient patient) {
        // Set the token to '0'
        patient.setToken("0");

        // Using a prepared statement to prevent SQL injection
        String query = "UPDATE medecin SET token = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            // Set parameters for the prepared statement
            statement.setString(1, patient.getToken());
            statement.setInt(2, patient.getId());

            // Execute the update
            int rowsUpdated = statement.executeUpdate();

            // Check if the update was successful
            if (rowsUpdated > 0) {
                System.out.println("Patient modifié avec succès !");
            } else {
                System.out.println("Aucune modification effectuée pour le patient.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour du patient : " + e.getMessage());
        }
    }
    @Override
    public void unblock(Patient patient) {
        // Set the token to '0'
        patient.setToken("1");

        // Using a prepared statement to prevent SQL injection
        String query = "UPDATE medecin SET token = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            // Set parameters for the prepared statement
            statement.setString(1, patient.getToken());
            statement.setInt(2, patient.getId());

            // Execute the update
            int rowsUpdated = statement.executeUpdate();

            // Check if the update was successful
            if (rowsUpdated > 0) {
                System.out.println("Patient modifié avec succès !");
            } else {
                System.out.println("Aucune modification effectuée pour le patient.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour du patient : " + e.getMessage());
        }
    }

    public void switchToUser(Patient patient){

        JSONArray roleArray = new JSONArray();
        roleArray.put("user");

        // Convert the JSON array to its string representation
        String roleJson = roleArray.toString();

        // Using a prepared statement to prevent SQL injection
        String query = "UPDATE medecin SET role = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            // Set parameters for the prepared statement
            statement.setString(1, roleJson);
            statement.setInt(2, patient.getId());

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
    public void switchToAdmin(Patient patient){

        JSONArray roleArray = new JSONArray();
        roleArray.put("admin");

        // Convert the JSON array to its string representation
        String roleJson = roleArray.toString();

        // Using a prepared statement to prevent SQL injection
        String query = "UPDATE medecin SET role = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            // Set parameters for the prepared statement
            statement.setString(1, roleJson);
            statement.setInt(2, patient.getId());

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
   /* @Override
    public List<Patient> afficher() {
        List<Patient> patients = new ArrayList<>();

        String req = "SELECT * FROM `medecin` WHERE `role` = 'patient'";
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                Patient patient = new Patient(); // Create a new Patient object

                // Set the attributes of the Patient object
                patient.setId(rs.getInt("id"));
                patient.setUsername(rs.getString("username"));
                patient.setEmail(rs.getString("email"));
                patient.setPhone(rs.getString("phone"));
                patient.setPassword(rs.getString("password"));
                patient.setToken(rs.getString("token"));
                patient.setRole(rs.getString("role"));
                patient.setFullname(rs.getString("fullname"));

                patients.add(patient); // Add the patient to the list
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return patients;
    }*/

   /* @Override
    public List<Patient> afficher() throws SQLException {
        List<Patient> patients = new ArrayList<>();
        String req = "SELECT id, username, email, phone, photo, fullname FROM `medecin` WHERE `role` = 'patient'";
        Statement statement = connection.createStatement();
        ResultSet res = statement.executeQuery(req);
        while (res.next()) {
            int id = res.getInt(1);
            String username = res.getString("username");
            String email = res.getString("email");
            String phone = res.getString("phone");
            String photo = res.getString("photo");

            String fullname = res.getString("fullname");

            // Create a new Doctor object
            Patient patient = new Patient(id, username, email, phone, "", "", photo,0, "patient" ,fullname);
            patients.add(patient);
        }
        return patients;
    }*/



    public Patient getPatientByUserId(int userId) {
        String query = "SELECT * FROM medecin WHERE id = ?";
        Patient patient = null;

        try (Connection connection = this.connection;
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userId);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                patient = new Patient();
                patient.setId(resultSet.getInt("id"));
                patient.setUsername(resultSet.getString("username"));
                patient.setEmail(resultSet.getString("email"));
                patient.setPhone(resultSet.getString("phone"));
                patient.setFullname(resultSet.getString("fullname"));
                // Set any additional patient attributes here
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle SQLException appropriately, e.g., show an error message
        }

        return patient;
    }





    @Override
    public List<Patient> afficher() {
        List<Patient> patients = new ArrayList<>();
        String query = "SELECT * FROM medecin WHERE JSON_CONTAINS(role, '[\"user\"]')";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Patient p = new Patient();
                p.setId(rs.getInt(1));
                p.setUsername(rs.getString("username"));
                p.setEmail(rs.getString("email"));
                p.setPhone(rs.getString("phone"));
                p.setToken(rs.getString("token"));
                p.setPhoto(rs.getString("photo"));
                p.setFullname(rs.getString("fullname"));
                patients.add(p);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return patients;
    }


}