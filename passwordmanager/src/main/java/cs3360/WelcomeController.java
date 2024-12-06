package cs3360;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class WelcomeController {

    @FXML
    private VBox contentVBox; // This will be used to display content in the scroll pane (to be bound in FXML)

    // Store the current master password for encryption/decryption
    private String currentMasterPassword;

    @FXML
    public void initialize() {
        System.out.println("WelcomeController initialized!");

        // Prompt for master password to decrypt existing entries
        promptForMasterPassword();

        // Load existing passwords into the content VBox
        loadExistingPasswords();
    }


    private void promptForMasterPassword() {
        // Create a custom dialog for entering the master password
        javafx.scene.control.Dialog<String> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle("Master Password");
        dialog.setHeaderText("Enter your master password to view saved passwords");
    
        // Create the password field
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Master Password");
    
        // Set up the layout for the dialog
        GridPane grid = new GridPane();
        grid.add(new javafx.scene.control.Label("Master Password:"), 0, 0);
        grid.add(passwordField, 1, 0);
    
        dialog.getDialogPane().setContent(grid);
    
        // Add an OK button to the dialog
        dialog.getDialogPane().getButtonTypes().add(javafx.scene.control.ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(javafx.scene.control.ButtonType.CANCEL);
    
        // Handle the result of the dialog
        dialog.setResultConverter(button -> {
            if (button == javafx.scene.control.ButtonType.OK) {
                return passwordField.getText();
            }
            return null; // Return null if cancel is pressed
        });
    
        dialog.showAndWait().ifPresent(password -> {
            currentMasterPassword = password;
        });
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
     * Opens a popup with text fields to collect account info and save it to a new encrypted text file.
     */
    @FXML
    private void addNewPassword() {
        // Check if master password is set
        if (currentMasterPassword == null || currentMasterPassword.isEmpty()) {
            showError("Error", "Please set a master password first.");
            return;
        }

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
                    // Save the data to a text file in the masterPasswords/passwords folder
                    File passwordsDir = new File(System.getProperty("user.home"), "Downloads/masterPasswords/passwords");
                    passwordsDir.mkdirs(); // Ensure the directory exists
                    File newFile = new File(passwordsDir, accountName + ".txt");
                    
                    // Encrypt the password
                    String encryptedPassword = EncryptionUtil.encryptEntry(password, currentMasterPassword);
                    
                    // Write encrypted data
                    BufferedWriter writer = new BufferedWriter(new FileWriter(newFile));
                    writer.write("Account Name: " + accountName + "\n");
                    writer.write("Username: " + username + "\n");
                    writer.write("Password: " + encryptedPassword + "\n");
                    writer.close();

                    // Update the scroll pane with new content
                    updateScrollPane(newFile);

                    // Close the popup
                    addPasswordStage.close();
                } catch (Exception ex) {
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

    private void updateScrollPane(File file) {
        try {
            // Read the file content
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String accountName = reader.readLine();
            String usernameLine = reader.readLine(); // Read the full username line
            String encryptedPassword = reader.readLine();
            reader.close();
    
            // Extract the actual username by removing the "Username: " part
            String username = usernameLine.substring(usernameLine.indexOf(": ") + 2);  // Remove "Username: " part
    
            // Decrypt the password
            String decryptedPassword = EncryptionUtil.decryptEntry(
                encryptedPassword.substring(encryptedPassword.indexOf(": ") + 2), 
                currentMasterPassword
            );
    
            // Create a VBox for the account, giving it a border or background to visually separate it
            VBox accountBox = new VBox(10);
            accountBox.setStyle("-fx-border-color: gray; -fx-border-width: 1; -fx-background-color: #f4f4f4; -fx-padding: 10;");
    
            // Add account name
            Label accountLabel = new Label(accountName);
            accountLabel.setFont(new Font(14));
    
            // Create dots for the username and password based on the length of the actual strings
            String usernameDots = new String(new char[username.length()]).replace("\0", "●");
            String passwordDots = new String(new char[decryptedPassword.length()]).replace("\0", "●");
    
            // Add the masked username and password to the VBox
            Label usernameLabel = new Label("Username: " + usernameDots);
            usernameLabel.setFont(new Font(14));
    
            Label passwordLabel = new Label("Password: " + passwordDots);
            passwordLabel.setFont(new Font(14));
    
            // Create a button to reveal the username and password
            Button revealButton = new Button("Show Details");
            revealButton.setOnAction(e -> {
                // Toggle visibility of the actual username and password
                if (usernameLabel.getText().contains(usernameDots)) {
                    usernameLabel.setText("Username: " + username);  // Reveal username
                    passwordLabel.setText("Password: " + decryptedPassword);  // Reveal password
                    revealButton.setText("Hide Details");  // Change button text to "Hide"
                } else {
                    usernameLabel.setText("Username: " + usernameDots);  // Hide username
                    passwordLabel.setText("Password: " + passwordDots);  // Hide password
                    revealButton.setText("Show Details");  // Change button text to "Show"
                }
            });
    
            // Create a button to delete the password
            Button deleteButton = new Button("Delete Password");
            deleteButton.setOnAction(e -> {
                // Confirm deletion with the user
                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                confirmation.setTitle("Confirm Delete Password");
                confirmation.setHeaderText("Delete Password");
                confirmation.setContentText("Are you sure you want to delete this password? This action cannot be undone.");
                
                confirmation.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        // Delete the password file
                        try {
                            boolean deleted = file.delete();  // Delete the password file
                            if (deleted) {
                                // Remove the account block from the UI
                                contentVBox.getChildren().remove(accountBox);
                            } else {
                                showError("Error", "Failed to delete password file.");
                            }
                        } catch (Exception ex) {
                            showError("Error", "Failed to delete password: " + ex.getMessage());
                        }
                    }
                });
            });
    
            // Create a container for the buttons (reveal and delete)
            HBox buttonBox = new HBox(10);
            buttonBox.getChildren().addAll(revealButton, deleteButton);
    
            // Add the account details, buttons, and the container to the VBox
            accountBox.getChildren().addAll(accountLabel, usernameLabel, passwordLabel, buttonBox);
    
            // Add the VBox to the contentVBox (the main VBox that holds all account entries)
            contentVBox.getChildren().add(accountBox);
        } catch (Exception e) {
            showError("Error", "Failed to display the password: " + e.getMessage());
        }
    }
    
    
    /**
     * Loads all existing passwords from the passwords folder and displays them in the content VBox.
     */
    private void loadExistingPasswords() {
        Path passwordsDir = Paths.get(System.getProperty("user.home"), "Downloads/masterPasswords/passwords");

        if (Files.exists(passwordsDir)) {
            try (Stream<Path> paths = Files.list(passwordsDir)) {
                paths.forEach(path -> updateScrollPane(path.toFile()));
            } catch (IOException e) {
                showError("Error", "Failed to load existing passwords: " + e.getMessage());
            }
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