package cs3360;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class WelcomeController {

    @FXML
    public void initialize() {
        System.out.println("WelcomeController initialized!");
    }

    /**
     * Handles the "Change Master Password" button.
     * Shows a confirmation popup and deletes the master password file if confirmed.
     * Switches to the signup screen.
     */
    @FXML
    private void changeMasterPassword() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Change Master Password");
        confirmation.setHeaderText("Change Master Password");
        confirmation.setContentText("Are you sure you want to change your master password?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Delete the master password file
                    Path masterPasswordFile = Paths.get(System.getProperty("user.home"), "Downloads", "masterPasswords", "masterPassword.txt");
                    Files.deleteIfExists(masterPasswordFile);

                    // Switch to the signup screen
                    App.setRoot("signup");
                } catch (IOException e) {
                    showError("Error", "Failed to change master password: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Handles the "Delete Account" button.
     * Shows a confirmation popup and deletes the entire masterPasswords directory if confirmed.
     * Switches to the primary screen.
     */
    @FXML
    private void deleteAccount() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Delete Account");
        confirmation.setHeaderText("Delete Account");
        confirmation.setContentText("Are you sure you want to delete your account? This action cannot be undone.");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Delete the masterPasswords folder
                    Path masterPasswordsDir = Paths.get(System.getProperty("user.home"), "Downloads", "masterPasswords");
                    if (Files.exists(masterPasswordsDir)) {
                        Files.walk(masterPasswordsDir)
                             .sorted((a, b) -> b.compareTo(a)) // Reverse order for deletion
                             .forEach(path -> {
                                 try {
                                     Files.delete(path);
                                 } catch (IOException e) {
                                     System.err.println("Failed to delete " + path + ": " + e.getMessage());
                                 }
                             });
                    }

                    // Switch to the primary screen
                    App.setRoot("primary");
                } catch (IOException e) {
                    showError("Error", "Failed to delete account: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Handles the "Log Out" button.
     * Shows a confirmation popup and switches to the primary screen if confirmed.
     */
    @FXML
    private void logOut() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Log Out");
        confirmation.setHeaderText("Log Out");
        confirmation.setContentText("Are you sure you want to log out?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Switch to the primary screen
                    App.setRoot("primary");
                } catch (IOException e) {
                    showError("Error", "Failed to log out: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Displays an error alert with the given title and content.
     *
     * @param title   Title of the alert
     * @param content Content of the alert
     */
    private void showError(String title, String content) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle(title);
        errorAlert.setHeaderText(null);
        errorAlert.setContentText(content);
        errorAlert.showAndWait();
    }
}
