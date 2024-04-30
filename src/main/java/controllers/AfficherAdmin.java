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
import javafx.stage.Stage;
import javafx.stage.Window;
import models.Admin;
import models.User;
import services.AdminService;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class AfficherAdmin implements Initializable {
    @FXML
    private AnchorPane root;
    private int userId;



    public void initData(Admin admin, User loggedInUser) {
        // Check if the provided user is an instance of Patient
        if (admin != null && loggedInUser != null && loggedInUser instanceof Admin) {
            // If both the patient and loggedInUser are not null, cast loggedInUser to Patient
            // and initialize labels with patient data
            loggedInAdmin = (Admin) loggedInUser;
            initializeLabels(admin);
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

    private Admin loggedInAdmin;

    private AdminService adminService = new AdminService();
    public AfficherAdmin() {
        // Create PatientService with DataSource instance
        this.adminService = new AdminService();
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

    // Method to initialize labels with patient data
    private void initializeLabels(Admin admin) {
        try {
            // Populate labels with patient data
            usernameLabel.setText(admin.getUsername());
            usernameLabel1.setText(admin.getUsername());
            fullnameLabel.setText(admin.getFullname());
            emailLabel.setText(admin.getEmail());
            phoneLabel.setText(admin.getPhone());
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

    // Method to handle delete button action
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
    public void setLoggedInAdmin(Admin admin) {
        this.loggedInAdmin = admin;
        // After setting the patient, initialize the labels
        initializeLabels(admin);
    }


    @FXML
    private void RedirectToModifyAdmin(ActionEvent event) {
        redirectToUpdateUserController(event, loggedInAdmin);
    }

    private void redirectToUpdateUserController(ActionEvent event, User user) {
        try {
            Window window = ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/updateAdmin.fxml"));
            Parent root = loader.load();

            // Pass the authenticated user to the controller
            UpdateAdmin updateAdminController = loader.getController();
            updateAdminController.setLoggedInAdmin((Admin) user);
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

    @FXML
    public void redirecttoaddadmin(ActionEvent event) {
        redirectToAddAdminController(event, loggedInAdmin);
    }

    private void redirectToAddAdminController(ActionEvent event, User user) {
        try {
            Window window = ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddAdmin.fxml"));
            Parent root = loader.load();

            // Pass the authenticated user to the controller
            AddAdmin AddAdminController = loader.getController();
            AddAdminController.setLoggedInAdmin((Admin) user);


            // Get the current stage
            Stage stage = (Stage) window;

            // Set the new scene
            stage.setScene(new Scene(root));
            // Traverse up the scene graph until an AnchorPane is found


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void showAdminAction(ActionEvent event)  throws IOException{
        MenuItem menuItem = (MenuItem) event.getSource();
        Parent parent = (Parent) menuItem.getParentPopup().getOwnerNode();
        Scene scene = parent.getScene();
        Window window = scene.getWindow();
        RedirectToTableViewAdmin(event, loggedInAdmin, window);
    }


    private void RedirectToTableViewAdmin(ActionEvent event, User user,Window window) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminTableviewAdmin.fxml"));
        Parent root = loader.load();
        AdminTableViewAdmin AdminTableViewAdminController = loader.getController();
        AdminTableViewAdminController.setLoggedInAdmin((Admin) user);
        Stage stage = (Stage) window;
        stage.setScene(new Scene(root));
    }


}

