package controllers;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;
import models.Admin;
import models.Doctor;
import models.User;
import services.AdminService;
import services.DoctorService;

import java.io.IOException;
import java.net.URL;

import java.util.*;

public class AdminTableViewDoctor implements Initializable {
    @FXML
    private Button deleteButton;
    @FXML
    private PieChart pieChart;
    private Admin loggedInAdmin;
    @FXML
    private Label usernameLabel;
    @FXML
    private ImageView userImageView;
    @FXML
    private Button profileButton;

    @FXML
    private TableView<Doctor> tableView;

    @FXML
    private TableColumn<Doctor, Integer> idColumn;

    @FXML
    private TableColumn<Doctor, String> usernameColumn;

    @FXML
    private TableColumn<Doctor, String> fullnameColumn;

    @FXML
    private TableColumn<Doctor, String> emailColumn;

    @FXML
    private TableColumn<Doctor, String> phoneColumn;
    @FXML
    private TextField searchButton;

    @FXML
    private TableColumn<Doctor, String> photoColumn;
    @FXML
    private TableColumn<Doctor, String> SpecialiteColumn;
    @FXML
    private TableColumn<Doctor, String>  AddressColumn;
    @FXML
    private TableColumn<Doctor, Void> blockColumn;
    @FXML
    private TableColumn<Doctor, Void> TrashColumn;
    private boolean isTableViewVisible = true;

    private DoctorService doctorService = new DoctorService();
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
    private AdminService adminService = new AdminService();
    public AdminTableViewDoctor() {
        // Create PatientService with DataSource instance
        this.adminService = new AdminService();
    }
    // Method to initialize the view with patient data
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        pieChart.setVisible(false);
        if (loggedInAdmin != null) {
            initializeLabels(loggedInAdmin);

        }
        List<Doctor> Persons = doctorService.afficher();


        ObservableList<Doctor> observableList= FXCollections.observableList(Persons);
        tableView.setItems(observableList);

        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        fullnameColumn.setCellValueFactory(new PropertyValueFactory<>("fullname"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        SpecialiteColumn.setCellValueFactory(new PropertyValueFactory<>("specialite"));
        AddressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        photoColumn.setCellValueFactory(new PropertyValueFactory<>("photo"));
        photoColumn.setCellFactory(column -> {
            return new TableCell<Doctor, String>() {
                private final ImageView imageView = new ImageView();
                {
                    imageView.setFitWidth(30);
                    imageView.setFitHeight(30);
                }
                @Override
                protected void updateItem(String photoUrl, boolean empty) {
                    super.updateItem(photoUrl, empty);

                    if (empty || photoUrl == null) {
                        // If the cell is empty or the photo URL is null, clear the image
                        imageView.setImage(null);
                    } else {
                        // Load the image from the URL and set it to the image view
                        Image image = new Image(photoUrl);
                        imageView.setImage(image);
                    }

                    // Set the image view as the graphic for the cell
                    setGraphic(imageView);
                }
            };
        });
        updatePieChartData();




        blockColumn = new TableColumn<>("Block");
        blockColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Doctor, Void> call(TableColumn<Doctor, Void> param) {
                return new BlockButtonCell(doctorService);
            }
        });

