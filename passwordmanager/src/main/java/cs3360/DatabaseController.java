package cs3360;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class DatabaseController {

    @FXML private TextField websiteNameField;
    @FXML private TextField websiteUrlField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TableView passwordTable;
    @FXML private TableColumn websiteColumn;
    @FXML private TableColumn usernameColumn;

    @FXML
    private void handleSavePassword() {
        String websiteName = websiteNameField.getText();
        String websiteUrl = websiteUrlField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();

        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO passwords (website_name, website_url, username, password) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, websiteName);
                ps.setString(2, websiteUrl);
                ps.setString(3, username);
                ps.setString(4, password);
                ps.executeUpdate();
                clearFields();
                showAlert("Password Saved", "Password has been saved successfully.", Alert.AlertType.INFORMATION);
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Error saving password: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleViewPasswords() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM passwords";
            try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    String websiteName = rs.getString("website_name");
                    String username = rs.getString("username");
                }
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Error retrieving passwords: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void clearFields() {
        websiteNameField.clear();
        websiteUrlField.clear();
        usernameField.clear();
        passwordField.clear();
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
