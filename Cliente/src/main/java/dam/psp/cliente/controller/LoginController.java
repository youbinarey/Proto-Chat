package dam.psp.cliente.controller;

import com.gluonhq.charm.glisten.control.TextField;
import dam.psp.cliente.model.Cliente;
import dam.psp.cliente.model.paquete.Paquete;
import dam.psp.cliente.model.paquete.PaqueteFactory;
import dam.psp.cliente.model.paquete.TipoPaquete;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class LoginController implements PaqueteListener{

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
        String usuario = txtUser.getText();
        String password = txtPass.getText();
        Paquete p = PaqueteFactory.crearPaquete(TipoPaquete.AUTENTICACION, usuario, password);
        if(conexionServidor.autenticar(p)){
            cliente = new Cliente(usuario, (PaqueteListener) this);

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


            Stage stage = (Stage) txtUser.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void mensajeRecibido(Paquete p) {

    }

    @Override
    public void updateUsuariosConectados(List<String> listaUsuarios) {

    }
}
