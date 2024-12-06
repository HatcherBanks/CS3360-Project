module cs3360 {
    requires javafx.controls;
    requires javafx.fxml;

    opens cs3360 to javafx.fxml;
    exports cs3360;
}
