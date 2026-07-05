module com.example.supermarketbillsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.oracle.database.jdbc;

    requires java.desktop;

    opens com.example.supermarketbillsystem to javafx.fxml;
    exports com.example.supermarketbillsystem;
}