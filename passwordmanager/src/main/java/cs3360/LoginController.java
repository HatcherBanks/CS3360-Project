package cs3360;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Optional;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;

public class LoginController {
    @FXML
    private PasswordField loginPasswordField;

    @FXML
    private void switchToWelcome() throws IOException {
        String enteredPassword = loginPasswordField.getText();

        // Check if master password file exists
        Path masterPasswordPath = Paths.get(System.getProperty("user.home"), "Downloads", "masterPasswords", "masterPassword.txt");
        
        if (!Files.exists(masterPasswordPath)) {
            // No master password file found - prompt to sign up
            Alert noMasterPasswordAlert = new Alert(Alert.AlertType.CONFIRMATION);
            noMasterPasswordAlert.setTitle("No Master Password");
            noMasterPasswordAlert.setHeaderText("No Master Password Found");
            noMasterPasswordAlert.setContentText("Would you like to create a master password?");
            
            Optional<ButtonType> result = noMasterPasswordAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Switch to signup page
                App.setRoot("signup");
            } else {
                // Close the application
                System.exit(0);
            }
            return;
        }

        try {
            // Read the encrypted password
            String storedEncryptedPassword = new String(Files.readAllBytes(masterPasswordPath));

            // Attempt to decrypt and verify the password
            SecretKey key = EncryptionUtil.generateSecretKey(enteredPassword);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            
            // Try to decrypt the stored password
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(storedEncryptedPassword));
            String decryptedPassword = new String(decryptedBytes);

            // Verify the password
            if (decryptedPassword.equals(enteredPassword)) {
                // Correct password - proceed to welcome screen
                App.setRoot("welcome");
            } else {
                // Incorrect password
                showErrorAlert("Incorrect Password", "The entered password is incorrect.");
            }
        } catch (Exception e) {
            // Handle decryption errors (likely incorrect password)
            showErrorAlert("Login Failed", "Unable to verify password. Please try again.");
        }
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }
}