        TrashColumn = new TableColumn<>("Delete");
        TrashColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Doctor, Void> call(TableColumn<Doctor, Void> param) {
                return new DeleteButtonCell(doctorService);
            }
        });

        searchButton.textProperty().addListener((observable, oldValue, newValue) -> {
            // Call a method to update the table based on the new search value
            searchDoctors();
        });
        // Add the Block button column to the TableView
        tableView.getColumns().add(blockColumn);

        tableView.getColumns().add(TrashColumn);

    }



    // Inner class for the Block button cell
    class BlockButtonCell extends TableCell<Doctor, Void> {
        private final Button blockButton;
        private final DoctorService doctorService;

        public BlockButtonCell(DoctorService doctorService) {
            this.doctorService = doctorService;
            this.blockButton = new Button();
            // Create an ImageView for the ban icon
            ImageView banIcon = new ImageView(new Image(getClass().getResourceAsStream("/values/ban.png")));
            banIcon.setFitWidth(20); // Adjust the width as needed
            banIcon.setFitHeight(20);
            // Set the ban icon as the graphic for the button
            blockButton.setGraphic(banIcon);
            blockButton.setOnAction(event -> {
                Doctor doctor = getTableView().getItems().get(getIndex());
                System.out.println("Doctor object: " + doctor);
                // Call the block method of the patientService
                if (doctor.getToken().equals("0")) {
                    // Unblock the patient
                    doctorService.unblock(doctor);
                } else {
                    // Block the patient
                    doctorService.block(doctor);
                }
                // Refresh the TableView to reflect the changes
                getTableView().refresh();
            });

        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                Doctor doctor = getTableView().getItems().get(getIndex());
                if (doctor.getToken().equals("0")) {
                    // User is blocked, set button color to green
                    blockButton.setStyle("-fx-background-color: #0f730f;");
                } else {
                    // User is not blocked, set button color to red
                    blockButton.setStyle("-fx-background-color: #ad1111;");
                }
                setGraphic(blockButton);
            }
        }
    }
    class DeleteButtonCell extends TableCell<Doctor, Void> {
        private final Button TrashButton;
        private final DoctorService doctorService;

        public DeleteButtonCell(DoctorService doctorService) {
            this.doctorService = doctorService;
            this.TrashButton = new Button();
            // Create an ImageView for the ban icon
            ImageView banIcon = new ImageView(new Image(getClass().getResourceAsStream("/values/trash.png")));
            banIcon.setFitWidth(20); // Adjust the width as needed
            banIcon.setFitHeight(20);

            // Set the ban icon as the graphic for the button
            TrashButton.setGraphic(banIcon);
            TrashButton.setOnAction(event -> {
                Doctor doctor = getTableView().getItems().get(getIndex());
                System.out.println("Doctor object: " + doctor);
                // Call the block method of the patientService

                // Unblock the patient
                doctorService.supprimer(doctor);

                // Block the patient

                // Refresh the TableView to reflect the changes
                getTableView().refresh();
            });

        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                TrashButton.setStyle("-fx-background-color: #0f736b;");
                setGraphic(null);
            } else {
                TrashButton.setStyle("-fx-background-color: #0f736b;");
                setGraphic(TrashButton);
            }
        }
    }


    @FXML
    void searchDoctors() {
        // Get the user's search criteria
        String searchText = searchButton.getText().toLowerCase();

        // Filter the list of patients based on the search criteria
        List<Doctor> allPatients = doctorService.afficher(); // Get all patients
        List<Doctor> filteredPatients = new ArrayList<>();

        for (Doctor doctor : allPatients) {
            // Check if username, email, phone, or fullname of the patient matches the search criteria
            boolean matchesUsername = doctor.getUsername().toLowerCase().contains(searchText);
            boolean matchesEmail = doctor.getEmail().toLowerCase().contains(searchText);
            boolean matchesPhone = doctor.getPhone().toLowerCase().contains(searchText);
            boolean matchesFullname = doctor.getFullname().toLowerCase().contains(searchText);
            boolean matchesAdress = doctor.getAddress().toLowerCase().contains(searchText);
            boolean matchesSpecialite = doctor.getSpecialite().toLowerCase().contains(searchText);

            // If any of the fields match the search criteria, add the patient to the filtered list
            if (matchesUsername || matchesEmail || matchesPhone || matchesFullname || matchesAdress || matchesSpecialite) {
                filteredPatients.add(doctor);
            }
        }

        // Update the TableView with the filtered patients
        ObservableList<Doctor> observableList = FXCollections.observableList(filteredPatients);
        tableView.setItems(observableList);
    }


    private void updatePieChartData() {
        // Clear existing data
        pieChart.getData().clear();

        // Fetch data for the pie chart
        List<Doctor> doctorsList = doctorService.afficher();

        // Create a map to store the count of each specialty
        Map<String, Integer> specialtyCount = new HashMap<>();
        pieChart.setPrefWidth(300);
        pieChart.setPrefHeight(250);

        // Count specialties
        for (Doctor doctor : doctorsList) {
            String specialty = doctor.getSpecialite();
            specialtyCount.put(specialty, specialtyCount.getOrDefault(specialty, 0) + 1);
        }

        // Populate pie chart data
        for (Map.Entry<String, Integer> entry : specialtyCount.entrySet()) {
            PieChart.Data data = new PieChart.Data(entry.getKey(), entry.getValue());
            pieChart.getData().add(data);
        }
    }

    @FXML
    private void handleStatButtonAction(ActionEvent event) {
        if (isTableViewVisible) {
            // Hide table view and show pie chart
            tableView.setVisible(false);
            pieChart.setVisible(true);
        } else {
            // Hide pie chart and show table view
            pieChart.setVisible(false);
            tableView.setVisible(true);
        }
        isTableViewVisible = !isTableViewVisible; // Toggle visibility flag
    }






    // Method to initialize labels with patient data
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
        MenuItem menuItem = (MenuItem) event.getSource();
        Parent parent = (Parent) menuItem.getParentPopup().getOwnerNode();
        Scene scene = parent.getScene();
        Window window = scene.getWindow();
        RedirectToTableViewAdminUser(event, loggedInAdmin, window);
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
    public void redirecttoaddadmin(ActionEvent event) {
        redirectToAddAdminController(event, loggedInAdmin);
    }

    private void redirectToAddAdminController(ActionEvent event, User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddAdmin.fxml"));
            Parent root = loader.load();

            // Pass the authenticated user to the controller
            AddAdmin AddAdminController = loader.getController();
            AddAdminController.setLoggedInAdmin((Admin) user);

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

