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


    public LoginController() {
        this.conexionServidor = ConexionServidor.getInstance();
    }

    @FXML
    void btnLogInOnClick(ActionEvent event) {


        String usuario = txtUser.getText();
        String password = txtPass.getText();


        if (!usuario.isEmpty() && !password.isEmpty()) {
            btnLogIn.setVisible(false);
            Paquete p = PaqueteFactory.crearPaquete(TipoPaquete.AUTENTICACION, usuario, password);
            verifyLogin(p);
        } else {

            isFieldEmpty();
            lblvalidation.setText("Completa todos los campos");
            lblvalidation.setStyle("-fx-text-fill: yellow;");
        }
    }

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
                        setLblvalidationErrorConexion();
                    }else if(autenticado){

                        setLblvalidation(true);
                        cliente = new Cliente(((PaqueteAutenticacion) p).getUsuario(), (PaqueteListener) this);
                        System.out.println(cliente.getNickname());

                        PauseTransition pause = new PauseTransition(Duration.seconds(1));
                        pause.setOnFinished(event -> loadClienteView(cliente));
                        pause.play();
                    } else {
                        btnLogIn.setVisible(true);
                        setLblvalidation(false);
                    }
                });

            } catch (Exception e) {

                Platform.runLater(() -> {
                    root.getChildren().remove(loadingPane);  // Eliminar indicador
                    btnLogIn.setVisible(true);
                    setLblvalidationErrorConexion(); // Mostrar mensaje de error de conexión
                });
            }
        }).start();
    }





    private void setLblvalidationErrorConexion() {
        String mensaje = "Error de conexión al servidor";
        String lblColor = "-fx-text-fill: red;";
        this.lblvalidation.setText(mensaje);
        this.lblvalidation.setStyle(lblColor);
    }
    // Iniciar animacion y comportamiento
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



    // Posicionamiento dentro del contenedor
    private void setLoadingIndicatorPosition(StackPane loadingPane, PasswordField passwordField, AnchorPane root) {
        double x = passwordField.getLayoutX();
        double y = passwordField.getLayoutY();
        loadingPane.setLayoutX(x);
        loadingPane.setLayoutY(y);

        // Agregar el indicador al root
        root.getChildren().add(loadingPane);

    }

    // Crear el indicador de progreso
    private StackPane createLoadingIndicator() {
        StackPane loadingPane = new StackPane();
        //loadingPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5); -fx-background-radius: 12;");

        ProgressIndicator loadingIndicator = new ProgressIndicator();
        loadingIndicator.setPrefSize(50, 50);
        loadingIndicator.setStyle("-fx-progress-color: #1DB954; -fx-background-color: transparent;");

        loadingPane.getChildren().add(loadingIndicator);
        return loadingPane;
    }







    private void loadClienteView(Cliente cliente){
        try{
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

    // Da informacion al usuairo de que la validacion ha sido erronea
    public void setLblvalidation (Boolean login ){
        String mensaje;
        String lblColor;
        if(login){
            mensaje = ("Login con éxito");
            lblColor = ("-fx-text-fill: white;");

        }else{
            mensaje = ("Datos incorrectos");
            lblColor = ("-fx-text-fill: red;");
            txtPass.clear();
            txtPass.setStyle("-fx-border-color: transparent;");
        }
        this.lblvalidation.setText(mensaje);
        this.lblvalidation.setStyle(lblColor);

    }

    //Comprobar campos vacios
    private void isFieldEmpty() {
        boolean userEmpty = txtUser.getText().isEmpty();
        boolean passEmpty = txtPass.getText().isEmpty();

        txtUser.setStyle(userEmpty ? "-fx-border-color: yellow;" : "-fx-border-color: transparent;");
        txtPass.setStyle(passEmpty ? "-fx-border-color: yellow;" : "-fx-border-color: transparent;");
    }

    @Override
    public void mensajeRecibido(Paquete p) {

    }

    @Override
    public void updateUsuariosConectados(List<String> listaUsuarios) {

    }
}
