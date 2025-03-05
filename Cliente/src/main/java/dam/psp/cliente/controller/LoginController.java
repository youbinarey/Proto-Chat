package dam.psp.cliente.controller;

import dam.psp.cliente.model.Cliente;
import dam.psp.cliente.model.paquete.Paquete;
import dam.psp.cliente.model.paquete.PaqueteAutenticacion;
import dam.psp.cliente.model.paquete.PaqueteFactory;
import dam.psp.cliente.model.paquete.TipoPaquete;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

/**
 * Controlador para el proceso de inicio de sesión del cliente.
 *
 * <p>Este controlador maneja los eventos relacionados con el inicio de sesión, la autenticación en el servidor y la
 * transición hacia la vista del cliente.</p>
 *
 * @see PaqueteListener
 */
public class LoginController implements PaqueteListener {

    @FXML
    private Button btnLogIn;

    @FXML
    private PasswordField txtPass;

    @FXML
    private TextField txtUser;

    @FXML
    private Label lblvalidation;

    private ConexionServidor conexionServidor;
    private Cliente cliente;

    /**
     * Constructor del controlador que inicializa la conexión con el servidor.
     */
    public LoginController() {
        this.conexionServidor = ConexionServidor.getInstance();
    }

    /**
     * Evento que se activa cuando el usuario hace clic en el botón de inicio de sesión.
     * Verifica si los campos de usuario y contraseña están completos y, si es así, intenta autenticar al usuario.
     *
     * @param event Evento generado por el clic del usuario en el botón de inicio de sesión.
     */
    @FXML
    void btnLogInOnClick(ActionEvent event) {
        editable(false);

        lblvalidation.setText("");
        String usuario = txtUser.getText();
        String password = txtPass.getText();

        if (!usuario.isEmpty() && !password.isEmpty()) {
            btnLogIn.setVisible(false);
            Paquete p = PaqueteFactory.crearPaquete(TipoPaquete.AUTENTICACION, usuario, password);
            verifyLogin(p);
        }else{
            isFieldEmpty();
        }

    }

    private void editable(boolean editable) {
        txtUser.setEditable(editable);
        ;
        txtPass.setEditable(editable);
    }

    /**
     * Verifica la autenticación del usuario con el servidor en un hilo separado.
     * Muestra un indicador de carga mientras se realiza la autenticación.
     *
     * @param p Paquete que contiene las credenciales del usuario.
     */
    private void verifyLogin(Paquete p) {
        btnLogIn.setVisible(false);
        StackPane loadingPane = createLoadingIndicator();

        // Referenciar el contenedor donde lo queremos posicionar
        AnchorPane root = (AnchorPane) txtPass.getParent();
        if (root == null) return;

        setLoadingIndicatorPosition(loadingPane, txtPass, root);
        addLoadingIndicatorAnimation(loadingPane);

        // Ejecutar autenticación en un hilo separado
        new Thread(() -> {
            try {
                // Intentar autenticar con el servidor
                Boolean autenticado = conexionServidor.autenticar(p);


                Platform.runLater(() -> {
                    root.getChildren().remove(loadingPane);  // Eliminar indicador



                    if (autenticado == null) {
                        btnLogIn.setVisible(true);
                        System.out.println("Ya hay un usuario en el chat");
                        setLblvalidationUsuarioActivo();

                    } else if (autenticado) {
                        setLblvalidation(true);
                        cliente = new Cliente(((PaqueteAutenticacion) p).getUsuario(), (PaqueteListener) this);
                        System.out.println(cliente.getNickname());

                        PauseTransition pause = new PauseTransition(Duration.seconds(1));
                        pause.setOnFinished(event -> loadClienteView(cliente));
                        pause.play();
                    } else {
                        setLblvalidationErrorConexion();
                        btnLogIn.setVisible(true);
                        setLblvalidation(false);
                    }

                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    root.getChildren().remove(loadingPane);  // Eliminar indicador
                    btnLogIn.setVisible(true);
                    setLblvalidationErrorConexion();
                });
            }

        }).start();

    }

    /**
     * Establece el texto y el color de la etiqueta de validación cuando ocurre un error de conexión.
     */
    private void setLblvalidationErrorConexion() {
        String mensaje = "Error de conexión al servidor";
        String lblColor = "-fx-text-fill: red;";
        this.lblvalidation.setText(mensaje);
        this.lblvalidation.setStyle(lblColor);


    }
    private void setLblvalidationUsuarioActivo() {
        System.out.println("lega aqui??");

        String mensaje1 = "Ya hay un usuario con este nickname en la sala";
        String mensaje2 = " Este equipo ya tiene una sesión iniciad";
        String mensaje = conexionServidor.isClienteConectado() ? mensaje2 : mensaje1;
        this.lblvalidation.setText(mensaje);
        editable(true);

    }

