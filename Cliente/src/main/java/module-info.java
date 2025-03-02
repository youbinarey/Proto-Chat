module dam.psp.cliente {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.jfoenix;
    requires com.gluonhq.charm.glisten;
    requires java.desktop;
    requires com.google.gson;
    requires org.json;


    opens dam.psp.cliente.controller to javafx.fxml;
    exports dam.psp.cliente;
}