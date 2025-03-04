module dam.psp.servidor {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires org.controlsfx.controls;
    requires java.sql;
    requires jbcrypt;
    requires jdk.accessibility;
    requires Paquete;
    requires Network;

    opens dam.psp.servidor to javafx.fxml;
    exports dam.psp.servidor;
    exports dam.psp.servidor.controller;
    opens dam.psp.servidor.controller to javafx.fxml;
}