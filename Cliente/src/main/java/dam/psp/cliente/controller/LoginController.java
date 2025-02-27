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
        Paquete p = PaqueteFactory.crearPaquete(TipoPaquete.AUTENTICACION, "Antonio", "abc123");
        if(conexionServidor.autenticar(p)){
            cliente = new Cliente("Antonio", (PaqueteListener) this);
            System.out.println(cliente.getNickname());


            loadClienteView(cliente);

        }else{
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

    @Override
    public void mensajeRecibido(Paquete p) {

    }

    @Override
    public void updateUsuariosConectados(List<String> listaUsuarios) {

    }
}
