package dam.psp.servidor;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.util.Duration;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ServidorController {

    @FXML
    private Label serverTxt;

    @FXML
    private Label timeLbl;

    @FXML
    private TextArea logTextArea;

    @FXML
    public void initialize() {
        actualizaHora();
    }

    public void mostrarLog(String log) {
        String textoActual = logTextArea.getText();
        logTextArea.setText(textoActual + log + "\n");
    }

    // Método para actualizar la hora cada segundo
    public void actualizaHora() {
        // Crear un Timeline para actualizar la hora cada segundo
        Timeline time = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            // Obtener la hora actual
            LocalTime horaActual = LocalTime.now();

            // Formatear la hora en el formato deseado (HH:mm:ss)
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("HH:mm:ss");
            String horaFormateada = horaActual.format(formato);

            // Mostrar la hora en el Label
            timeLbl.setText("Hora: " + horaFormateada);
        }));

        // Hacer que el Timeline se ejecute indefinidamente
        time.setCycleCount(Timeline.INDEFINITE);

        // Iniciar el Timeline
        time.play();
    }
}
