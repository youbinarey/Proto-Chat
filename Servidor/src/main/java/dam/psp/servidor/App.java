package dam.psp.servidor;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class App extends Application {

    public static void main(String[] args) {
        launch(App.class, args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("servidor-view2.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        // Configurar el evento para cuando la ventana se cierre
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                // Aquí puedes poner cualquier lógica antes de salir
                System.out.println("Cerrando la aplicación...");
                // Cerrar la aplicación completamente
                Platform.exit();
                // Evitar que se sigan ejecutando hilos u otros procesos en segundo plano
                System.exit(0);  // Se asegura de que el proceso termine completamente
            }
        });

        stage.setTitle("Servidor");
        stage.setScene(scene);
        stage.show();

    }


}