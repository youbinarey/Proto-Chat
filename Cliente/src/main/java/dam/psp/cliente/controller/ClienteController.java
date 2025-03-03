package dam.psp.cliente.controller;

import com.gluonhq.charm.glisten.control.AutoCompleteTextField;
import com.jfoenix.controls.JFXToggleButton;
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

import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import javafx.scene.image.ImageView;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * Controlador principal de la vista cliente, maneja los eventos y la lógica de la aplicación de chat.
 * Implementa PaqueteListener para procesar los paquetes recibidos del servidor.
 */
public class ClienteController implements PaqueteListener {

    public Button btnAdjuntar;
    public AnchorPane bannerComandos;
    public AutoCompleteTextField<String> auto;
    public JFXToggleButton toggleTheme;
    public ListView <HBox> listViewChat;
    private Cliente cliente;
    public TextArea inputUser;
    public Button btnEnviar;

    public Button btnLogOut;
    public ListView<String> listUsuarios;
    private ObservableList<String> usuariosList;
    private final ObservableList<String> comandos = FXCollections.observableArrayList(
            "/tiempo",
            "/ping",
            "/bye",
            "/usuarios"
    );


    @FXML
    private Label timeLbl;

    @FXML
    private Label serverTxt;

    @FXML
    private Button btnPing;

    @FXML
    private ListView<String> listViewComandos;

    /**
     * Método de inicialización de la vista cliente. Configura las vistas y eventos.
     */
    public void initialize() {
        configurarListViewChat();
        configurarListViewComandos();
        configurarEventosTeclado();
        configurarTextAreaMensaje();
        configurarCierreVentana();
        actualizaHora();
    }

