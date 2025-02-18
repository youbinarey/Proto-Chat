module dam.psp.cliente {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.jfoenix;


    opens dam.psp.cliente.controller to javafx.fxml;
    exports dam.psp.cliente;
}