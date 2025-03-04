package dam.psp.cliente;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        for(int i = 1;i <= 1; i++){
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
        stage.setTitle("Cliente" );
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }
}