module dam.psp.servidor {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    //requires eu.hansolo.tilesfx;
    opens dam.psp.servidor to javafx.fxml;
    exports dam.psp.servidor;
    exports dam.psp.servidor.controller;
    opens dam.psp.servidor.controller to javafx.fxml;
}