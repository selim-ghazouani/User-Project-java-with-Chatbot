package controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import javafx.stage.Window;
import models.Doctor;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import services.DoctorService;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class SignupDoc {

    @FXML
    private ImageView uploadedImageView;

    @FXML
    private TextField username;

    @FXML
    private TextField fullname;

    @FXML
    private TextField email;

    @FXML
    private ChoiceBox specialite;

    @FXML
    private TextField adress;

    @FXML
    private Label SignupMessageLabel;

    @FXML
    private TextField phone;

    @FXML
    private PasswordField password;

    @FXML
    private PasswordField confirmpassword;

    @FXML
    private Label confirmPasswordLabel;
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private DoctorService doctorService = new DoctorService();

    @FXML
    void handleSignupDocButtonClick(ActionEvent event) {
        try {
            // Get user input from text fields
            String enteredUsername = username.getText();
            String enteredFullname = fullname.getText();
            String enteredEmail = email.getText();
            String enteredPhone = phone.getText();
            String enteredspecialite = (String) specialite.getValue();


            String eneterdadress = adress.getText();
            String imagePath = (uploadedImageView.getImage() != null) ? uploadedImageView.getImage().getUrl() : null;
            String enteredPassword = password.getText();
            String confirmedPassword = confirmpassword.getText();

            if (enteredUsername.isEmpty() || enteredFullname.isEmpty() || enteredEmail.isEmpty() || enteredPhone.isEmpty() || enteredPassword.isEmpty() || confirmedPassword.isEmpty() ||eneterdadress.isEmpty() || enteredspecialite.isEmpty()) {
                SignupMessageLabel.setText("Please fill in all fields!");
                return; // Exit the method if any field is empty
            }
            if (enteredPassword.equals(confirmedPassword)) {
                if (Doctor.isValidEmail(enteredEmail)) {
                    if (Doctor.isValidPhoneNumber(enteredPhone)) {
                        if (Doctor.isValidPassword(enteredPassword)) {
                            if (Doctor.isValidFullname(enteredFullname)) {
                                String encodedPassword = passwordEncoder.encode(enteredPassword);
            // Call the add function in the service class
                doctorService.ajouter(new Doctor(enteredUsername, enteredEmail, enteredPhone, encodedPassword, "1",imagePath,  new String[]{"doctor"},enteredspecialite,eneterdadress, enteredFullname));



                SignupMessageLabel.setText("Doctor has been added successfully!");

                // Clear the text fields
                username.clear();
                fullname.clear();
                email.clear();
                phone.clear();
                password.clear();
                specialite.setValue(null);
                adress.clear();
                confirmpassword.clear();
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

            SignupMessageLabel.setText("Doctor has not been added !");
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
    void LoginPage(ActionEvent event) throws IOException {
        Next(event);
    }

    void Next(ActionEvent event) throws IOException {
        // Get the current scene's window
        Window window = ((Node) event.getSource()).getScene().getWindow();

        // Load the new FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
        Parent root = loader.load();

        // Get the current stage
        Stage stage = (Stage) window;

        // Set the new scene
        stage.setScene(new Scene(root));
    }

}
