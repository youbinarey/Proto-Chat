module dam.psp.cliente {
    requires javafx.controls;
    requires javafx.fxml;


    opens dam.psp.cliente to javafx.fxml;
    exports dam.psp.cliente;
}