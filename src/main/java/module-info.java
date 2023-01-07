module com.eitan07.snakebutfr {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.eitan07.snakebutfr to javafx.fxml;
    exports com.eitan07.snakebutfr;
}