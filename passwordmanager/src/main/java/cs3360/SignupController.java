package cs3360;

import java.io.IOException;

import javafx.fxml.FXML;

public class SignupController {

    @FXML
    private void switchToWelcome() throws IOException {
        App.setRoot("welcome");
    }
    // Function to switch to the login view
    @FXML
    private void switchToLogin() throws IOException {
        App.setRoot("login");
    }
}