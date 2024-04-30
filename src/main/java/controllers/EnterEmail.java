package controllers;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.User;
import services.UserService;

import java.util.Properties;
import java.util.Random;

public class EnterEmail {
    public EnterEmail() {
        this.emailSender = new EmailSender(); // Initialize emailSender
    }

    // Constructor to inject dependencies
    public EnterEmail(EmailSender emailSender) {
        this.emailSender = emailSender; // Inject emailSender
    }
    private final UserService userService = new UserService(); // Creating an instance of UserService
    private final EmailSender emailSender;
    @FXML
    private TextField loginemail;

    @FXML
    private Button enteremailbutton;

    // Constructor to inject dependencies


    // Method to handle the action when the user clicks the "Enter Email" button
    @FXML
    void enterEmailButtonClicked(ActionEvent event) {
        String email = loginemail.getText();

        // Validate the email address
        if (isValidEmail(email)) {
            // Generate a random 4-digit verification code
            String verificationCode = generateVerificationCode();

            // Create a new User object with the email
            User user = new User();
            user.setEmail(email);

            // Set the verification code to the user
            user.setCode(Integer.parseInt(verificationCode));

            // Update the verification code in the database using UserService
            userService.updateVerificationCode(user.getEmail(), user.getCode());

            // Send the verification code via email using EmailSender class
            if (sendVerificationCode(user, verificationCode)) {
                // Display a confirmation message
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Verification Code Sent");
                alert.setHeaderText(null);
                alert.setContentText("A verification code has been sent to your email address. Please check your email.");
                alert.showAndWait();

                // Close the current stage
                Stage stage = (Stage) loginemail.getScene().getWindow();
                stage.close();

                // Load the Enter Code scene
                loadEnterCodeScene();
            } else {
                // Display an error message for email sending failure
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Email Sending Failed");
                alert.setHeaderText(null);
                alert.setContentText("Failed to send the verification code via email. Please try again later.");
                alert.showAndWait();
            }
        } else {
            // Display an error message for invalid email address
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Email");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a valid email address.");
            alert.showAndWait();
        }
    }

    // Method to validate email address
    private boolean isValidEmail(String email) {
        // Use a simple regex pattern for email validation
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }

    // Method to generate a random 4-digit verification code
    private String generateVerificationCode() {
        Random random = new Random();
        int code = 1000 + random.nextInt(9000); // Generate a random 4-digit code
        return String.valueOf(code);
    }

    // Method to send verification code via email
    private boolean sendVerificationCode(User user, String verificationCode) {
        // SMTP server properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", EmailSender.SMTP_HOST);
        props.put("mail.smtp.port", EmailSender.SMTP_PORT);

        // Create a session with authentication
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EmailSender.EMAIL_USERNAME, EmailSender.EMAIL_PASSWORD);
            }
        });

        try {
            // Create a message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EmailSender.EMAIL_USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(user.getEmail()));
            message.setSubject("Verification Code");


            String htmlContent = "<div style=\\\"max-width: 600px; margin: 0 auto; background-color: #f9f9f9; padding: 20px; border-radius: 10px; font-family: Arial, sans-serif;\\\">"; // Set background color to a light gray and use Arial font
            htmlContent += "<div style=\\\"background-color:#ffd000; color: #fff; padding: 15px; text-align: center; border-radius: 10px 10px 0 0;\\\">"; // Centered headline with blue background and white text
            htmlContent += "<h1 style=\\\"margin: 0;\\\"> Verification Code </h1>";
            htmlContent += "</div>";
            htmlContent += "<div style='margin-top: 20px; padding: 20px;'>";
            htmlContent += "<p style='font-size: 18px;  color: #333;'><strong>Hello" + user.getEmail()  + "</strong></p>"; // Replace "User" with the user's name if available
// Add a paragraph with the verification code
            htmlContent += "<p style='margin: 10px 0; font-size: 16px; color: #555; line-height: 1.5;'>Your verification code is a unique combination of numbers that serves as a security measure to verify your identity. It helps ensure that only authorized users can access the system or perform certain actions. Please use the verification code provided below to complete the verification process: <span style='background-color: #FF5722; color: #ffffff; padding: 5px; border-radius: 3px; font-family: monospace;'>" + verificationCode + "</span></p>";

            htmlContent += "</div>";
            htmlContent += "</body></html>";




            // Set the HTML content of the message
            message.setContent(htmlContent, "text/html");

            // Send the message
            Transport.send(message);

            System.out.println("Verification code sent successfully to " + user.getEmail());
            return true; // Email sent successfully
        } catch (MessagingException e) {
            System.out.println("Failed to send verification code to " + user.getEmail());
            e.printStackTrace();
            return false; // Email sending failed
        }
    }
    private void loadEnterCodeScene() {
        try {
            // Load the Enter Code FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EnterCode.fxml"));
            Parent root = loader.load();

            // Show the Enter Code scene
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
