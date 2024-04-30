package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.stage.Window;
import models.Patient;
import models.User;
import services.PatientService;

import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Optional;

public class AfficherUser implements Initializable {
    @FXML
    private AnchorPane root;
    private int userId;


    public AfficherUser() {
        // Create PatientService with DataSource instance
        this.patientService = new PatientService();
    }
    public void initData(Patient patient, User loggedInUser) {
        // Check if the provided user is an instance of Patient
        if (patient != null && loggedInUser != null && loggedInUser instanceof Patient) {
            // If both the patient and loggedInUser are not null, cast loggedInUser to Patient
            // and initialize labels with patient data
            loggedInPatient = (Patient) loggedInUser;
            initializeLabels(patient);
        } else {
            // Handle invalid data or show an error message
            showAlert("Error", "Invalid data provided");
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
    private Label phoneLabel;

    @FXML
    private Button modifyButton;

    @FXML
    private Button deleteButton;



    private Patient loggedInPatient;

    private PatientService patientService = new PatientService();

    // Method to initialize the view with patient data
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Check if the loggedInPatient object is not null
        if (loggedInPatient != null) {
            // Initialize the view with patient data
            initializeLabels(loggedInPatient);
        } else {
            // Display an error message if the loggedInPatient object is null

        }
    }

    // Method to initialize labels with patient data
    private void initializeLabels(Patient patient) {
        try {
            // Populate labels with patient data
            usernameLabel.setText(patient.getUsername());
            usernameLabel1.setText(patient.getUsername());
            fullnameLabel.setText(patient.getFullname());
            emailLabel.setText(patient.getEmail());
            phoneLabel.setText(patient.getPhone());
            String imagePath = patient.getPhoto(); // Replace with the actual method to get the image path
            if (imagePath == null || imagePath.isEmpty()) {
                imagePath = "/values/default.png";
            }

            Image image = new Image(imagePath);
            userImageView.setImage(image);

        } catch (Exception e) {
            // Handle any exceptions that occur during initialization
            showAlert("Error", "An error occurred during initialization: " + e.getMessage());
        }
    }

    // Method to handle delete button action
    @FXML
    private void handleDeleteButton() {
        if (loggedInPatient == null) {
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
                    PatientService patientService = new PatientService();
                    patientService.supprimer(loggedInPatient);

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

    @FXML
    private void RedirectToModifyProfile(ActionEvent event) {
        redirectToUpdateUserController(event, loggedInPatient);
    }


    private void redirectToUpdateUserController(ActionEvent event, User user) {
        try {
            Window window = ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/updateUser.fxml"));
            Parent root = loader.load();

            // Pass the authenticated user to the controller
            UpdateUser updateUserController = loader.getController();

            // Set the loggedInPatient in the UpdateUser controller
            updateUserController.setLoggedInPatient((Patient) user);

            // Show the UpdateUser view
            Stage stage = (Stage) window;

            // Set the new scene
            stage.setScene(new Scene(root));
        } catch (IOException e) {
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
    public void setLoggedInPatient(Patient patient) {
        this.loggedInPatient = patient;
        // After setting the patient, initialize the labels
        initializeLabels(patient);
    }



    @FXML
    private void handleLogoutButton(ActionEvent event) {
        // Code to handle the logout action goes here
        logout(event);
    }

    private void logout(ActionEvent event) {
        loggedInPatient = null;

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




    @FXML
    void RedirectToDoctorProfile(ActionEvent event) throws IOException {
        if (loggedInPatient != null) {

            RedirectToTableViewAdminUser(event, loggedInPatient);
        } else {
            showAlert("Error", "Logged in admin is null.");
        }
    }


    private void RedirectToTableViewAdminUser(ActionEvent event, User user) throws IOException {
        if (user instanceof Patient) {
            Window window = ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ShowDoctor.fxml"));
            Parent root = loader.load();

            ShowDoctor adminTableViewController = loader.getController();
            adminTableViewController.setLoggedInPatient((Patient) user);

            Stage stage = (Stage) window;

            // Set the new scene
            stage.setScene(new Scene(root));
        } else {
            showAlert("Error", "Logged in user is not a patient.");
        }
    }

    @FXML
    private void redirectToChatBot(ActionEvent event) {
        redirectToChatVSTController(event, loggedInPatient);
    }


    private void redirectToChatVSTController(ActionEvent event, User user) {
        try {
            Window window = ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chatbot.fxml"));
            Parent root = loader.load();

            // Pass the authenticated user to the controller
            Chatbot chatbotController = loader.getController();

            // Set the loggedInPatient in the UpdateUser controller
            chatbotController.setLoggedInPatient((Patient) user);

            // Show the UpdateUser view
            Stage stage = (Stage) window;

            // Set the new scene
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
