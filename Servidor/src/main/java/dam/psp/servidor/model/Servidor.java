package dam.psp.servidor.model;


import dam.psp.cliente.model.Paquete;
import dam.psp.cliente.model.TipoPaquete;
import dam.psp.servidor.config.Config;
import dam.psp.servidor.controller.ServidorController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

public class Servidor {

    private ServerSocket serverSocket;
    private final int PUERTO = Config.SERVER_PORT;
    private String logs;
    public Sala sala;
    //private Set<ClienteHandler> clientes;
    private ServidorController controlador;

    public void setControlador(ServidorController controlador) {
        this.controlador = controlador;
    }


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
                //p.setListaUsuarios(sala.getClientesNickname());
                    conectarCliente(clienteSocket,in,out,clienteHandler,p);
            }
            case MENSAJE -> {
                //capturar quien lo envia y el mensaje
                System.out.println(leerMensaje(p));
                addChat(p);
                logPaquete(p);

                broadcastMensaje(p);
            }
            case DESCONECTAR -> {
                desconectarCliente(clienteSocket, out, in, clienteHandler);
                logPaquete(p);
                broadcastMensaje(p);
            }
            default -> System.out.println("Tipo de Paquete no reconocido");
        }
    }

    void desconectarCliente(Socket clienteSocket, ObjectOutputStream out, ObjectInputStream in, ClienteHandler cliente) {

        sala.leaveCliente(cliente);
        //p.setListaUsuarios(sala.getClientesNickname());
        // Eliminar al cliente de la sala

       // Platform.runLater(()-> {
        //clientesObservables.remove(cliente.getNickname());
            //Actualiza la UI
        //});

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

    private void conectarCliente(Socket socketCliente,  ObjectInputStream in, ObjectOutputStream out, ClienteHandler cliente, Paquete p){
        if (sala.contieneCliente(cliente)) {
            System.out.println("Cliente duplicado:" + p.getRemitente());
        } else {
            cliente.setNickname(p.getRemitente());

            sala.joinCliente(cliente);

            logPaquete(p);
            //Platform.runLater(()->{
              //  clientesObservables.add(cliente.getNickname());

            //});

            broadcastMensaje(p);

        }

    }
    public void addChat(Paquete p) {
        sala.setChat(sala.getChat() + "\n" + p.getRemitente() + " dice: " + p.getMensajeCliente());
    }

    private void broadcastMensaje(Paquete pRecibido) {


        Paquete pEnviar = new Paquete();
        pEnviar.setTipo(TipoPaquete.MENSAJE);
        pEnviar.setRemitente(pRecibido.getRemitente());
        pEnviar.setMensajeCliente((leerMensaje(pRecibido)));
        pEnviar.setDestinatario("TODOS");
        pEnviar.setListaUsuarios(sala.getClientesNickname());
        System.out.println("handeler envia  -> " + pEnviar.getMensajeCliente());

        System.out.println("USUARIOS CONECTADOS");
        System.out.println(sala.getClientesNickname());

        sala.broadcastMensaje(pEnviar);
       // TODO
        /*
        for (ClienteHandler cliente : clientes) {
            cliente.enviarPaquete(pEnviar);
        }

         */
    }

    private void addActivity(String log) {
        logs= log;
        showActivity();
        Platform.runLater(() -> controlador.mostrarLog(logs)); // controlador es la instancia de ServidorController
    }

    private void showActivity() {
        System.out.println(logs);
    }

    public void infoPaquete(Paquete p) {
        System.out.println("----------------------");
        System.out.println("Paquete tipo: " + p.getTipo().toString());
        System.out.println("Contenido del mensaje: " + p.getMensajeCliente());
        System.out.println("Hash del objeto: " + p.hashCode());
        System.out.println("----------------------");
    }

    //public ObservableList<String> getClientesObservable() {
       // return clientesObservables;
    //}

    public void logPaquete(Paquete p){
        addActivity(p.getTipo() + " - " +p.getRemitente() + LocalTime.now());
    }



    public static void main(String[] args) {
        Servidor servidor = new Servidor();
        servidor.servidorUp();
    }

    public  String leerMensaje(Paquete p) {
        switch (p.getTipo()) {
            case CONECTAR -> {
                return p.getRemitente() + " se ha unido";
            }
            case MENSAJE -> {
                return p.getRemitente() + " dice: " + p.getMensajeCliente();

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