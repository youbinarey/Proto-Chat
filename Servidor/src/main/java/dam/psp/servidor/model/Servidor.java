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
import java.time.format.DateTimeFormatter;
import java.util.List;


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
    private synchronized ClienteHandler createCliente(String nickname, String IP, ObjectOutputStream out, ObjectInputStream in, Socket clienteSocket) {

        if (!sala.isNicknameInSala(nickname)) {
            ClienteHandler clienteHandler = new ClienteHandler(nickname, IP, out, in, clienteSocket, this);
            new Thread(clienteHandler).start();
            return clienteHandler;
        }

        System.out.println("El cliente ya esta conectado");

        //LOGICA PARA MANDAR  MENSAJE ERRONEO
        return null;
    }



    //PROCESAR PAQUETES --------------------
    public void procesarPaquete(Paquete p, ObjectOutputStream out, ObjectInputStream in, Socket clienteSocket, ClienteHandler cliente) {

        switch (p.getTipo()) {
            case AUTENTICACION -> {
                System.out.println("Recibido paquete autenticacion " + p.getIP());

                PaqueteAutenticacion pa = (PaqueteAutenticacion) p;// convierte el paquete para acceder a sus metodos
                addActivity(pa.getIP()+ "@" + pa.getTipo() );

                try {
                    //si autenticar es true manda true a la conexion
                    out.writeObject(autenticar(pa));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
            case CONECTAR -> {
                System.out.println("RECIBIDO UN PAQUETE CONECTAR");


                conectarCliente((PaqueteConectar) p, out, in, clienteSocket);


            }
            case MENSAJE -> {
                System.out.println("RECIBIDO UN PAQUETE MENSAJE");
                mensajeCliente(clienteSocket, in, out, cliente, p);

            }
            case PING -> {
                pingCliente(clienteSocket,in,out,cliente,p);
            }

            case DESCONECTAR -> {
                System.out.println("RECIBIDO UN PAQUETE DESCONECTAR");

                 desconectarCliente(clienteSocket, out, in, cliente, p);
            }
            default -> System.out.println("Tipo de Paquete no reconocido");
        }

    }

    private void pingCliente(Socket clienteSocket, ObjectInputStream in, ObjectOutputStream out, ClienteHandler cliente, Paquete p) {
            PaquetePing pp = (PaquetePing) p;
            System.out.println("Ping recibido de " + cliente.getNickname()+ " en " + pp.getTimestamp());

            // envia Ping de vuelta
        Paquete pong = PaqueteFactory.crearPaquete(pp.getTipo(), cliente.getNickname());

        cliente.enviarPaquete(pong);

    }

    private synchronized void conectarCliente(PaqueteConectar pc, ObjectOutputStream out, ObjectInputStream in, Socket clienteSocket) {
        ClienteHandler cliente = createCliente(pc.getUsuario(),pc.getIP(), out, in, clienteSocket);

        if(clienteSocket != null){
            // Notificar a la sala que un nuevo cliente se ha conectado
            sala.joinCliente(cliente);


            addActivity(pc.getUsuario() + "@" + pc.getTipo() + "//" + pc.getIP());
            broadcastNotify(pc,cliente);
        }else{
            addActivity("Error: El cliente ya está conectado: " + pc.getUsuario());
        }


        try {
            Thread.sleep(50L);
            enviarListaUsuarios();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    private boolean autenticar(PaqueteAutenticacion pa) {
        DatabaseManager dbManager = new DatabaseManager();
        boolean request = dbManager.logInUser(pa.getUsuario(), pa.getPassword());
        dbManager.closeConnection();
        return  request;

    }

    //TODO
     synchronized void desconectarCliente(Socket clienteSocket, ObjectOutputStream out, ObjectInputStream in, ClienteHandler cliente, Paquete p) {
        // Verificar si el cliente sigue en la sala antes de desconectarlo
        if (!sala.contieneCliente(cliente)) {
            return; // Si no está en la sala, ya ha sido desconectado, así que salimos
        }

        sala.leaveCliente(cliente);

        //LOG DEL SERVIDOR
         addActivity(cliente.getNickname() + "@" + p.getTipo() + "//" + p.getIP());
         broadcastNotify(p,cliente);
         enviarListaUsuarios();


        // Intentar enviar el mensaje solo si el socket sigue abierto
        if (clienteSocket != null && !clienteSocket.isClosed()) {
            try {
                if (out != null) {
                    out.writeObject(p);
                    out.flush();
                }
            } catch (IOException e) {
                System.err.println("Error al enviar mensaje de desconexión: " + e.getMessage());
            }
        }

        // Cerrar recursos
        try {
            if (out != null) out.close();
        } catch (IOException e) {
            System.err.println("Error al cerrar OutputStream: " + e.getMessage());
        }

        try {
            if (in != null) in.close();
        } catch (IOException e) {
            System.err.println("Error al cerrar InputStream: " + e.getMessage());
        }

        try {
            if (clienteSocket != null && !clienteSocket.isClosed()) {
                clienteSocket.close();
            }
            System.out.println("Cliente desconectado correctamente.");
        } catch (IOException e) {
            System.err.println("Error al cerrar el socket: " + e.getMessage());
        }
        enviarListaUsuarios();
    }




    public void addChat(PaqueteMensaje pm, ClienteHandler cliente) {

        sala.setChat(sala.getChat() + "\n" + cliente.getNickname() + " dice: " + pm.getMensaje());
    }

    void  mensajeCliente(Socket clienteSocket, ObjectInputStream in, ObjectOutputStream out, ClienteHandler cliente, Paquete p){
        //captura el mensaje y notifica a todos
        System.out.println(leerMensaje(p,cliente));

        //Crea el factory
        Paquete pm = PaqueteFactory.crearPaquete(p.getTipo(), cliente.getNickname(), leerMensaje(p,cliente));


        addActivity(cliente.getNickname() + "@" + p.getTipo() + "//" + p.getIP());


        //Procesar mensaje
        broadcastMensaje(pm, cliente);
    }

    //TODO refactorizar leerMensaje
    private synchronized void broadcastMensaje(Paquete p, ClienteHandler cliente) {
       PaqueteMensaje pm = (PaqueteMensaje) p;

        logPaquete(p, cliente);
        addChat(pm,cliente);


        broadcast(p);
        System.out.println("BROADCASTMENSAJE ENVIADO");
    }

    private void broadcast(Paquete p) {
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


        // TODO CREA UN BUENFORMATO DE LOG
    public void logPaquete(Paquete p,ClienteHandler cliente) {
        LocalTime time = LocalTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

        addActivity(p.getTipo() + " - " + p.getIP() +" - "+  time.format(formato));
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

    private synchronized void enviarListaUsuarios() {
        // Obtener la lista de clientes conectados
        List<String> listaUsuarios = sala.getClientesNickname();
        System.out.println("ENVIAR---"+ sala.getClientesNickname());

        // Enviar la lista de usuarios a todos los clientes conectados
        for (ClienteHandler cliente : sala.getClientes()) {
            try {
                // Enviar la lista como un objeto
                cliente.getOut().writeObject(listaUsuarios);
                cliente.getOut().flush();
            } catch (IOException e) {
                System.err.println("Error al enviar lista de usuarios a " + cliente.getNickname() + ": " + e.getMessage());
            }
        }
    }

    private void broadcastNotify(Paquete p, ClienteHandler cliente){
        // preparar mensaje
        String notificacion = "";
        if(p.getTipo()== TipoPaquete.CONECTAR)notificacion = " se ha unido!!";
        else if (p.getTipo()== TipoPaquete.DESCONECTAR) notificacion = " ha abandona la sala";

        //crear notificacion con mensaje
        p = PaqueteFactory.crearPaquete(TipoPaquete.NOTIFICACION, cliente.getNickname(), cliente.getNickname() + notificacion  );

        broadcast(p);
        System.out.println("BROADCAST NOTIFY ENVIADO");
    }


    public void detenerServidor() {
        try {
            // Notificar a los clientes que el servidor se está cerrando
            for (ClienteHandler cliente : sala.getClientes()) {
                cliente.setConnected(false); // Evita que vuelvan a entrar al bucle
                cliente.enviarPaquete(PaqueteFactory.crearPaquete(TipoPaquete.DESCONECTAR));
                cliente.getSocket().close(); // Forzar cierre del socket
            }

            // Detener el servidor cerrando el socket
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                addActivity("Servidor detenido");
            }
        } catch (IOException e) {
            System.err.println("Error al detener el servidor: " + e.getMessage());
        }
    }





}