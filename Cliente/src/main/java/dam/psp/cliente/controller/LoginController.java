package dam.psp.cliente.controller;

import dam.psp.cliente.model.Cliente;
import dam.psp.cliente.model.paquete.Paquete;
import dam.psp.cliente.model.paquete.PaqueteAutenticacion;
import dam.psp.cliente.model.paquete.PaqueteFactory;
import dam.psp.cliente.model.paquete.TipoPaquete;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class LoginController implements PaqueteListener{

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


    public LoginController(){
        this.conexionServidor = ConexionServidor.getInstance();
    }

    @FXML
    void btnLogInOnClick(ActionEvent event) {


        String usuario = txtUser.getText();
        String password = txtPass.getText();


        if(!usuario.isEmpty() && !password.isEmpty()){
            Paquete p = PaqueteFactory.crearPaquete(TipoPaquete.AUTENTICACION, usuario, password);
            verifyLogin(p);
        }else {
            isFieldEmpty();
            lblvalidation.setText("Completa todos los campos");
            lblvalidation.setStyle("-fx-text-fill: yellow;");
        }
    }

    private void verifyLogin(Paquete p) {
        if(conexionServidor.autenticar(p)){
            PaqueteAutenticacion pa = (PaqueteAutenticacion) p;

            setLblvalidation(true);

            cliente = new Cliente(pa.getUsuario(), (PaqueteListener) this);
            System.out.println(cliente.getNickname());
            //TODO animacion

            // Ejecutar la pausa en un hilo separado
            new Thread(() -> {
                try {
                    Thread.sleep(1000L); // Espera 1 segundo
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Volver al hilo de JavaFX para cargar la nueva vista
                Platform.runLater(() -> loadClienteView(cliente));
            }).start();


        }else{
            setLblvalidation(false);

            System.out.println("Autenticacion fallida");

        }
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
