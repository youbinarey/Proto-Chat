package dam.psp.servidor.model;


import dam.psp.cliente.model.Paquete;
import dam.psp.cliente.model.TipoPaquete;
import dam.psp.servidor.config.Config;
import dam.psp.servidor.controller.ServidorController;
import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;

public class Servidor {

    private final int PUERTO = Config.SERVER_PORT;
    public Sala sala;
    private ServerSocket serverSocket;
    private String logs;
    private ServidorController controlador;

    public Servidor() {

        try {
            serverSocket = new ServerSocket(PUERTO);
            logs = "";
            sala = new Sala();
            //clientes = new HashSet<>();
            //clientesObservables = FXCollections.observableArrayList();

        } catch (IOException e) {
            System.err.println("Error al crear el servidor en el puerto " + PUERTO + " " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Servidor servidor = new Servidor();
        servidor.servidorUp();
    }

    public void setControlador(ServidorController controlador) {
        this.controlador = controlador;
    }

    public void servidorUp() {
        addActivity("Servidor Arrancado");
        while (true) {
            Socket clienteSocket = esperarConexion(); // Aceptar conexión
            if (clienteSocket != null) {
                conexionCliente(clienteSocket); // Manejar la conexión del cliente
            }
        }
    }

    private Socket esperarConexion() {
        try {
            return serverSocket.accept();
        } catch (IOException e) {
            System.out.println("Error esperando Conexión " + e.getMessage());
            return null;
        }
    }

    private void conexionCliente(Socket clienteSocket) {
        ClienteHandler clienteHandler = new ClienteHandler(clienteSocket, this);
        //clientes.add(clienteHandler);
        new Thread(clienteHandler).start();
    }

    public void procesarPaquete(Paquete p, ObjectOutputStream out, ObjectInputStream in, Socket clienteSocket, ClienteHandler clienteHandler) {
        switch (p.getTipo()) {
            case CONECTAR -> {
                
                conectarCliente(clienteSocket, in, out, clienteHandler, p);
            }
            case MENSAJE -> {
                mensajeCliente(clienteSocket, in, out, clienteHandler, p);
                
            }
            case DESCONECTAR -> {
                desconectarCliente(clienteSocket, out, in, clienteHandler, p);
                logPaquete(p);
                
            }
            default -> System.out.println("Tipo de Paquete no reconocido");
        }
    }

    

    void desconectarCliente(Socket clienteSocket, ObjectOutputStream out, ObjectInputStream in, ClienteHandler cliente, Paquete p) {
        sala.leaveCliente(cliente);
        broadcastMensaje(p);

        try {
            if (out != null) out.close(); // Cerrar el ObjectOutputStream
            if (in != null) in.close();  // Cerrar el ObjectInputStream
            if (clienteSocket != null && !clienteSocket.isClosed()) {
                clienteSocket.close(); // Cerrar el socket
            }
            System.out.println("Cliente desconectado correctamente.");
        } catch (IOException e) {
            System.err.println("Error al desconectar al cliente: " + e.getMessage());
        }
    }

    private void conectarCliente(Socket socketCliente, ObjectInputStream in, ObjectOutputStream out, ClienteHandler cliente, Paquete p) {
        if (sala.contieneCliente(cliente)) {
            //TODO PRESCINDIR O CAMBIAR COMPOROBACION
            System.out.println("Cliente duplicado:" + p.getRemitente());
        } else {
            cliente.setNickname(p.getRemitente());
            sala.joinCliente(cliente);
            broadcastMensaje(p);
        }

    }

    public void addChat(Paquete p) {
        sala.setChat(sala.getChat() + "\n" + p.getRemitente() + " dice: " + p.getMensajeCliente());
    }

    void mensajeCliente(Socket clienteSocket, ObjectInputStream in, ObjectOutputStream out, ClienteHandler clientehHandler, Paquete p){
        //captura el mensaje y notifica a todos
        System.out.println(leerMensaje(p));
      
        //Procesar mensaje
        broadcastMensaje(p);
    }

    //TODO refactorizar leerMensaje
    private void broadcastMensaje(Paquete p) {
        p.setMensajeCliente(leerMensaje(p));
        p.setListaUsuarios(sala.getClientesNickname());

        logPaquete(p);
        addChat(p);

        System.out.println("handeler envia  -> " + p.getMensajeCliente());

        System.out.println("USUARIOS CONECTADOS");
        System.out.println(sala.getClientesNickname());

        // Difusión
        for (ClienteHandler c : sala.getClientes()) {
            
            c.enviarPaquete(p);
        }

    }

   

    private void addActivity(String log) {
        logs = log;
        showActivity();
        Platform.runLater(() -> controlador.mostrarLog(logs)); // controlador es la instancia de ServidorController
    }

    private void showActivity() {
        System.out.println(logs);
    }

    //public ObservableList<String> getClientesObservable() {
    // return clientesObservables;
    //}

    public void infoPaquete(Paquete p) {
        System.out.println("----------------------");
        System.out.println("Paquete tipo: " + p.getTipo().toString());
        System.out.println("Contenido del mensaje: " + p.getMensajeCliente());
        System.out.println("Hash del objeto: " + p.hashCode());
        System.out.println("----------------------");
    }

    public void logPaquete(Paquete p) {
        addActivity(p.getTipo() + " - " + p.getRemitente() + LocalTime.now());
    }

    public String leerMensaje(Paquete p) {
        switch (p.getTipo()) {
            case CONECTAR -> {
                return p.getRemitente() + " se ha unido";
            }
            case MENSAJE -> {
                return p.getRemitente() + ": " + p.getMensajeCliente();

            }
            case DESCONECTAR -> {
                return p.getRemitente() + " ha abandonado la sala";
            }
            default -> {
                return "OPCION NO RECONOCIDA";
            }
        }
    }
}