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
import javafx.stage.FileChooser;
import java.io.File;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import models.Patient;
import models.User;
import services.PatientService;

import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Optional;



public class UpdateUser implements Initializable {

    @FXML
    private AnchorPane root;
    private int userId;

    @FXML
    private TextField updateusername;
    @FXML
    private TextField updatefullname;
    @FXML
    private TextField updateemail;
    @FXML
    private TextField updatephone;
    @FXML
    private Label errormessagelabel;
    @FXML
    private Label usernameLabel;
    @FXML
    private ImageView userImageView;
    @FXML
    private ImageView userImageView1;
    @FXML
    private ImageView selectedImage;

    @FXML
    private Button updateButton;

    public UpdateUser() {
        // Create PatientService with DataSource instance
        this.patientService = new PatientService();
    }

    public void initData(User user) {
        // Check if the provided user is an instance of Patient
        if (user instanceof Patient) {
            // If the user is a Patient, cast it to Patient and initialize labels
            Patient patient = (Patient) user;
            initializeLabels(patient);
        } else {
            // Handle other types of users or show an error message
            showAlert("Error", "Invalid user type");
        }
    }


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
            updateusername.setText(patient.getUsername());
            usernameLabel.setText(patient.getUsername());
            updatefullname.setText(patient.getFullname());
            updateemail.setText(patient.getEmail());
            updatephone.setText(patient.getPhone());
            String imagePath = patient.getPhoto(); // Replace with the actual method to get the image path
            if (imagePath == null || imagePath.isEmpty()) {
                imagePath = "/values/default.png";
            }

            Image image = new Image(imagePath);
            userImageView.setImage(image);
            userImageView1.setImage(image);

        } catch (Exception e) {
            // Handle any exceptions that occur during initialization
            showAlert("Error", "An error occurred during initialization: " + e.getMessage());
        }
    }
    @FXML
    void handleUploadImageClick(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Image");
        Stage stage = (Stage) updateusername.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            Image image = new Image(file.toURI().toString());
            userImageView1.setImage(image);
        }
    }

   /* @FXML
    private void handleUpdateButton(ActionEvent event) {
        // Gather the updated information from the text fields
        String newUsername = updateusername.getText();
        String newFullname = updatefullname.getText();
        String newEmail = updateemail.getText();
        String newPhone = updatephone.getText();
        String defaultImagePath = "values/default.png";
        String imagePath = (userImageView1.getImage() != null) ? userImageView1.getImage().getUrl() : defaultImagePath;

        // Set the updated information to the logged-in patient
        loggedInPatient.setUsername(newUsername);
        loggedInPatient.setFullname(newFullname);
        loggedInPatient.setEmail(newEmail);
        loggedInPatient.setPhone(newPhone);
       loggedInPatient.setPhoto(imagePath);

        // Call the modifier method to update the patient's information in the database
        patientService.modifier(loggedInPatient);

        errormessagelabel.setText("Your account has been modified!");
        RedirectToProfile();

    }*/

    @FXML
    private void handleUpdateButton(ActionEvent event) {
        try {
            // Gather the updated information from the text fields
            String newUsername = updateusername.getText();
            String newFullname = updatefullname.getText();
            String newEmail = updateemail.getText();
            String newPhone = updatephone.getText();
            // Assuming you have a TextField for confirming password
            String defaultImagePath = "values/default.png";
            String imagePath = (userImageView1.getImage() != null) ? userImageView1.getImage().getUrl() : defaultImagePath;

            // Check if any field is empty
            if (isAnyFieldEmpty(newUsername, newFullname, newEmail, newPhone)) {
                errormessagelabel.setText("Please fill in all fields!");
                return; // Exit the method if any field is empty
            }

            // Validate entered data
            if (!Patient.isValidEmail(newEmail)) {
                errormessagelabel.setText("Please enter a valid email address!");
                return;
            }

            if (!Patient.isValidPhoneNumber(newPhone)) {
                errormessagelabel.setText("Please enter a valid phone number (length 8)!");
                return;
            }

            if (!Patient.isValidFullname(newFullname)) {
                errormessagelabel.setText("Please enter a valid fullname (letters and spaces only)!");
                return;
            }



            // Set the updated information to the logged-in patient
            loggedInPatient.setUsername(newUsername);
            loggedInPatient.setFullname(newFullname);
            loggedInPatient.setEmail(newEmail);
            loggedInPatient.setPhone(newPhone);
            loggedInPatient.setPhoto(imagePath);

            // Call the modifier method to update the patient's information in the database
            patientService.modifier(loggedInPatient);

            errormessagelabel.setText("Your account has been modified!");
            RedirectToProfile();
        } catch (Exception e) {
            e.printStackTrace();
            errormessagelabel.setText("Error modifying account!");
        }
    }

    private boolean isAnyFieldEmpty(String... fields) {
        for (String field : fields) {
            if (field.isEmpty()) {
                return true;
            }
        }
        return false;
    }


    @FXML
    private Button deleteButton;


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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
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
    private void RedirectToProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Afficheruser.fxml"));
            Parent root = loader.load();

            // Pass the modified patient object to the AfficherUser controller
            AfficherUser afficherUserController = loader.getController();
            afficherUserController.setLoggedInPatient(loggedInPatient);

            // Get the current stage
            Stage stage = (Stage) updateButton.getScene().getWindow();

            // Set the new scene
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
}






















