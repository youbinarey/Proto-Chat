package dam.psp.cliente.controller;

import com.jfoenix.controls.JFXTextArea;
import dam.psp.cliente.model.Cliente;
import dam.psp.cliente.model.paquete.*;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ClienteController implements PaqueteListener {

    public Button btnAdjuntar;
    private Cliente cliente;
    public TextArea textAreaMensaje;
    public Button btnEnviar;

    public Button btnLogOut;
    public ListView<String> listUsuarios;
    private ObservableList<String> usuariosList;
    public JFXTextArea textAreaChat;

    @FXML
    private Label timeLbl;

    @FXML
    private  Label serverTxt;

    public void initialize() {
            actualizaHora();
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

        if(p.getTipo()== TipoPaquete.NOTIFICACION){
            PaqueteNotificacion pn  = (PaqueteNotificacion) p;
            Platform.runLater(() -> mostrarBanner(pn.getEvento()));
        }

    }

    @Override
    public void updateUsuariosConectados(List<String> listaUsuarios) {
        Platform.runLater(() -> {
            usuariosList.setAll(listaUsuarios);
            //mostrarBanner("Nuevo usuario en la sala");
        });

    }


    @FXML
    void btnLogOutOnClick(ActionEvent event) {
        cliente.desconectar();

        goLoging();
    }

    private void goLoging() {
        try {
            // Cargar la vista del login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dam/psp/cliente/clienteLogIn-view.fxml"));
            Parent root = loader.load();

            // Obtener el escenario actual
            Stage stage = (Stage) btnLogOut.getScene().getWindow();

            // Reemplazar la escena actual con la del login
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Error al cargar la vista de login: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
        cliente.conectar();
        System.out.println();

    }

    @FXML
    void btnEnviarOnClick(ActionEvent event){
        if(textAreaMensaje.getText().isEmpty()){
            cliente.enviarMensaje(textAreaMensaje.getText());
            textAreaMensaje.clear();
        }
    }


    // Método para actualizar la hora cada segundo
    public void actualizaHora() {
        // Crear un Timeline para actualizar la hora cada segundo
        Timeline time = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            // Obtener la hora actual
            LocalTime horaActual = LocalTime.now();

            // Formatear la hora en el formato deseado (HH:mm:ss)
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("HH:mm:ss");
            String horaFormateada = horaActual.format(formato);

            // Mostrar la hora en el Label
            timeLbl.setText("Hora: " + horaFormateada);
        }));

        // Hacer que el Timeline se ejecute indefinidamente
        time.setCycleCount(Timeline.INDEFINITE);

        // Iniciar el Timeline
        time.play();
    }


// BANNER NOTIFICACION
// Método para mostrar el banner cuando la lista de usuarios se actualiza
public  void mostrarBanner(String mensaje) {
    Platform.runLater(() -> {
        Label label = new Label(mensaje);
        label.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-padding: 10px;");

        FadeTransition fade = new FadeTransition(Duration.seconds(5), label);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setOnFinished(event -> label.setVisible(false));

        AnchorPane root = (AnchorPane) serverTxt.getParent();
        if (root == null) return;

        AnchorPane.setTopAnchor(label, 0.0);
        AnchorPane.setLeftAnchor(label, 0.0);
        AnchorPane.setRightAnchor(label, 0.0);

        root.getChildren().add(label);
        fade.play();
    });


    }



}