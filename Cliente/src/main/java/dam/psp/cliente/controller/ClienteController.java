package dam.psp.cliente.controller;

import com.jfoenix.controls.JFXTextArea;
import dam.psp.cliente.model.Cliente;
import dam.psp.cliente.model.paquete.*;
import javafx.animation.*;
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
import javafx.stage.WindowEvent;
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

    @FXML
    private Button btnPing;

    public void initialize() {
            actualizaHora();

        Platform.runLater(() -> {
            Stage stage = (Stage) btnLogOut.getScene().getWindow();
            stage.setOnCloseRequest(this::handleWindowClose);
        });
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

    private void handleWindowClose(WindowEvent event) {
        if (cliente != null) {
            cliente.desconectar();
        }
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
            Platform.runLater(() -> mostrarBanner2(pn.getEvento()));
        }

        if (p.getTipo() == TipoPaquete.PING) {
            PaquetePing ping = (PaquetePing) p;
            long latencia = System.currentTimeMillis() - ping.getTimestamp();
            mostrarBanner2("Latencia con el chat -> " + latencia +
                    "ms");
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
        if(!textAreaMensaje.getText().isEmpty()){
            cliente.enviarMensaje(textAreaMensaje.getText());
            textAreaMensaje.clear();
        }
    }

    @FXML
    void btnPingOnClick(ActionEvent event) {
        Platform.runLater(()-> cliente.ping());
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
// Método para mostrar el banner
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

    public void mostrarBanner2(String mensaje) {
        Platform.runLater(() -> {
            Label label = new Label(mensaje);
            label.setStyle("""
                        -fx-background-color: rgba(54, 57, 63, 0.9);
                        -fx-text-fill: white;
                        -fx-font-size: 14px;
                        -fx-font-weight: bold;
                        -fx-padding: 15px;
                        -fx-border-radius: 8px;
                        -fx-background-radius: 8px;
                        -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0, 0, 4);
                    """);

            AnchorPane root = (AnchorPane) serverTxt.getParent();
            if (root == null) return;

            // Inicializar en posición superior fuera de la pantalla
            label.setTranslateY(-50);
            AnchorPane.setTopAnchor(label, 10.0);
            AnchorPane.setLeftAnchor(label, 20.0);
            AnchorPane.setRightAnchor(label, 20.0);

            root.getChildren().add(label);

            // Animación de entrada: desplazar hacia abajo y aparecer
            TranslateTransition slideIn = new TranslateTransition(Duration.millis(500), label);
            slideIn.setFromY(-50);
            slideIn.setToY(0);
            slideIn.setInterpolator(Interpolator.EASE_OUT);

            FadeTransition fadeOut = new FadeTransition(Duration.seconds(1.5), label);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setDelay(Duration.seconds(3)); // Se mantiene visible por 3 segundos
            fadeOut.setOnFinished(event -> root.getChildren().remove(label));

            // Animación de salida: Desplazar hacia arriba y desvanecer
            TranslateTransition slideOut = new TranslateTransition(Duration.millis(500), label);
            slideOut.setFromY(0);
            slideOut.setToY(-30);
            slideOut.setInterpolator(Interpolator.EASE_IN);
            slideOut.setDelay(Duration.seconds(3));

            // Ejecutar animaciones
            slideIn.setOnFinished(e -> {
                fadeOut.play();
                slideOut.play();
            });
            slideIn.play();
        });


    }
    }