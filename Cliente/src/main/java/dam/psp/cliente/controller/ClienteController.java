package dam.psp.cliente.controller;

import com.jfoenix.controls.JFXTextArea;
import dam.psp.cliente.model.Cliente;
import dam.psp.cliente.model.paquete.Paquete;
import dam.psp.cliente.model.paquete.PaqueteMensaje;
import dam.psp.cliente.model.paquete.TipoPaquete;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.List;

public class ClienteController implements PaqueteListener {

    private Cliente cliente;
    public TextArea textAreaMensaje;
    public Button btnEnviar;
    public Button btnLogIn;
    public Button btnLogOut;
    public ListView<String> listUsuarios;
    private ObservableList<String> usuariosList;
    public JFXTextArea textAreaChat;

    @FXML
    public void initialize() {
        cliente = new Cliente("Yeray", this);

        usuariosList = FXCollections.observableArrayList();
        listUsuarios.setItems(usuariosList);

        textAreaMensaje.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (event.isControlDown()) {
                    enviarMensaje();
                } else {
                    textAreaMensaje.appendText("\n");
                    event.consume();
                }
            }
        });

        textAreaMensaje.textProperty().addListener((obs, oldText, newText) -> {
            ajustarAltura();
        });
    }

    private void enviarMensaje() {
        String mensaje = textAreaMensaje.getText();
        cliente.enviarMensaje(mensaje);
        textAreaMensaje.clear();
        ajustarAltura();
    }

    private void ajustarAltura() {
        int lineas = textAreaMensaje.getText().split("\n").length;
        textAreaMensaje.setPrefRowCount(Math.min(lineas, 5));
    }

    @Override
    public void mensajeRecibido(Paquete p) {
        if (p.getTipo() == TipoPaquete.MENSAJE) {
            PaqueteMensaje pm = (PaqueteMensaje) p;
            Platform.runLater(() -> textAreaChat.appendText(pm.getMensaje() + "\n"));
        }
    }

    @Override
    public void updateUsuariosConectados(List<String> listaUsuarios) {
        Platform.runLater(() -> usuariosList.setAll(listaUsuarios));
    }

    @FXML
    void btnLogOutOnClick(ActionEvent event) {
        cliente.desconectar();
    }

    @FXML
    void btnLogInOnClick(ActionEvent event) {
        cliente.autenticar("Antonio", "abc123");
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    @FXML
    void btnEnviarOnClick(ActionEvent event){
            cliente.enviarMensaje(textAreaMensaje.getText());
            textAreaMensaje.clear();
    }
}