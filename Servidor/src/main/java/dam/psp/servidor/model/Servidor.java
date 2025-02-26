package dam.psp.servidor.model;



import dam.psp.cliente.model.paquete.*;
import dam.psp.cliente.model.paquete.Paquete;
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
    private ObjectInputStream out;
    private ObjectInputStream in;

    public Servidor() {

        try {
            serverSocket = new ServerSocket(PUERTO);
            logs = "";
            sala = new Sala();

        } catch (IOException e) {
            System.err.println("Error al crear el servidor en el puerto " + PUERTO + " " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        DatabaseManager dbManager = new DatabaseManager();

        if (dbManager.logInUser("Antonio", "abc123")) {
            System.out.println("USUARIO Y CONTRASEÑA CORRECTAS");
        }

        Paquete p = PaqueteFactory.crearPaquete(TipoPaquete.MENSAJE, "yo", "holaaa");


        System.out.println(p.getTipo());


    }

    public void setControlador(ServidorController controlador) {
        this.controlador = controlador;
    }

    public void servidorUp() {
        addActivity("Servidor Arrancado");
        while (true) {
            Socket clienteSocket = esperarConexion(); // Aceptar conexión
            if (clienteSocket != null) {
                conexiones(clienteSocket); // Manejar la conexión del cliente
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


    //Petticiond e acceso al servidor para...
    private void conexiones(Socket clienteSocket) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(clienteSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(clienteSocket.getInputStream());


            Paquete p = (Paquete) in.readObject();
            procesarPaquete(p, out, in, clienteSocket, null);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("SERVIDOR: Conexion rechazada por el servidor");

        }
    }


    // LLAMADO POR CONECTAR, se crea un cliente para manejarlo
    private ClienteHandler createCliente(String nickname, String IP, ObjectOutputStream out, ObjectInputStream in, Socket clienteSocket) {

        if (!sala.isNicknameInSala(nickname)) {
            ClienteHandler clienteHandler = new ClienteHandler(nickname, IP, out, in, clienteSocket, this);
            new Thread(clienteHandler).start();
            return clienteHandler;
        }

        System.out.println("El cliente ya esta conectado");

        //LOGICA PARA MANDAR  MENSAJE ERRONEO
        return null;
    }

    public void procesarPaquete(Paquete p, ObjectOutputStream out, ObjectInputStream in, Socket clienteSocket, ClienteHandler cliente) {



        switch (p.getTipo()) {
            case AUTENTICACION -> {
                System.out.println("Recibido paquete autenticacion");
                addActivity(p.getIP() +"@" + p.getTipo());

                PaqueteAutenticacion pa = (PaqueteAutenticacion) p;// convierte el paquete para acceder a sus metodos


                try {
                    //si autenticar es true manda true a la conexion
                    out.writeObject(autenticar(pa));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
            case CONECTAR -> {
                System.out.println("Entra ene la case...conectar");

                PaqueteConectar pc = (PaqueteConectar) p;
                addActivity("Cliente conectado: " + pc.getUsuario() + " desde " + pc.getIP());

                 ClienteHandler clienteHandler = createCliente(pc.getUsuario(),pc.getIP(), out, in, clienteSocket);

                 if(clienteSocket != null){
                     // Notificar a la sala que un nuevo cliente se ha conectado
                     sala.joinCliente(clienteHandler);
                     addActivity("Cliente añadido a la sala: " + pc.getUsuario());
                 }else{
                     addActivity("Error: El cliente ya está conectado: " + pc.getUsuario());
                 }
            }
            case MENSAJE -> {
                mensajeCliente(clienteSocket, in, out, cliente, p);

            }
            case DESCONECTAR -> {
                 desconectarCliente(clienteSocket, out, in, cliente, p);


            }
            default -> System.out.println("Tipo de Paquete no reconocido");
        }
    }


    // conectar
    private void conectarCliente(ClienteHandler cliente) {//
        if (sala.contieneCliente(cliente)) {
            //TODO PRESCINDIR O CAMBIAR COMPOROBACION
            System.out.println("Cliente duplicado:" + cliente.getNickname());
        } else {
            sala.joinCliente(cliente);
            //broadcastMensaje(p);
        }

    }

    private boolean autenticar(PaqueteAutenticacion pa) {
        DatabaseManager dbManager = new DatabaseManager();
        boolean request = dbManager.logInUser(pa.getUsuario(), pa.getPassword());
        dbManager.closeConnection();
        return  request;

    }

    //TODO
    void desconectarCliente(Socket clienteSocket, ObjectOutputStream out, ObjectInputStream in, ClienteHandler cliente, Paquete p) {

        sala.leaveCliente(cliente);
        //broadcastMensaje(p);


        try {
            out.writeObject(p);
            out.flush();
            out.close(); // Cerrar el ObjectOutputStream
            if (in != null) in.close();  // Cerrar el ObjectInputStream
            if (clienteSocket != null && !clienteSocket.isClosed()) {
                clienteSocket.close(); // Cerrar el socket
            }
            System.out.println("Cliente desconectado correctamente.");
        } catch (IOException e) {
            System.err.println("Error al desconectar al cliente: " + e.getMessage());
        }
    }



    public void addChat(PaqueteMensaje pm, ClienteHandler cliente) {

        sala.setChat(sala.getChat() + "\n" + cliente.getNickname() + " dice: " + pm.getMensaje());
    }

    void mensajeCliente(Socket clienteSocket, ObjectInputStream in, ObjectOutputStream out, ClienteHandler cliente, Paquete p){
        //captura el mensaje y notifica a todos
        System.out.println(leerMensaje(p,cliente));
        Paquete pm = PaqueteFactory.crearPaquete(TipoPaquete.MENSAJE,cliente.getNickname(), leerMensaje(p,cliente));

        //Procesar mensaje
        broadcastMensaje(pm, cliente);
    }

    //TODO refactorizar leerMensaje
    private void broadcastMensaje(Paquete p, ClienteHandler cliente) {
       PaqueteMensaje pm = (PaqueteMensaje) p;

        logPaquete(p);
        addChat(pm,cliente);

        System.out.println("USUARIOS CONECTADOS");
        System.out.println(sala.getClientesNickname());

        // Difusión
        for (ClienteHandler c : sala.getClientes()) {

            c.enviarPaquete(p);
        }
        System.out.println("BROADCASTMENSAJE ENVIADO");
    }



    private void addActivity(String log) {
        logs = log;
        showActivity();
        Platform.runLater(() -> controlador.mostrarLog(logs)); // controlador es la instancia de ServidorController
    }

    private void showActivity() {
        System.out.println(logs);
    }



    public void logPaquete(Paquete p) {
        addActivity(p.getTipo() + " - " + p.getIP() + LocalTime.now());
    }

    public String leerMensaje(Paquete p, ClienteHandler cliente) {


        switch (p.getTipo()) {

            case CONECTAR -> {

                return "hola";

            }
            case MENSAJE -> {


                return cliente.getNickname() +": " + ((PaqueteMensaje) p).getMensaje();

            }
            case DESCONECTAR -> {
                //return p.getRemitente() + " ha abandonado la sala";
                return "hola";

            }
            default -> {
                return "OPCION NO RECONOCIDA";
            }

        }
    }


}