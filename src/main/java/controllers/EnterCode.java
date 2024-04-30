package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import models.User;
import services.UserService;

public class EnterCode {

    @FXML
    private TextField enterCode;

    @FXML
    private Label messageLabel; // Label to display messages

    private final UserService userService = new UserService(); // Service to interact with user data
    private User currentUser; // Keep track of the current user

    // Setter method to set the current user
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    @FXML
    void enterCodeButtonClicked(ActionEvent event) {
        String enteredCode = enterCode.getText().trim();

        // Check if the entered code is not empty
        if (!enteredCode.isEmpty()) {
            // Attempt to retrieve the user based on the entered verification code
            User user = userService.getUserByVerificationCode(Integer.parseInt(enteredCode));

            if (user != null) {
                // Set the current user
                setCurrentUser(user);

                try {
                    // Load the resetpassword.fxml file
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/ResetPassword.fxml"));
                    Parent root = loader.load();

                    // Get the controller of the resetpassword.fxml file
                    ResetPassword resetPasswordController = loader.getController();

                    // Set the current user in the reset password controller
                    resetPasswordController.setCurrentUser(currentUser);

                    // Show the reset password scene
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.show();

                    // Close the current stage (Enter Code)
                    Stage currentStage = (Stage) enterCode.getScene().getWindow();
                    currentStage.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                // Display error message for wrong code
                messageLabel.setText("No user found with the entered code. Please try again.");
            }
        } else {
            // Display error message for empty code
            messageLabel.setText("Please enter the verification code.");
        }
    }

}
