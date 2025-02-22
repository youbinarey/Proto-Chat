package dam.psp.cliente.controller;

import com.jfoenix.controls.JFXTextArea;
import dam.psp.cliente.model.Cliente;
import dam.psp.cliente.model.Paquetes;
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

       // cliente = new Cliente("yeray",this,ConexionServidor.getInstance());
        //cliente.conectar();
        usuariosList = FXCollections.observableArrayList();
        listUsuarios.setItems(usuariosList);

        // Manejar eventos del teclado
        textAreaMensaje.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (event.isControlDown()) {
                    enviarMensaje();
                } else {
                    // Permitir salto de línea
                    textAreaMensaje.appendText("\n");
                    event.consume(); // Evita que se cierre el `TextArea`
                }
            }
        });

        // Ajustar la altura cuando el texto cambia
        textAreaMensaje.textProperty().addListener((obs, oldText, newText) -> {
            ajustarAltura();
        });

    }

    private void enviarMensaje() {
        System.out.println("Mensaje enviado: " + textAreaMensaje.getText());
        textAreaMensaje.clear();
        ajustarAltura();
    }

    private void ajustarAltura() {
        int lineas = textAreaMensaje.getText().split("\n").length;
        textAreaMensaje.setPrefRowCount(Math.min(lineas, 5)); // Máximo 5 líneas antes del scroll
    }



    // Evento para capturar la tecla Enter y enviar el mensaje
    @FXML
    private void handleKeyPress(KeyEvent event) {
        if (event.getCode().toString().equals("ENTER")) {
            String mensaje = textAreaMensaje.getText();
            enviarMensaje(mensaje); // Llamada para enviar el mensaje
            textAreaMensaje.clear();  // Limpiar el área de texto después de enviar
        }
    }
    private void enviarMensaje(String mensaje) {
        // Implementa el envío del mensaje
        System.out.println("Mensaje enviado: " + mensaje);
    }

    @FXML
    void btnEnviarOnClick(ActionEvent event) {
        String mensaje = textAreaMensaje.getText();
        enviarMensaje();
        cliente.enviarMensaje(mensaje);
    }

    @Override
    public void mensajeRecibido(Paquetes p) {
        Platform.runLater(() -> {
            textAreaChat.appendText(p.getMensajeCliente() + "\n");
        });
    }

    @Override
    public void updateUsuariosConectados(List<String> listaUsuarios) {
        Platform.runLater(()->{
            usuariosList.setAll(listaUsuarios);
        });
    }


    @FXML
    void btnLogOutOnClick(ActionEvent event) {
            cliente.desconectar();
    }
    @FXML
    void btnLogInOnClick(ActionEvent event) {
        cliente.conectar();
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
}
