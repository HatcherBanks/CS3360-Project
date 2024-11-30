package cs3360;

import java.io.IOException;

import javafx.fxml.FXML;
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
           App.setRoot("primary"); // Ensure "welcome.fxml" matches this name
        } catch (IOException e) {
            System.err.println("Error" + e.getMessage());
            e.printStackTrace();
        }
    }
}
