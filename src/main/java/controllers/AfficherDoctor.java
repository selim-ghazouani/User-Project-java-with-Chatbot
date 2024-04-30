package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import models.Doctor;
import models.User;
import services.DoctorService;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class AfficherDoctor implements Initializable {

    @FXML
    private AnchorPane root;
    private int userId;


    public AfficherDoctor() {
        // Create PatientService with DataSource instance
        this.doctorService = new DoctorService();
    }
    public void initData(User user) {
        // Check if the provided user is an instance of Patient
        if (user instanceof Doctor) {
            // If the user is a Patient, cast it to Patient and initialize labels
            Doctor doctor = (Doctor) user;
            initializeLabels(doctor);
        } else {
            // Handle other types of users or show an error message
            showAlert("Error", "Invalid user type");
        }
    }

    @FXML
    private Label usernameLabel;

    @FXML
    private Label usernameLabel1;
    @FXML
    private ImageView userImageView;
    @FXML
    private Label fullnameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label specialiteLabel;

    @FXML
    private Label adressLabel;
    @FXML
    private Label phoneLabel;

    @FXML
    private Button modifyButton;

    @FXML
    private Button deleteButton;

    private Doctor loggedInDoctor;

    private DoctorService doctorService = new DoctorService();

    // Method to initialize the view with patient data
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Check if the loggedInPatient object is not null
        if (loggedInDoctor != null) {
            // Initialize the view with patient data
            initializeLabels(loggedInDoctor);
        } else {
            // Display an error message if the loggedInPatient object is null

        }
    }

    // Method to initialize labels with patient data
    private void initializeLabels(Doctor doctor) {
        try {
            // Populate labels with patient data
            usernameLabel.setText(doctor.getUsername());
            usernameLabel1.setText(doctor.getUsername());
            fullnameLabel.setText(doctor.getFullname());
            emailLabel.setText(doctor.getEmail());
            specialiteLabel.setText(doctor.getSpecialite());
            adressLabel.setText(doctor.getAddress());
            phoneLabel.setText(doctor.getPhone());
            String imagePath = doctor.getPhoto(); // Replace with the actual method to get the image path
            if (imagePath != null && !imagePath.isEmpty()) {
                Image image = new Image(imagePath);
                userImageView.setImage(image);
            }
        } catch (Exception e) {
            // Handle any exceptions that occur during initialization
            showAlert("Error", "An error occurred during initialization: " + e.getMessage());
        }
    }

    // Method to handle delete button action
    @FXML
    private void handleDeleteButton() {
        if (loggedInDoctor == null) {
            // Handle the case where loggedInPatient is null
            showAlert("Error", "Logged in patient is null.");
            return;
        }

        // Create an alert to confirm the deletion
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Account");
        alert.setHeaderText("Are you sure you want to delete your account?");
        alert.setContentText("This action cannot be undone.");

        // Show the alert and wait for user response
        Optional<ButtonType> result = alert.showAndWait();

        // If the user confirms deletion, delete the patient account
        result.ifPresent(buttonType -> {
            if (buttonType == ButtonType.OK) {
                try {
                    // Call the supprimer method from the PatientService class to delete the account
                    DoctorService doctorService = new DoctorService();
                    doctorService.supprimer(loggedInDoctor);

                    // Close the current window after successful deletion
                    Stage stage = (Stage) deleteButton.getScene().getWindow();
                    stage.close();

                    // Redirect to the signup page
                    redirectToSignupPage();
                } catch (Exception e) {
                    // Handle any exceptions that occur during deletion
                    showAlert("Error", "An error occurred while deleting the patient account: " + e.getMessage());
                    e.printStackTrace(); // Log the exception
                }
            }
        });
    }

    private void redirectToSignupPage() {
        try {
            // Load the Signup.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Signup.fxml"));
            Parent root = loader.load();

            // Create a new stage for the signup window
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            // Handle any IOException that might occur during loading
            showAlert("Error", "An error occurred while loading the signup page: " + e.getMessage());
            e.printStackTrace();
        }
    }


    // Helper method to display an alert message
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }



    // Setter method for the loggedInPatient variable
    public void setLoggedInDoctor(Doctor doctor) {
        this.loggedInDoctor = doctor;
        // After setting the patient, initialize the labels
        initializeLabels(doctor);
    }


    @FXML
    private void RedirectToModifyDoctor(ActionEvent event) {
        redirectToUpdateDoctorController(event, loggedInDoctor);
    }


    private void redirectToUpdateDoctorController(ActionEvent event, User user) {
        try {
            Window window = ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/updatedoctor.fxml"));
            Parent root = loader.load();

            // Pass the authenticated user to the controller
            UpdateDoctor updateDoctorController = loader.getController();

            // Set the loggedInPatient in the UpdateUser controller
            updateDoctorController.setLoggedInDoctor((Doctor) user);

            Stage stage = (Stage) window;

            // Set the new scene
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleLogoutButton(ActionEvent event) {
        // Code to handle the logout action goes here
        logout(event);
    }

    private void logout(ActionEvent event) {
        loggedInDoctor = null;

        // Redirect the user to the login page
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
