package cs3360;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;


public class WelcomeController {

    @FXML
    private VBox contentVBox; // This will be used to display content in the scroll pane (to be bound in FXML)

    @FXML
    public void initialize() {
        System.out.println("WelcomeController initialized!");
    }

    /**
     * Handles the "Change Master Password" button.
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
     * Handles the "Add New Password" button.
     * Opens a popup with text fields to collect account info and save it to a new text file.
     */
    @FXML
    private void addNewPassword() {
        // Create a new stage for the input popup
        Stage addPasswordStage = new Stage();

        // Create the text fields and buttons for the popup
        Label accountLabel = new Label("Account Name:");
        TextField accountNameField = new TextField();
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        Label passwordLabel = new Label("Password:");
        TextField passwordField = new TextField();

        Button addButton = new Button("Add");
        Button cancelButton = new Button("Cancel");

        // Setup layout for the popup (VBox)
        VBox layout = new VBox(10);
        layout.getChildren().addAll(accountLabel, accountNameField, usernameLabel, usernameField, passwordLabel, passwordField, addButton, cancelButton);

        // Handle the "Add" button action
        addButton.setOnAction(e -> {
            String accountName = accountNameField.getText();
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (accountName.isEmpty() || username.isEmpty() || password.isEmpty()) {
                showError("Input Error", "Please fill in all fields.");
            } else {
                try {
                    // Save the data to a text file
                    File newFile = new File(System.getProperty("user.home"), "Downloads/passwords/" + accountName + ".txt");
                    newFile.getParentFile().mkdirs(); // Create directory if not exists
                    BufferedWriter writer = new BufferedWriter(new FileWriter(newFile));
                    writer.write("Account Name: " + accountName + "\n");
                    writer.write("Username: " + username + "\n");
                    writer.write("Password: " + password + "\n");
                    writer.close();

                    // Update the scroll pane with new content
                    updateScrollPane(newFile);

                    // Close the popup
                    addPasswordStage.close();
                } catch (IOException ex) {
                    showError("Error", "Failed to save password: " + ex.getMessage());
                }
            }
        });

        // Handle the "Cancel" button action
        cancelButton.setOnAction(e -> addPasswordStage.close());

        // Show the popup
        addPasswordStage.setScene(new javafx.scene.Scene(layout, 300, 250));
        addPasswordStage.show();
    }

    /**
     * Updates the scroll pane content with the new password file's details.
     * @param file The file to be displayed.
     */
    private void updateScrollPane(File file) {
        try {
            // Read the file content and display it in the scroll pane
            String content = new String(Files.readAllBytes(file.toPath()));
            Label newContentLabel = new Label(content);
            newContentLabel.setFont(new Font(16));
            contentVBox.getChildren().add(newContentLabel); // Add to the content VBox displayed in the scroll pane
        } catch (IOException e) {
            showError("Error", "Failed to display the new password: " + e.getMessage());
        }
    }

    /**
     * Displays an error alert with the given title and content.
     */
    private void showError(String title, String content) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle(title);
        errorAlert.setHeaderText(null);
        errorAlert.setContentText(content);
        errorAlert.showAndWait();
    }
}
