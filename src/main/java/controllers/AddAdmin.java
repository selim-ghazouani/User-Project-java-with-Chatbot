package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import models.Admin;

import models.User;
import services.AdminService;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class AddAdmin implements Initializable {
    @FXML
    private Label usernameLabel;
    @FXML
    private ImageView userImageView;

    @FXML
    private ImageView uploadedImageView;

    @FXML
    private TextField username;

    @FXML
    private TextField fullname;

    @FXML
    private Label SignupMessageLabel;
    @FXML
    private Button profileButton;

    @FXML
    private TextField email;

    @FXML
    private TextField phone;

    @FXML
    private PasswordField password;

    @FXML
    private PasswordField confirmpassword;
    private Admin loggedInAdmin;
    private AdminService adminService = new AdminService();

    public AddAdmin() {
        // Create PatientService with DataSource instance
        this.adminService = new AdminService();
    }

    public void setLoggedInAdmin(Admin admin) {
        this.loggedInAdmin = admin;
        // After setting the patient, initialize the labels
        initializeLabels(admin);

    }
    public void initData(Admin admin, User loggedInUser) {
        // Check if the provided user is an instance of Patient
        if (admin != null && loggedInUser != null && loggedInUser instanceof Admin) {
            // If both the patient and loggedInUser are not null, cast loggedInUser to Patient
            // and initialize labels with patient data
            loggedInAdmin = (Admin) loggedInUser;

        } else {
            // Handle invalid data or show an error message
            showAlert("Error", "Invalid data provided");
        }
    }
    // Method to initialize the view with patient data
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Check if the loggedInPatient object is not null
        if (loggedInAdmin != null) {
            // Initialize the view with patient data
            initializeLabels(loggedInAdmin);
        } else {
            // Display an error message if the loggedInPatient object is null

        }
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private void initializeLabels(Admin admin) {
        try {
            // Populate labels with patient data
            usernameLabel.setText(admin.getUsername());
            String imagePath = admin.getPhoto(); // Replace with the actual method to get the image path
            if (imagePath != null && !imagePath.isEmpty()) {
                Image image = new Image(imagePath);
                userImageView.setImage(image);
            }
        } catch (Exception e) {
            // Handle any exceptions that occur during initialization
            showAlert("Error", "An error occurred during initialization: " + e.getMessage());
        }
    }
    @FXML
    private void RedirectToProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherAdmin.fxml"));
            Parent root = loader.load();

            // Pass the modified patient object to the AfficherUser controller
            AfficherAdmin afficherAdminController = loader.getController();
            afficherAdminController.setLoggedInAdmin(loggedInAdmin);

            // Get the current stage
            Stage stage = (Stage) profileButton.getScene().getWindow();

            // Set the new scene
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleSignupButtonClick(ActionEvent event) {
        try {
            // Get user input from text fields
            String enteredUsername = username.getText();
            String enteredFullname = fullname.getText();
            String enteredEmail = email.getText();
            String enteredPhone = phone.getText();
            String defaultImagePath = "values/default.png";
            String enteredPassword = password.getText();
            String confirmedPassword = confirmpassword.getText();
            String imagePath = (uploadedImageView.getImage() != null) ? uploadedImageView.getImage().getUrl() : defaultImagePath;
            if (enteredUsername.isEmpty() || enteredFullname.isEmpty() || enteredEmail.isEmpty() || enteredPhone.isEmpty() || enteredPassword.isEmpty() || confirmedPassword.isEmpty()) {
                SignupMessageLabel.setText("Please fill in all fields!");
                return; // Exit the method if any field is empty
            }

            // Validate entered data
            if (enteredPassword.equals(confirmedPassword)) {
                if (Admin.isValidEmail(enteredEmail)) {
                    if (Admin.isValidPhoneNumber(enteredPhone)) {
                        if (Admin.isValidPassword(enteredPassword)) {
                            if (Admin.isValidFullname(enteredFullname)) {
                                // Call the add function in the service class
                                adminService.ajouter(new Admin(enteredUsername, enteredEmail, enteredPhone, enteredPassword, "1", imagePath,  new String[]{"admin"}, enteredFullname));

                                SignupMessageLabel.setText("Admin has been added successfully!");

                                // Clear the text fields
                                username.clear();
                                fullname.clear();
                                email.clear();
                                phone.clear();
                                password.clear();
                                confirmpassword.clear();
                                //uploadedImageView.clear();
                            } else {
                                SignupMessageLabel.setText("Please enter a valid fullname (letters only)!");
                            }
                        } else {
                            SignupMessageLabel.setText("Please enter a valid password (minimum length 6)!");
                        }
                    } else {
                        SignupMessageLabel.setText("Please enter a valid phone number (length 8)!");
                    }
                } else {
                    SignupMessageLabel.setText("Please enter a valid email address!");
                }
            } else {
                SignupMessageLabel.setText("Passwords do not match!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            SignupMessageLabel.setText("User has not been added !");
        }
    }



    @FXML
    void handleUploadImageClick(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Image");
        Stage stage = (Stage) username.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            Image image = new Image(file.toURI().toString());
            uploadedImageView.setImage(image);
        }
    }

    @FXML
    private Button deleteButton;


    @FXML
    private void handleDeleteButton() {
        if (loggedInAdmin == null) {
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
                    AdminService adminService = new AdminService();
                    adminService.supprimer(loggedInAdmin);

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

    @FXML
    private void RedirectToModifyAdmin(ActionEvent event) {
        redirectToUpdateUserController(event, loggedInAdmin);
    }

    private void redirectToUpdateUserController(ActionEvent event, User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/updateAdmin.fxml"));
            Parent root = loader.load();

            // Pass the authenticated user to the controller
            UpdateAdmin updateAdminController = loader.getController();
            updateAdminController.setLoggedInAdmin((Admin) user);

            Node sourceNode = (Node) event.getSource();
            Parent parent = sourceNode.getParent();
            AnchorPane anchorPane = null;

            // Traverse up the scene graph until an AnchorPane is found
            while (parent != null) {
                if (parent instanceof AnchorPane) {
                    anchorPane = (AnchorPane) parent;
                    break;
                }
                parent = parent.getParent();
            }

            if (anchorPane != null) {
                // Clear the anchor pane and add the new content
                anchorPane.getChildren().clear();
                anchorPane.getChildren().add(root);
            } else {
                System.err.println("No AnchorPane found in the parent hierarchy.");
            }

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
        loggedInAdmin = null;

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
    void showUserAction(ActionEvent event) throws IOException {
        if (loggedInAdmin != null) {
            MenuItem menuItem = (MenuItem) event.getSource();
            Parent parent = (Parent) menuItem.getParentPopup().getOwnerNode();
            Scene scene = parent.getScene();
            Window window = scene.getWindow();
            RedirectToTableViewAdminUser(event, loggedInAdmin, window);
        } else {
            showAlert("Error", "Logged in admin is null.");
        }
    }


    private void RedirectToTableViewAdminUser(ActionEvent event, User user, Window window) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminTableview.fxml"));
        Parent root = loader.load();
        AdminTableView AdminTableViewController = loader.getController();
        AdminTableViewController.setLoggedInAdmin((Admin) user);
        Stage stage = (Stage) window;
        stage.setScene(new Scene(root));
    }

    @FXML
    void showDoctorAction(ActionEvent event)  throws IOException{
        if (loggedInAdmin != null) {
            MenuItem menuItem = (MenuItem) event.getSource();
            Parent parent = (Parent) menuItem.getParentPopup().getOwnerNode();
            Scene scene = parent.getScene();
            Window window = scene.getWindow();
            RedirectToTableViewAdminDoctor(event, loggedInAdmin, window);
        } else {
            showAlert("Error", "Logged in admin is null.");
        }
    }


    private void RedirectToTableViewAdminDoctor(ActionEvent event, User user,Window window) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminTableviewDoctor.fxml"));
        Parent root = loader.load();
        AdminTableViewDoctor AdminTableViewDoctorController = loader.getController();
        AdminTableViewDoctorController.setLoggedInAdmin((Admin) user);
        Stage stage = (Stage) window;
        stage.setScene(new Scene(root));
    }

}
