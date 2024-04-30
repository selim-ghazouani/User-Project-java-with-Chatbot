package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import models.Admin;
import models.Doctor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import services.PatientService;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import models.Patient;
import models.User;
import utils.DataSource;
import javafx.scene.Node;


import javafx.stage.Window;

import java.io.IOException;
import java.security.MessageDigest;
import java.sql.*;
import java.util.ResourceBundle;
import  java.net.URL;

import services.UserService;
public class Login implements Initializable {
    @FXML
    private Button cancelButton;
    @FXML
    private Label LoginMessageLabel;
    @FXML
    private TextField loginemail;

    @FXML
    private PasswordField loginpassword;
    private Patient loggedInPatient = null;
    private UserService userService = new UserService();
    public void setLoggedInPatient(Patient patient) {
        this.loggedInPatient = patient;
    }

    private Connection connection = DataSource.getInstance().getConnection();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){

    }


    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @FXML
    void loginButtonOnAction(ActionEvent event) {
        String email = loginemail.getText();
        String password = loginpassword.getText();

        // Retrieve the user from the database using the email
        User authenticatedUser = userService.login(email);

        if (authenticatedUser != null) {
            // Verify the entered password against the stored hashed password

            if (passwordEncoder.matches(password, authenticatedUser.getPassword())) {
                // Passwords match, user is authenticated
                if (authenticatedUser.isBlocked()) {
                    LoginMessageLabel.setText("Your account has been blocked. Please contact support.");
                } else {
                    String role = authenticatedUser.getRole().toLowerCase();

                    switch (role) {
                        case "user":
                            if (authenticatedUser instanceof Patient) {
                                redirectToAfficherUserController(event, (Patient) authenticatedUser);
                            } else {
                                LoginMessageLabel.setText("Invalid user type");
                            }
                            break;
                        case "medecin":
                            if (authenticatedUser instanceof Doctor) {
                                redirectToAfficherDoctorController(event, (Doctor) authenticatedUser);
                            } else {
                                LoginMessageLabel.setText("Invalid user type");
                            }
                            break;
                        case "admin":
                            if (authenticatedUser instanceof Admin) {
                                redirectToAfficherAdminController(event, (Admin) authenticatedUser);
                            } else {
                                LoginMessageLabel.setText("Invalid user type");
                            }
                            break;
                        default:
                            LoginMessageLabel.setText("Unknown role");
                            break;
                    }
                }
            } else {
                // Passwords don't match
                LoginMessageLabel.setText("Invalid email or password");
            }
        } else {
            // User not found in the database
            LoginMessageLabel.setText("Invalid email or password");
        }
    }




    private String encryptPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private void redirectToAfficherAdminController(ActionEvent event, User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherAdmin.fxml"));
            Parent root = loader.load();

            // Pass the authenticated user to the controller
            AfficherAdmin controller = loader.getController();

            // Check if the user is an instance of Patient
            if (user instanceof Admin) {
                // If the user is a Patient, cast it to Patient and set it as loggedInPatient
                Admin admin = (Admin) user;
                controller.setLoggedInAdmin(admin);
            } else {
                // Handle other types of users or show an error message
                showAlert("Error", "Invalid user type");
                return; // Exit the method if the user is not a Patient
            }

            // Show the AfficherUser view
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();

            // Close the login window
            ((Node)(event.getSource())).getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void redirectToAfficherDoctorController(ActionEvent event, User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherDoctor.fxml"));
            Parent root = loader.load();

            // Pass the authenticated user to the controller
            AfficherDoctor controller = loader.getController();

            // Check if the user is an instance of Patient
            if (user instanceof Doctor) {
                // If the user is a Patient, cast it to Patient and set it as loggedInPatient
                Doctor doctor = (Doctor) user;
                controller.setLoggedInDoctor(doctor);
            } else {
                // Handle other types of users or show an error message
                showAlert("Error", "Invalid user type");
                return; // Exit the method if the user is not a Patient
            }

            // Show the AfficherUser view
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();

            // Close the login window
            ((Node)(event.getSource())).getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void redirectToAfficherUserController(ActionEvent event, User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherUser.fxml"));
            Parent root = loader.load();

            // Pass the authenticated user to the controller
            AfficherUser controller = loader.getController();

            // Check if the user is an instance of Patient
            if (user instanceof Patient) {
                // If the user is a Patient, cast it to Patient and set it as loggedInPatient
                Patient patient = (Patient) user;
                controller.setLoggedInPatient(patient);
            } else {
                // Handle other types of users or show an error message
                showAlert("Error", "Invalid user type");
                return; // Exit the method if the user is not a Patient
            }

            // Show the AfficherUser view
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();

            // Close the login window
            ((Node)(event.getSource())).getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


   /* @FXML
    void loginButtonOnAction(ActionEvent event) {
        String email = loginemail.getText();
        String password = loginpassword.getText();

        // Call the login method from UserService to authenticate the user
        User user = userService.login(email, password);

        if (user != null) {
            // Redirect based on the user's role
            switch (user.getRole()) {
                case "patient":
                    redirectToPatientPage(user.getId());
                    break;
                case "doctor":
                    //redirectToDoctorPage(event);
                    break;
                case "admin":
                    //redirectToAdminPage(event);
                    break;
                default:
                    LoginMessageLabel.setText("Invalid Role. Please contact administrator.");
            }
        } else {
            showAlert("Invalid Login", "Invalid email or password. Please try again.");
        }
    }*/

   /* @FXML
    void loginButtonOnAction(ActionEvent event){

String verifyLogin = "SELECT count(1) FROM medecin WHERE email = '" + loginemail.getText() + "' AND password = '" + loginpassword.getText() + "'";
try {
   Statement statement = connection.createStatement();
   ResultSet queryResult = statement.executeQuery(verifyLogin);
   while (queryResult.next()){
       if (queryResult.getInt(1)==1){
           LoginMessageLabel.setText("mabrouk");
       }
       else {
           LoginMessageLabel.setText("tahchee");
       }
   }

}catch (Exception e){
e.printStackTrace();
e.getCause();
}
    }*/



    // Method to display an alert message
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    @FXML
    private void cancelButtonOnAction() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void redirectToPatientPage(int userId) {
        try {
            // Fetch patient data based on the user ID
            PatientService patientService = new PatientService();
            Patient patient = patientService.getPatientByUserId(userId);

            // Check if patient data is found
            if (patient != null) {
                // Get the current stage
                Stage stage = (Stage) cancelButton.getScene().getWindow();

                // Load the AfficherUser FXML file
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherUser.fxml"));
                Parent root = loader.load();

                // Get the controller instance
                AfficherUser controller = loader.getController();

                // Set the logged in patient
                controller.setLoggedInPatient(patient);

                // Set the new scene
                stage.setScene(new Scene(root));
            } else {
                // Display an error message if patient data is not found
                showAlert("Patient Data Not Found", "Patient data not found.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle IOException appropriately, e.g., show an error message
            LoginMessageLabel.setText("An error occurred. Please try again later.");
        }
    }



    @FXML
    private void cancelButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    /*public void validateLogin(){

        String verifyLogin = "SELECT * FROM medecin WHERE email = '" + loginemail.getText() + "' AND password = '" + loginpassword.getText() + "'";
        try {
            Statement statement = connection.createStatement();
            ResultSet queryResult = statement.executeQuery(verifyLogin);

            if (queryResult.next()) {
                String role = queryResult.getString("role");
                switch (role) {
                    case "patient":
                       // redirectToPatientPage();
                        break;
                    case "doctor":
                        // redirectToDoctorPage();
                        break;
                    case "admin":
                        // redirectToAdminPage();
                        break;
                    default:
                        LoginMessageLabel.setText("Invalid Role. Please contact administrator.");
                }
            } else {
                LoginMessageLabel.setText("Invalid Login. Please try again");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle SQLException appropriately, e.g., show an error message
            LoginMessageLabel.setText("An error occurred. Please try again later.");
        }
    }
*/

 /* public void createAccountForm(){
        try {
           Parent root = FXMLLoader.load(getClass().getResource("/Signup.fxml"));
            Stage signupstage = new Stage();
            signupstage.initStyle(StageStyle.UNDECORATED);
            signupstage.setScene(new Scene(root,520,400));
            signupstage.show();

        }catch(Exception e) {
            e.printStackTrace();

      }
      }*/






    @FXML
    void SignUpage(ActionEvent event) throws IOException {
        Next(event);
    }

    void Next(ActionEvent event) throws IOException {
        // Get the current scene's window
        Window window = ((Node) event.getSource()).getScene().getWindow();

        // Load the new FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Signup.fxml"));
        Parent root = loader.load();

        // Get the current stage
        Stage stage = (Stage) window;

        // Set the new scene
        stage.setScene(new Scene(root));
    }


    @FXML
    void RedirectToEnetrEmail(ActionEvent event) throws IOException {
        RedirectToEnterEmailPage(event);
    }

    void RedirectToEnterEmailPage(ActionEvent event) throws IOException {
        // Get the current scene's window
        Window window = ((Node) event.getSource()).getScene().getWindow();

        // Load the new FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/EnterEmail.fxml"));
        Parent root = loader.load();

        // Get the current stage
        Stage stage = (Stage) window;

        // Set the new scene
        stage.setScene(new Scene(root));
    }



}






