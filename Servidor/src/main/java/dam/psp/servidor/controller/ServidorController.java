package dam.psp.servidor.controller;

import dam.psp.servidor.model.ClienteHandler;
import dam.psp.servidor.model.Servidor;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Controlador para la interfaz gráfica del servidor.
 * Este controlador maneja la interacción con la vista del servidor, incluyendo la actualización
 * de la lista de clientes conectados y la hora en pantalla, así como el inicio del servidor.
 */
public class ServidorController {

    // Instancia del servidor
    private Servidor servidor;

    @FXML
    private ListView<ClienteHandler> listOfClientes; // Lista de clientes conectados

    @FXML
    private Label serverTxt; // Etiqueta para mostrar el estado del servidor

    @FXML
    private TextArea textAreaOfLogs; // Área de texto para mostrar los logs

    @FXML
    private Label timeLbl; // Etiqueta para mostrar la hora

    /**
     * Método de inicialización de la vista. Se ejecuta al cargar la interfaz gráfica.
     * Inicializa el servidor y la lista de clientes conectados, y comienza a actualizar la hora en pantalla.
     */
    @FXML
    public void initialize() {
        actualizaHora();
        servidor = new Servidor(); // Crear una nueva instancia del servidor
        servidor.setControlador(this); // Establecer el controlador en el servidor
        listOfClientes.setItems(servidor.sala.getClientes()); // Vincular la lista de clientes al ListView
        iniciarServidor(); // Iniciar el servidor
    }

    /**
     * Inicia el servidor en un hilo separado para evitar bloquear la interfaz gráfica.
     * Utiliza un {@link Task} para manejar la ejecución en segundo plano.
     */
    private void iniciarServidor() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                servidor.servidorUp(); // Iniciar el servidor
                return null;
            }
        };
        new Thread(task).start(); // Ejecutar la tarea en un nuevo hilo
    }

    /**
     * Muestra un mensaje de log en el área de texto de logs.
     *
     * @param log El mensaje de log que se desea mostrar en la interfaz.
     */
    public void mostrarLog(String log) {
        String textoActual = textAreaOfLogs.getText();
        textAreaOfLogs.setText(textoActual + log + "\n"); // Añadir el log al área de texto
    }

    /**
     * Actualiza la hora en la interfaz gráfica cada segundo.
     * Utiliza un {@link Timeline} para actualizar la hora en un intervalo de 1 segundo.
     */
    public void actualizaHora() {
        // Crear un Timeline para actualizar la hora cada segundo
        Timeline time = new Timeline(new KeyFrame(Duration.seconds(1), e -> {

            LocalTime horaActual = LocalTime.now(); // Obtener la hora actual

            // Formatear la hora
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("HH:mm:ss");
            String horaFormateada = horaActual.format(formato);

            timeLbl.setText(horaFormateada); // Mostrar la hora en la interfaz
        }));

        // Hacer que el Timeline se ejecute indefinidamente
        time.setCycleCount(Timeline.INDEFINITE);
        time.play(); // Iniciar el Timeline
    }
}
