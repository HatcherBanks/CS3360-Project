package cs3360;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;

public class SignupController {
    @FXML
    private PasswordField signupPasswordField1;

    @FXML
    private PasswordField signupPasswordField2;

    @FXML
    private void createMasterPassword() {
        String password1 = signupPasswordField1.getText();
        String password2 = signupPasswordField2.getText();

        // Validate passwords
        if (password1.isEmpty() || password2.isEmpty()) {
            showAlert("Error", "Both password fields must be filled.");
            return;
        }

        if (!password1.equals(password2)) {
            showAlert("Error", "Passwords do not match.");
            return;
        }

        // Validate password strength (optional but recommended)
        if (password1.length() < 8) {
            showAlert("Error", "Password must be at least 8 characters long.");
            return;
        }

        try {
            // Encrypt and save the password
            String encryptedPassword = EncryptionUtil.encrypt(password1);
            EncryptionUtil.saveEncryptedPassword(encryptedPassword);

            // Show success message
            showAlert("Success", "Master password created successfully!");

            // Optional: Switch to login or welcome screen
            switchToLogin();
        } catch (Exception e) {
            showAlert("Error", "Failed to create master password: " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void switchToWelcome() throws IOException {
        App.setRoot("welcome");
    }

    @FXML
    private void switchToLogin() throws IOException {
        App.setRoot("login");
    }
}