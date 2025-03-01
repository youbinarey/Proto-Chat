module dam.psp.cliente {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.jfoenix;
    requires com.gluonhq.charm.glisten;
    requires java.desktop;


    opens dam.psp.cliente.controller to javafx.fxml;
    exports dam.psp.cliente;
}