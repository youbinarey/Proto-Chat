package dam.psp.cliente;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        for(int i = 0;i < 11; i++){
            openNewWindow(i);
        }
    }

    public static void main(String[] args) {
        launch();


    }



    private void openNewWindow(int index) throws IOException {
        // Cargar la misma FXML, pero puedes cambiar cualquier cosa si quieres personalizar las ventanas
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("clienteLogIn-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setTitle("Cliente " + index);
        stage.setScene(scene);
        stage.show();
    }
}