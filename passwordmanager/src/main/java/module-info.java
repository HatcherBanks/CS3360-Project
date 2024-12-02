module cs3360 {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive java.sql;
    requires mysql.connector.java;

    opens cs3360 to javafx.fxml;
    exports cs3360;
}
