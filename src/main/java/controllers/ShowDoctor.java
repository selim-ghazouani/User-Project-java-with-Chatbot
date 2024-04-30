package controllers;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.Window;
import models.Doctor;
import models.Patient;
import models.User;
import services.DoctorService;
import services.PatientService;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ShowDoctor  implements Initializable {
    @FXML
    private AnchorPane root;
    private int userId;
    @FXML
    private VBox doctorListVBox;

    @FXML
    private Label DoctorFullname;
    @FXML
    private Label DoctorSpecialite;
    @FXML
    private TextField searchtext;
    @FXML
    private Label DoctorAdress;
    @FXML
    private Label DoctorPhone;
    @FXML
    private ImageView doctorImageView;
    @FXML
    private VBox doctorCardsContainer;
    private Patient loggedInPatient;
    private DoctorService doctorService = new DoctorService();
    private PatientService patientService = new PatientService();

    public ShowDoctor() {
        // Create PatientService with DataSource instance
        this.patientService = new PatientService();
    }
    public void setLoggedInPatient(Patient patient) {
        this.loggedInPatient = patient;
        // After setting the patient, initialize the labels
loadDoctors();
    }

    public void initData(User user) {
        // Check if the provided user is an instance of Patient
        if (user instanceof Patient) {
            // If the user is a Patient, cast it to Patient and initialize labels
            Patient patient = (Patient) user;
            loadDoctors();
        } else {
            // Handle other types of users or show an error message
            showAlert("Error", "Invalid user type");
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Check if the loggedInPatient object is not null
        if (loggedInPatient != null) {
            // Initialize the view with patient data
            loadDoctors();
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

    private void loadDoctors() {
        try {
            List<Doctor> doctors = doctorService.afficher();

            // Check if doctors are retrieved successfully
            if (doctors != null && !doctors.isEmpty()) {
                int doctorsPerRow = 3; // Number of doctors per row
                int rowCount = (int) Math.ceil((double) doctors.size() / doctorsPerRow);

                for (int i = 0; i < rowCount; i++) {
                    HBox row = new HBox(10); // Spacing between doctors
                    row.getStyleClass().add("doctor-row"); // Add a style class for styling

                    // Add doctors to the row
                    int startIndex = i * doctorsPerRow;
                    int endIndex = Math.min((i + 1) * doctorsPerRow, doctors.size());
                    for (int j = startIndex; j < endIndex; j++) {
                        Doctor doctor = doctors.get(j);
                        VBox doctorCard = createDoctorCard(doctor);
                        row.getChildren().add(doctorCard);
                    }

                    // Add the row to the doctorListVBox
                    doctorListVBox.getChildren().add(row);
                }
            } else {
                showAlert("Error", "No doctors found in the database.");
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to retrieve doctors from the database: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private VBox createDoctorCard(Doctor doctor) {

        VBox card = new VBox();
        card.getStyleClass().add("doctor-card");
        Color customColor = Color.rgb(234, 187, 133);
        HBox.setMargin(card, new Insets(8, 20, 10, 8));
        // Add styling for spacing and background color
        card.setPadding(new Insets(20)); // Add padding for spacing
        card.setBackground(new Background(new BackgroundFill(customColor, new CornerRadii(50), Insets.EMPTY))); // Set light gray background with rounded corners
        card.setPrefWidth(150); // Adjust the width as needed
        card.setPrefHeight(150); // Adjust the height as needed
        card.setAlignment(Pos.CENTER);
        card.setOnMouseClicked(event -> showDoctorInfo(doctor));

        // Create an ImageView for the doctor's image
        ImageView imageView = new ImageView();
        // Set the doctor's image (you need to replace "doctor.getPhoto()" with the appropriate method to get the image URL from the Doctor object)
        String imageUrl = doctor.getPhoto();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            imageView.setImage(new Image(imageUrl));
        } else {
            // If no image URL is provided, you can set a default image here
            // For example: imageView.setImage(new Image("default_image_url"));
        }
        imageView.setFitWidth(85); // Adjust the width of the image
        imageView.setFitHeight(85); // Adjust the height of the image

        // Populate the card with doctor information
        Label nameLabel = new Label("Name: " + doctor.getFullname());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
        nameLabel.setPadding(new Insets(5, 0, 0, 0));
        Label specialtyLabel = new Label("Specialty: " + doctor.getSpecialite());
        specialtyLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
        specialtyLabel.setPadding(new Insets(5, 0, 0, 0));
        // Add more labels for other information if needed

        card.getChildren().addAll(imageView, nameLabel, specialtyLabel);

        return card;
    }

    private void showDoctorInfo(Doctor doctor) {
        // Implement logic to display the selected doctor's information in the big card
        // Update the labels in the big card with the information of the selected doctor
        DoctorFullname.setText( doctor.getFullname());
        DoctorSpecialite.setText( doctor.getSpecialite());
        DoctorAdress.setText( doctor.getAddress());
        DoctorPhone.setText( doctor.getPhone());

        // Load and set the image
        String imageUrl = doctor.getPhoto(); // Assuming getPhoto() returns the URL of the doctor's image
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Image image = new Image(imageUrl);
            doctorImageView.setImage(image);
        } else {
            // If no image URL is provided, you can set a default image here
            // For example: doctorImageView.setImage(new Image("default_image_url"));
        }
    }

    @FXML
    void searchDoctors(String searchText) {
        try {
            List<Doctor> allDoctors = doctorService.afficher();

            // Clear existing doctor cards
            doctorListVBox.getChildren().clear();

            // Display filtered doctors if search text is not empty
            if (!searchText.isEmpty()) {
                List<Doctor> filteredDoctors = new ArrayList<>();

                // Filter doctors based on search text
                for (Doctor doctor : allDoctors) {
                    if (doctor.getFullname().toLowerCase().contains(searchText.toLowerCase()) ||
                            doctor.getSpecialite().toLowerCase().contains(searchText.toLowerCase())) {
                        filteredDoctors.add(doctor);
                    }
                }

                displayDoctors(filteredDoctors);
            } else {
                // Display all doctors if search text is empty
                displayDoctors(allDoctors);
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to retrieve doctors from the database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void displayDoctors(List<Doctor> doctors) {
        // Display the filtered doctors
        for (Doctor doctor : doctors) {
            VBox doctorCard = createDoctorCard(doctor);
            doctorListVBox.getChildren().add(doctorCard);
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