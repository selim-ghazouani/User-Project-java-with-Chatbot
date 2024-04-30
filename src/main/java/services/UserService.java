package services;

import models.Admin;
import models.Doctor;
import models.Patient;
import models.User;
import utils.DataSource;
import org.json.JSONArray;
import org.json.JSONException;

import java.sql.*;

public class UserService {
    private DataSource dataSource;
    private Connection connection = DataSource.getInstance().getConnection();


    public void supprimer(User user) {
        // SQL query to delete a user by ID
        String req = "DELETE FROM `medecin` WHERE `id` = " + user.getId();

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            // Execute the delete query
            statement.executeUpdate(req);

            // Print success message
            System.out.println(user.getClass().getSimpleName() + " deleted successfully !");
        } catch (SQLException e) {
            // Print error message if deletion fails
            System.out.println(e.getMessage());
        }
    }


    public User login(String email) {
        String query = "SELECT id, role FROM medecin WHERE email = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int userId = resultSet.getInt("id");
                String roleJson = resultSet.getString("role");

                try {
                    JSONArray roleArray = new JSONArray(roleJson);
                    String role = roleArray.optString(0); // Get the first role in the array

                    // Retrieve the full User object from the database based on role
                    switch (role) {
                        case "user":
                            return getPatientById(userId);
                        case "medecin":
                            return getDoctorById(userId);
                        case "admin":
                            return getAdminById(userId);
                        default:
                            // Handle unknown roles
                            return null;
                    }
                } catch (JSONException e) {
                    // Handle JSON parsing exceptions
                    e.printStackTrace(); // Print the exception stack trace for debugging
                    return null;
                }
            } else {
                // If no user is found with the provided credentials, return null
                return null;
            }
        } catch (SQLException e) {
            // Handle SQL exceptions
            e.printStackTrace(); // Print the exception stack trace for debugging
            return null; // Return null to indicate a login failure
        }
    }

    // Method to retrieve a User object by ID and role from the database
    private Patient getPatientById(int userId) {
        String query = "SELECT * FROM medecin WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Retrieve patient details from the resultSet and create a Patient object
                int id = resultSet.getInt("id");
                String username = resultSet.getString("username");
                String email = resultSet.getString("email");
                String phone = resultSet.getString("phone");
                String password = resultSet.getString("password");
                String token = resultSet.getString("token");
                String photo = resultSet.getString("photo");
                int code = resultSet.getInt("code");
                String roleJson = resultSet.getString("role");

                JSONArray roleArray = new JSONArray(roleJson);
                String[] role = new String[roleArray.length()];
                for (int i = 0; i < roleArray.length(); i++) {
                    role[i] = roleArray.optString(i);
                }

                String fullname = resultSet.getString("fullname");

                return new Patient(id, username, email, phone, password, token, photo, code, role, fullname);
            }
        } catch (SQLException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Doctor getDoctorById(int userId) {
        String query = "SELECT * FROM medecin WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Retrieve doctor details from the resultSet and create a Doctor object
                int id = resultSet.getInt("id");
                String username = resultSet.getString("username");
                String email = resultSet.getString("email");
                String phone = resultSet.getString("phone");
                String password = resultSet.getString("password");
                String token = resultSet.getString("token");
                String photo = resultSet.getString("photo");
                String roleJson = resultSet.getString("role");

                JSONArray roleArray = new JSONArray(roleJson);
                String[] role = new String[roleArray.length()];
                for (int i = 0; i < roleArray.length(); i++) {
                    role[i] = roleArray.optString(i);
                }

                String specialite = resultSet.getString("specialite");
                String address = resultSet.getString("adress");
                String fullname = resultSet.getString("fullname");

                Doctor doctor = new Doctor(username, email, phone, password, token, photo, role, specialite, address, fullname);
                doctor.setId(id);
                return doctor;
            }
        } catch (SQLException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Admin getAdminById(int userId) {
        String query = "SELECT * FROM medecin WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Retrieve admin details from the resultSet and create an Admin object
                int id = resultSet.getInt("id");
                String username = resultSet.getString("username");
                String email = resultSet.getString("email");
                String phone = resultSet.getString("phone");
                String password = resultSet.getString("password");
                String token = resultSet.getString("token");
                String photo = resultSet.getString("photo");
                String roleJson = resultSet.getString("role");

                JSONArray roleArray = new JSONArray(roleJson);
                String[] role = new String[roleArray.length()];
                for (int i = 0; i < roleArray.length(); i++) {
                    role[i] = roleArray.optString(i);
                }

                String fullname = resultSet.getString("fullname");

                Admin admin = new Admin(username, email, phone, password, token, photo, role, fullname);
                admin.setId(id);
                return admin;
            }
        } catch (SQLException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void updateVerificationCode(String email, int code) {
        // SQL query to update the verification code for a user
        String query = "UPDATE medecin SET code = ? WHERE email = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, code);
            statement.setString(2, email);

            // Execute the update query
            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                // Print success message if the code is updated successfully
                System.out.println("Verification code updated successfully for email: " + email);
            } else {
                // Print a message if no rows are updated (email not found)
                System.out.println("No user found with email: " + email);
            }
        } catch (SQLException e) {
            // Print error message if the update fails
            System.out.println("Failed to update verification code for email: " + email);
            e.printStackTrace();
        }
    }

    public void updatePassword(String email, String newPassword) {
        // Update the password for the user
        String updatePasswordQuery = "UPDATE medecin SET password = ? WHERE email = ?";
        try (PreparedStatement updatePasswordStatement = connection.prepareStatement(updatePasswordQuery)) {
            updatePasswordStatement.setString(1, newPassword);
            updatePasswordStatement.setString(2, email);
            updatePasswordStatement.executeUpdate();
            System.out.println("Password updated successfully for user with email: " + email);
        } catch (SQLException e) {
            System.out.println("Failed to update password for user with email: " + email);
            e.printStackTrace();
        }
    }

    public void clearVerificationCode(String email) {
        // Set the verification code to null for the user
        String updateVerificationCodeQuery = "UPDATE medecin SET code = NULL WHERE email = ?";
        try (PreparedStatement updateVerificationCodeStatement = connection.prepareStatement(updateVerificationCodeQuery)) {
            updateVerificationCodeStatement.setString(1, email);
            updateVerificationCodeStatement.executeUpdate();
            System.out.println("Verification code cleared successfully for user with email: " + email);
        } catch (SQLException e) {
            System.out.println("Failed to clear verification code for user with email: " + email);
            e.printStackTrace();
        }
    }

    public User getUserByVerificationCode(int enteredCode) {
        String query = "SELECT * FROM medecin WHERE code = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, enteredCode);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                User user = new User();
                user.setEmail(resultSet.getString("email"));
                user.setCode(resultSet.getInt("code"));
                // You can set other user properties here if needed
                return user;
            } else {
                // No user found with the entered verification code
                return null;
            }
        } catch (SQLException e) {
            System.out.println("Error fetching user by verification code: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }



}


