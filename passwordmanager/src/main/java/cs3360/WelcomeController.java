package cs3360;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

public class WelcomeController {

    @FXML
    private Button secondaryButton;

    @FXML
    public void initialize() {
        // Initialization logic can go here
        System.out.println("LoginController initialized!");
    }

    @FXML
    private void switchToPrimary() {
        try {
           App.setRoot("welcome"); // Ensure "welcome.fxml" matches this name
        } catch (IOException e) {
            System.err.println("Error" + e.getMessage());
            e.printStackTrace();
        }
    }


    @FXML
    private void handleButton1Action() {
        showAlert("Button 1", "You clicked Button 1.");
    }

    @FXML
    private void handleButton2Action() {
        showAlert("Button 2", "You clicked Button 2.");
    }

    @FXML
    private void handleButton3Action() {
        showAlert("Button 3", "You clicked Button 3.");
    }

    @FXML
    private void handleBottomRightButtonAction() {
        showAlert("Bottom Right Button", "You clicked the Bottom Right Button.");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
