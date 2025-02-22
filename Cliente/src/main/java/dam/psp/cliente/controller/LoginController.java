package dam.psp.cliente.controller;

import com.gluonhq.charm.glisten.control.TextField;
import dam.psp.cliente.model.Cliente;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private Button btnLogIn;

    @FXML
    private TextField txtPass;

    @FXML
    private TextField txtUser;
    private ConexionServidor conexionServidor;
    private Cliente cliente;


    public LoginController(){
        this.conexionServidor = ConexionServidor.getInstance();
    }

    @FXML
    void btnLogInOnClick(ActionEvent event) {
        if(conexionServidor.autenticarUsuario(txtUser.getText(), txtPass.getText())){
            System.out.println("CORRECTOOO");

            cliente = new Cliente(txtUser.getText(),  conexionServidor);
      
            loadClienteView(cliente);


        }else{
            System.out.println("Autenticacion fallida");

        }
    }

    private void loadClienteView(Cliente cliente){
        try{
         FXMLLoader loader = new FXMLLoader(getClass().getResource("/dam/psp/cliente/cliente-view2.fxml"));
            Parent root = loader.load();

            ClienteController clienteController = loader.getController();
            clienteController.setCliente(cliente);

            conexionServidor.setMessageListener(clienteController);

            cliente.conectar();

            Stage stage = (Stage) txtUser.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }






}
