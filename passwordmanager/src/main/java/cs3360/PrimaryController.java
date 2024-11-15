package cs3360;

import java.io.IOException;
import javafx.fxml.FXML;

public class PrimaryController {

    // Function to switch to the secondary view
    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }

    // Function to switch to the login view
    @FXML
    private void switchToLogin() throws IOException {
        App.setRoot("login");
    }
}