    /**
     * Inicia la animación del indicador de carga.
     * Realiza una animación de rotación y escala en el indicador de carga.
     *
     * @param loadingPane El contenedor del indicador de carga.
     */
    private void addLoadingIndicatorAnimation(StackPane loadingPane) {
        ProgressIndicator loadingIndicator = (ProgressIndicator) loadingPane.getChildren().get(0);

        // Animación de rotación
        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(1), loadingIndicator);
        rotateTransition.setByAngle(360);
        rotateTransition.setCycleCount(Animation.INDEFINITE);
        rotateTransition.setInterpolator(Interpolator.LINEAR);
        rotateTransition.play();

        // Animación de escala
        ScaleTransition scale = new ScaleTransition(Duration.seconds(0.5), loadingPane);
        scale.setFromX(0.7);
        scale.setFromY(0.7);
        scale.setToX(1);
        scale.setToY(1);
        scale.setInterpolator(Interpolator.EASE_OUT);
        scale.play();
    }

    /**
     * Establece la posición del indicador de carga dentro del contenedor de la interfaz de usuario.
     *
     * @param loadingPane El contenedor del indicador de carga.
     * @param passwordField El campo de contraseña donde se posicionará el indicador.
     * @param root El contenedor raíz donde se colocará el indicador de carga.
     */
    private void setLoadingIndicatorPosition(StackPane loadingPane, PasswordField passwordField, AnchorPane root) {
        double x = btnLogIn.getLayoutX();
        double y = btnLogIn.getLayoutY();
        loadingPane.setLayoutX(x);
        loadingPane.setLayoutY(y);

        // Agregar el indicador al root
        root.getChildren().add(loadingPane);
    }

    /**
     * Crea el indicador de progreso para mostrar mientras se espera la autenticación.
     *
     * @return El contenedor del indicador de progreso.
     */
    private StackPane createLoadingIndicator() {
        StackPane loadingPane = new StackPane();

        ProgressIndicator loadingIndicator = new ProgressIndicator();
        loadingIndicator.setPrefSize(50, 50);
        loadingIndicator.setStyle("-fx-progress-color: #1DB954; -fx-background-color: transparent;");

        loadingPane.getChildren().add(loadingIndicator);
        return loadingPane;
    }

    /**
     * Carga la vista del cliente después de un inicio de sesión exitoso.
     *
     * @param cliente El objeto cliente que contiene la información del usuario autenticado.
     */
    private void loadClienteView(Cliente cliente) {
        try {
            String fxmlPath = "/dam/psp/cliente/cliente-view2.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            ClienteController clienteController = loader.getController();
            clienteController.setCliente(cliente);

            conexionServidor.setMessageListener(clienteController);

            Stage stage = (Stage) txtUser.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            System.err.println("Error en loadCliente: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Actualiza el texto de validación según el resultado del intento de inicio de sesión.
     *
     * @param login {@code true} si el inicio de sesión fue exitoso, {@code false} en caso contrario.
     */
    public void setLblvalidation(Boolean login) {
        String mensaje;
        String lblColor;
        if (login) {
            mensaje = "Login con éxito";
            lblColor = "-fx-text-fill: white;";
        } else {
            mensaje = "Datos incorrectos";
            lblColor = "-fx-text-fill: red;";
            txtPass.clear();
            txtPass.setStyle("-fx-border-color: transparent;");
        }
        this.lblvalidation.setText(mensaje);
        this.lblvalidation.setStyle(lblColor);
        editable(true);


    }

    /**
     * Comprueba si los campos de usuario y contraseña están vacíos y aplica un estilo visual.
     */
    private void isFieldEmpty() {
        boolean userEmpty = txtUser.getText().isEmpty();
        boolean passEmpty = txtPass.getText().isEmpty();

        txtUser.setStyle(userEmpty ? "-fx-border-color: yellow;" : "-fx-border-color: transparent;");
        txtPass.setStyle(passEmpty ? "-fx-border-color: yellow;" : "-fx-border-color: transparent;");
        lblvalidation.setText("Completa todos los campos");
        lblvalidation.setStyle("-fx-text-fill: yellow;");
        editable(true);


    }

    @Override
    public void mensajeRecibido(Paquete p) {
        // Implementar lógica de mensaje recibido si es necesario.
    }

    @Override
    public void updateUsuariosConectados(List<String> listaUsuarios) {
        // Implementar lógica de actualización de usuarios conectados si es necesario.
    }
}