    /**
     * Configura el comportamiento de la lista de chat.
     */
    private void configurarListViewChat() {
        listViewChat.setCellFactory(param -> new ListCell<HBox>() {
            @Override
            protected void updateItem(HBox hbox, boolean empty) {
                super.updateItem(hbox, empty);
                if (empty || hbox == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setGraphic(hbox);
                }
            }
        });
    }
    /**
     * Configura la lista de comandos disponibles en la interfaz.
     */
    private void configurarListViewComandos() {
        listViewComandos.setItems(comandos); // Asignar la lista de comandos
        listViewComandos.setVisible(false); // Ocultar inicialmente

        // Manejar la selección de una opción
        listViewComandos.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String comandoSeleccionado = listViewComandos.getSelectionModel().getSelectedItem();
                if (comandoSeleccionado != null) {

                    eventoComandoSeleccionado(comandoSeleccionado);
                    ocultarMenuComandos(); // Ocultar el menú
                }
            } else if (event.getCode() == KeyCode.ESCAPE) {
                ocultarMenuComandos(); // Ocultar el menú al presionar Escape
            }else{
                ocultarBannerComandos();
            }
        });
    }

    private void eventoComandoSeleccionado(String comando) {
        switch (comando){
            case  "/ping" -> cliente.ping();
            case "/tiempo" -> mostrarBanner2("La temperatura es de " + cliente.getWeather());
            case "/bye" -> {cliente.desconectar(); goLoging();}
            default -> System.out.println("Comando no reconocido");
        }
    }



    /**
     * Configura el comportamiento del TextArea para el mensaje.
     */
    private void configurarTextAreaMensaje() {
        inputUser.textProperty().addListener((observableValue, s, t1) -> {
            detectURL(t1);
            if (t1.endsWith("/")) {
                mostrarMenuComandos();
            } else {
                ocultarMenuComandos();
            }

            ajustarAltura();
        });

        usuariosList = FXCollections.observableArrayList();
        listUsuarios.setItems(usuariosList);
    }

    /**
     * Configura los eventos del teclado para la interacción en la aplicación.
     */
    private void configurarEventosTeclado() {
        inputUser.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.isShiftDown() && event.getCode() == KeyCode.ENTER) {
                inputUser.appendText("\n");
                event.consume();
                return;
            }

            if (event.getCode() == KeyCode.ENTER) {
                enviarMensaje();
                event.consume();
            }

            if (event.getCode() == KeyCode.TAB) {
                btnEnviar.requestFocus();
                event.consume();
            }
        });
    }

    /**
     * Configura el comportamiento del cierre de la ventana.
     */
    private void configurarCierreVentana() {
        Platform.runLater(() -> {
            Stage stage = (Stage) btnLogOut.getScene().getWindow();
            stage.setOnCloseRequest(this::handleWindowClose);
        });
    }

    /**
     * Detecta si un mensaje contiene una URL.
     *
     * @param mensaje El mensaje a evaluar.
     * @return true si el mensaje contiene una URL, false en caso contrario.
     */
    private boolean detectURL(String mensaje) {
        String regex = "(https?://\\S+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(mensaje);
        return matcher.find();
    }

    /**
     * Muestra el menú de comandos cuando se detecta que el usuario está escribiendo un comando.
     */
    private void mostrarMenuComandos() {
        if (listViewComandos != null) {
            listViewComandos.toFront(); // Mover el menú al frente
            listViewComandos.setVisible(true); // Mostrar el menú
            listViewComandos.requestFocus(); // Dar foco al menú para permitir la navegación con el teclado
        }
    }


    /**
     * Oculta el menú de comandos cuando no detecta el input '/'.
     */
    private void ocultarMenuComandos() {
        if (listViewComandos != null) {
            listViewComandos.setVisible(false); // Ocultar el menú
        }

        if (inputUser != null) {
            inputUser.requestFocus(); // Solicitar el foco en el TextArea
        }

    }

    /**
     * Maneja el evento de cierre de la ventana.
     * Desconecta al cliente cuando la ventana se cierra.
     *
     * @param event El evento de cierre de ventana.
     */
    private void handleWindowClose(WindowEvent event) {
        if (cliente != null) {
            cliente.desconectar();
        }
    }

    /**
     * Envía el mensaje escrito en el TextArea.
     */
    private void enviarMensaje() {
        String mensaje = inputUser.getText();
        if(!mensaje.isEmpty()){
            cliente.enviarMensaje(mensaje);
            inputUser.clear();
            ajustarAltura();
        }

    }

    /**
     * Ajusta la altura del TextArea dependiendo del número de líneas.
     */
    private void ajustarAltura() {
        int lineas = inputUser.getText().split("\n").length;
        inputUser.setPrefRowCount(Math.min(lineas, 5));
    }

    /**
     * Muestra el mensaje recibido en el chat.
     *
     * @param mensaje El mensaje recibido.
     * @param usuario El usuario que envió el mensaje.
     */
    private void mostrarMensajeEnChat(String mensaje, String usuario) {
        Platform.runLater(() -> {
            Text textoUsuario = new Text(usuario + ": ");
            textoUsuario.getStyleClass().add("nombre-usuario");

            Text textoMensaje = new Text(mensaje);
            textoMensaje.getStyleClass().add("mensaje-usuario");
            //textoMensaje.setWrappingWidth(400); // Ajusta este valor según el ancho deseado

            HBox hbox = new HBox(textoUsuario, textoMensaje);
            // Espacio entre el nombre de usuario y el mensaje
            //hbox.setMaxWidth(400); // Ajusta el ancho máximo del HBox

            listViewChat.getItems().add(hbox);
            listViewChat.scrollTo(listViewChat.getItems().size() - 1);
        });
    }


    @Override
    public void mensajeRecibido(Paquete p) {

        switch (p.getTipo()){
            case  MENSAJE -> {
                PaqueteMensaje pm = (PaqueteMensaje) p;
                String mensaje = pm.getMensaje();
                System.out.println(mensaje);
                //Si mensaje contiene url aplica formato y mostrarMensaje
                Platform.runLater(()-> {
                    if(detectURL(mensaje)){
                        mostrarMensajeEnChatConURL(mensaje, pm.getRemitente());
                    }else{
                        mostrarMensajeEnChat(mensaje , pm.getRemitente());
                    }

                });
            }
            case NOTIFICACION -> {PaqueteNotificacion pn = (PaqueteNotificacion) p;
            Platform.runLater(() -> mostrarBanner2(pn.getEvento()));}
            case ARCHIVO -> {
                PaqueteArchivo par = (PaqueteArchivo) p;
                Platform.runLater(()-> mostrarArchivoEnChat(par));
            }
            case  PING -> {
                PaquetePing ping = (PaquetePing) p;
                long latencia = System.currentTimeMillis() - ping.getTimestamp();
                mostrarBanner2("Latencia con el chat -> " + latencia +
                        "ms");
            }
            default -> System.err.println("Tipo de paquete no reconocido: " + p.getTipo());
        }

    }
    @Override
    public void updateUsuariosConectados(List<String> listaUsuarios) {
        Platform.runLater(() -> {
            usuariosList.setAll(listaUsuarios);
            //mostrarBanner("Nuevo usuario en la sala");
        });

    }


    /**
     * Muestra un mensaje con una URL en el chat.
     *
     * @param mensaje El mensaje con la URL.
     * @param usuario El remitente del mensaje.
     */
    private void mostrarMensajeEnChatConURL(String mensaje, String usuario) {
        Text textoUsuario = new Text(usuario + ": ");
        textoUsuario.getStyleClass().add("nombre-usuario");

        HBox hbox = new HBox(textoUsuario);
        hbox.getStyleClass().add("mensaje-container");

        String[] palabras = mensaje.split(" ");
        for (String palabra : palabras) {
            if (palabra.matches("(https?://\\S+)")) {
                Hyperlink link = new Hyperlink(palabra);
                link.setOnAction(event -> {
                    try {
                        Desktop.getDesktop().browse(new URI(palabra));
                    } catch (Exception e) {
                        System.out.println("Error al abrir URL: " + e.getMessage());
                    }
                });
                link.getStyleClass().add("mensaje-url"); // CSS para diferenciar enlaces
                hbox.getChildren().add(link);
            } else {
                Text textoMensaje = new Text(palabra + " ");
                textoMensaje.getStyleClass().add("mensaje-usuario");
                hbox.getChildren().add(textoMensaje);
            }
        }

        listViewChat.getItems().add(hbox);
        listViewChat.scrollTo(listViewChat.getItems().size() - 1);

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
            Stage stage = (Stage) this.btnLogOut.getScene().getWindow();

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
    void btnEnviarOnClick(ActionEvent event) {
        if (!inputUser.getText().isEmpty()) {
            cliente.enviarMensaje(inputUser.getText());
            inputUser.clear();
        }
    }

    @FXML
    void btnPingOnClick(ActionEvent event) {
        Platform.runLater(() -> cliente.ping());
    }


    /**
     * Actualiza la hora actual en la interfaz.
     */
    public void actualizaHora() {
        // Crear un Timeline para actualizar la hora cada segundo
        Timeline time = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            // Obtener la hora actual
            LocalTime horaActual = LocalTime.now();

            // Formatear la hora en el formato deseado (HH:mm:ss)
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("HH:mm:ss");
            String horaFormateada = horaActual.format(formato);

            // Mostrar la hora en el Label
            timeLbl.setText(horaFormateada);
        }));

        // Hacer que el Timeline se ejecute indefinidamente
        time.setCycleCount(Timeline.INDEFINITE);

        // Iniciar el Timeline
        time.play();
    }


    // BANNER NOTIFICACION
// Método para mostrar el banner
    public void mostrarBanner(String mensaje) {
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

    public void mostrarBanner2(String mensaje ) {
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

    private void inicializarBannerComandos() {
        // Crear el banner
        bannerComandos.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #ccc; -fx-border-width: 1px;");
        //bannerComandos.setPrefSize(200, 100); // Tamaño del banner

        // Añadir opciones al banner
        double yPos = 10; // Posición vertical inicial
        for (String comando : comandos) {
            Button botonComando = new Button(comando);
            botonComando.setStyle("-fx-background-color: transparent; -fx-text-fill: #333; -fx-font-size: 14px;");
            botonComando.setOnAction(event -> {
                inputUser.setText(comando + " "); // Insertar el comando en el textAreaMensaje
                ocultarBannerComandos(); // Ocultar el banner
            });
            botonComando.setLayoutX(10); // Posición horizontal
            botonComando.setLayoutY(yPos); // Posición vertical
            bannerComandos.getChildren().add(botonComando);
            yPos += 30; // Espacio entre opciones
        }

        // Añadir el banner al layout principal



        // Ocultar el banner inicialmente
        bannerComandos.setVisible(false);
    }

    private void mostrarBannerComandos() {
        if (bannerComandos != null) {

            AnchorPane.setTopAnchor(bannerComandos, inputUser.getLayoutY() - 150); // Ajusta la posición
            AnchorPane.setLeftAnchor(bannerComandos, inputUser.getLayoutX());
            bannerComandos.toFront();

            // Mostrar el banner
            bannerComandos.setVisible(true);
        }
    }

    private void ocultarBannerComandos() {
        if (bannerComandos != null) {
            bannerComandos.setVisible(false);
        }
    }

    public void toggleThemeOnClick(ActionEvent event){

    }

    public void onDesconexionServidor() {
        mostrarBanner2("Se ha perdido la conexión con el servidor. Redirigiendo al login...");

        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(event -> goLoging());
        pause.play();
    }

    @FXML
    void btnAdjuntarOnclick(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.gif"),
                new FileChooser.ExtensionFilter("Documentos", "*.pdf", "*.docx", "*.txt")
        );

        File archivoSeleccionado = fileChooser.showOpenDialog(null); // Abrir el diálogo de selección de archivos

        if (archivoSeleccionado != null) {
            String tipoArchivo = cliente.getTipoArchivo(archivoSeleccionado);
            cliente.archivo(archivoSeleccionado, tipoArchivo); // Enviar el archivo al servidor

           //if(tipoArchivo.endsWith("imagen"))mostrarImagenEnChat(archivoSeleccionado);
           // TODO AMPLIAR
        }
    }




    private void mostrarArchivoEnChat(PaqueteArchivo paqueteArchivo) {
            Platform.runLater(()-> {
                if (paqueteArchivo.getTipoArchivo().equals("imagen")) {
                    enviarImagen(paqueteArchivo);
                } else {
                enviarEnlace(paqueteArchivo);
                }
            });

    }
    private void enviarEnlace(PaqueteArchivo paqueteArchivo) {
        // Crear el enlace para descargar el archivo
        Hyperlink enlaceDescarga = new Hyperlink(paqueteArchivo.getNombre());
        enlaceDescarga.setOnAction(event -> {
            // Guardar el archivo localmente y abrirlo
            File archivo = new File("ruta/local/" + paqueteArchivo.getNombre());
            try (FileOutputStream fileOutputStream = new FileOutputStream(archivo)) {
                fileOutputStream.write(paqueteArchivo.getContenido());
                Desktop.getDesktop().open(archivo);
            } catch (IOException e) {
                System.err.println("Error al abrir el archivo: " + e.getMessage());
            }
        });

        // Estilo del enlace
        enlaceDescarga.setStyle("-fx-text-fill: blue; -fx-underline: true;");

        // Crear el texto del nombre de usuario
        Text textoUsuario = new Text(paqueteArchivo.getUsuario() + ": ");
        textoUsuario.getStyleClass().add("nombre-usuario");

        // Contenedor con el nombre de usuario y el enlace
        HBox hbox = new HBox(textoUsuario, enlaceDescarga);
        hbox.setSpacing(5); // Espacio entre el nombre de usuario y el enlace

        // Agregar el HBox al ListView
        listViewChat.getItems().add(hbox);

        // Desplazarse automáticamente al último mensaje
        listViewChat.scrollTo(listViewChat.getItems().size() - 1);
    }

    private void enviarImagen(PaqueteArchivo paqueteArchivo) {
        // Crear la imagen
        Image image = new Image(new ByteArrayInputStream(paqueteArchivo.getContenido()));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(100); // Ajustar el ancho de la imagen
        imageView.setPreserveRatio(true);

        // Crear el texto del nombre de usuario
        Text textoUsuario = new Text(paqueteArchivo.getUsuario() + ": ");
        textoUsuario.getStyleClass().add("nombre-usuario");

        // Crear el HBox para contener el nombre de usuario y la imagen
        HBox hbox = new HBox(textoUsuario, imageView);
        hbox.setSpacing(5); // Espacio entre el nombre de usuario y la imagen

        // Agregar el HBox al ListView
        listViewChat.getItems().add(hbox);

        // Desplazarse automáticamente al último mensaje
        listViewChat.scrollTo(listViewChat.getItems().size() - 1);
    }
}







