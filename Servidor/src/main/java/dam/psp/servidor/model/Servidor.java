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
import java.util.ArrayList;
import java.util.List;

/**
 * La clase Servidor gestiona la comunicación de red entre un servidor y varios clientes.
 * Inicia un servidor en un puerto específico y maneja la conexión, autenticación, mensajes,
 * desconexión y otros eventos relacionados con los clientes.
 */
public class Servidor {

    private final int PUERTO = Config.SERVER_PORT; // El puerto en el que el servidor escucha conexiones entrantes.
    public Sala sala; // La sala que gestiona los clientes conectados al servidor.
    private ServerSocket serverSocket; // El socket del servidor para escuchar conexiones.
    private ServidorController controlador; // El controlador que maneja la lógica del servidor.
    private List<String> logs; // Lista de logs de las actividades del servidor.


    /**
     * Constructor de la clase Servidor. Inicializa el servidor, el socket y la sala de clientes.
     *
     */
    public Servidor() {
        try {
            // Inicializa el socket del servidor en el puerto especificado.
            serverSocket = new ServerSocket(PUERTO);
            logs = new ArrayList<>(); // Inicializa la lista de logs.
            sala = new Sala(); // Inicializa la sala de clientes.
        } catch (IOException e) {
            System.err.println("Error al crear el servidor en el puerto " + PUERTO + " " + e.getMessage());
        }
    }


    /**
     * Establece el controlador que manejará la lógica del servidor.
     *
     * @param controlador El controlador que se asignará al servidor para gestionar las actividades y eventos.
     */
    public void setControlador(ServidorController controlador) {
        this.controlador = controlador;
    }


    /**
     * Inicia el servidor y comienza a escuchar conexiones de clientes en un bucle infinito.
     * Una vez que se acepta una conexión, se maneja la comunicación con el cliente.
     *
     * <p>Este método también registra la actividad de arranque del servidor y mantiene el servidor
     * en funcionamiento, esperando continuamente conexiones entrantes de clientes.</p>
     */
    public void servidorUp() {
        addActivity("Servidor Arrancado"); // Registra que el servidor ha arrancado
        while (true) {
            // Espera una conexión de un cliente
            Socket clienteSocket = esperarConexion();
            if (clienteSocket != null) {
                // Maneja la conexión del cliente
                conexiones(clienteSocket);
            }
        }
    }


    /**
     * Espera una conexión entrante de un cliente en el servidor.
     *
     * <p>Este método bloquea el hilo actual hasta que un cliente se conecta al servidor
     * a través del socket. Si ocurre un error al esperar la conexión, se captura y semuestra un mensaje de error.</p>
     *
     * @return El socket de la conexión aceptada o {@code null} si ocurrió un error durante el proceso de aceptación.
     */
    private Socket esperarConexion() {
        try {
            return serverSocket.accept();
        } catch (IOException e) {
            System.out.println("Error esperando Conexión " + e.getMessage());
            return null;
        }
    }


    /**
     * Maneja la conexión de un cliente al servidor.
     *
     * Este método establece los flujos de entrada y salida para la comunicación con el cliente,
     * lee el primer paquete enviado por el cliente y lo procesa. Si ocurre un error durante la
     * lectura del paquete o el establecimiento de la conexión, se maneja la excepción correspondiente.
     *
     * @param clienteSocket El socket de la conexión con el cliente.
     */
    private void conexiones(Socket clienteSocket) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(clienteSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(clienteSocket.getInputStream());

            // Lee el paquete enviado por el cliente
            Paquete p = (Paquete) in.readObject();

            // Procesa el paquete recibido
            procesarPaquete(p, out, in, clienteSocket, null);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("SERVIDOR: Conexion rechazada por el servidor");
        }
    }



    /**
     * Crea un nuevo cliente y lo agrega a la sala si el nickname no está en uso.
     *
     * Este método verifica si el nickname del cliente ya está en uso dentro de la sala.
     * Si no está en uso, crea un nuevo objeto {@code ClienteHandler}, lo inicia en un hilo
     * independiente y lo devuelve. Si el nickname ya está registrado, se imprime un mensaje
     * indicando que el cliente ya está conectado y se devuelve {@code null}.
     *
     * @param nickname       El nombre del cliente que se intenta conectar.
     * @param IP             La dirección IP del cliente.
     * @param out            El flujo de salida para la comunicación con el cliente.
     * @param in             El flujo de entrada para recibir datos del cliente.
     * @param clienteSocket  El socket del cliente conectado.
     * @return               Un objeto {@code ClienteHandler} si el cliente fue creado con éxito,
     *                       o {@code null} si el nickname ya estaba en uso.
     */
    private synchronized ClienteHandler createCliente(String nickname, String IP, ObjectOutputStream out, ObjectInputStream in, Socket clienteSocket) {

        if (!sala.isNicknameInSala(nickname)) {
            ClienteHandler clienteHandler = new ClienteHandler(nickname, IP, out, in, clienteSocket, this);
            new Thread(clienteHandler).start();
            return clienteHandler;
        }

        System.out.println("El cliente ya está conectado");

        // Lógica para enviar un mensaje de error al cliente si el nickname ya está en uso.
        return null;
    }




    /**
     * Procesa un paquete recibido y ejecuta la acción correspondiente según su tipo.
     * <p>
     * Dependiendo del tipo de paquete recibido, este método realiza distintas operaciones, como
     * autenticación, conexión, envío de mensajes, verificación de estado (ping), desconexión o
     * recepción de archivos. Cada tipo de paquete se maneja en su propia sección dentro de un
     * bloque {@code switch-case}.
     * </p>
     *
     * @param p              El paquete recibido que contiene la información a procesar.
     * @param out            El flujo de salida para la comunicación con el cliente.
     * @param in             El flujo de entrada para recibir datos del cliente.
     * @param clienteSocket  El socket del cliente conectado.
     * @param cliente        El {@code ClienteHandler} que maneja la conexión del cliente
     *                       (puede ser {@code null} si el cliente aún no está autenticado).
     */
    public void procesarPaquete(Paquete p, ObjectOutputStream out, ObjectInputStream in, Socket clienteSocket, ClienteHandler cliente) {

        switch (p.getTipo()) {
            case AUTENTICACION -> {
                System.out.println("Recibido UN PAQUETE CONECTAR " + p.getIP());
                logPaquete(p, cliente);
                try {
                    // Si la autenticación es correcta, envía una respuesta positiva
                    out.writeObject(autenticar((PaqueteAutenticacion) p));
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
                mensajeCliente(cliente, p);
            }
            case PING -> {
                System.out.println("RECIBIDO UN PAQUETE PING");
                pingCliente(cliente, p);
            }
            case DESCONECTAR -> {
                System.out.println("RECIBIDO UN PAQUETE DESCONECTAR");
                desconectarCliente(clienteSocket, out, in, cliente, p);
            }
            case ARCHIVO -> {
                System.out.println("RECIBIDO UN ARCHIVO");
                archivoCliente(cliente, p);
            }
            default -> System.out.println("Tipo de Paquete no reconocido");
        }
    }


    /**
     * Procesa un paquete de tipo archivo enviado por un cliente y lo difunde a los demás clientes conectados.
     *
     * @param cliente El cliente que envió el paquete.
     * @param p       El paquete que contiene el archivo.
     */
    private void archivoCliente(ClienteHandler cliente, Paquete p) {
        logPaquete(p, cliente);
        broadcast(p);
    }

    /**
     * Responde a un paquete de tipo PING enviando un paquete PONG de vuelta al cliente.
     *
     * @param cliente El cliente que envió el paquete PING.
     * @param p       El paquete de tipo PING recibido.
     */
    private void pingCliente(ClienteHandler cliente, Paquete p) {
        logPaquete(p, cliente);
        // Envia un PONG de vuelta al cliente
        Paquete pong = PaqueteFactory.crearPaquete(p.getTipo(), cliente.getNickname());
        cliente.enviarPaquete(pong);
    }

    /**
     * Maneja la conexión de un nuevo cliente al servidor.
     * <p>
     * Crea un nuevo {@code ClienteHandler} si el usuario no está ya conectado, lo añade a la sala
     * y notifica a los demás clientes sobre su conexión. También envía la lista actualizada de usuarios.
     * </p>
     *
     * @param pc            El paquete de conexión recibido.
     * @param out           Flujo de salida para comunicarse con el cliente.
     * @param in            Flujo de entrada para recibir datos del cliente.
     * @param clienteSocket Socket del cliente que intenta conectarse.
     */
    private synchronized void conectarCliente(PaqueteConectar pc, ObjectOutputStream out, ObjectInputStream in, Socket clienteSocket) {
        ClienteHandler cliente = createCliente(pc.getUsuario(), pc.getIP(), out, in, clienteSocket);
        if (cliente != null) {
            // Notificar a la sala que un nuevo cliente se ha conectado
            sala.joinCliente(cliente);
            broadcastNotify(pc, cliente);
        } else {
            System.out.println("Error: El cliente ya está conectado.");
        }
        logPaquete(pc, cliente);
        try {
            Thread.sleep(50L);
            enviarListaUsuarios();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Autentica a un usuario verificando sus credenciales en la base de datos.
     *
     * @param pa El paquete de autenticación que contiene el nombre de usuario y la contraseña.
     * @return {@code true} si la autenticación es exitosa, {@code false} en caso contrario.
     */
    private boolean autenticar(PaqueteAutenticacion pa) {
        DatabaseManager dbManager = new DatabaseManager();
        boolean request = dbManager.logInUser(pa.getUsuario(), pa.getPassword());
        dbManager.closeConnection();
        return request;
    }



    /**
     * Maneja la desconexión de un cliente del servidor.
     * <p>
     * Si el cliente está en la sala, lo elimina de la lista de clientes y notifica a los demás usuarios sobre su desconexión.
     * También cierra los recursos asociados al cliente (socket y flujos de entrada/salida).
     * </p>
     *
     * @param clienteSocket El socket del cliente que se va a desconectar.
     * @param out           Flujo de salida para enviar datos al cliente.
     * @param in            Flujo de entrada para recibir datos del cliente.
     * @param cliente       El cliente que se está desconectando.
     * @param p             El paquete de desconexión recibido.
     */
    synchronized void desconectarCliente(Socket clienteSocket, ObjectOutputStream out, ObjectInputStream in, ClienteHandler cliente, Paquete p) {

        // Si el cliente no está en la sala, ya ha sido desconectado
        if (!sala.contieneCliente(cliente)) {
            return;
        }

        // Remover al cliente de la sala
        sala.leaveCliente(cliente);
        logPaquete(p, cliente);
        broadcastNotify(p, cliente);
        enviarListaUsuarios();

        // Intentar enviar el paquete de desconexión si el socket sigue abierto
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

        // Cerrar los recursos asociados al cliente
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
    }




    /**
     * Procesa un mensaje recibido de un cliente y lo retransmite a todos los clientes conectados.
     *
     * @param cliente El cliente que envió el mensaje.
     * @param p       El paquete que contiene el mensaje.
     */
    void mensajeCliente(ClienteHandler cliente, Paquete p) {
        logPaquete(p, cliente);
        // Procesar y retransmitir el mensaje
        broadcastMensaje((PaqueteMensaje) p);
    }

    /**
     * Retransmite un mensaje recibido a todos los clientes conectados.
     *
     * @param p El paquete de mensaje que se enviará a todos los clientes.
     */
    private synchronized void broadcastMensaje(Paquete p) {
        broadcast(p);
        System.out.println("BROADCASTMENSAJE ENVIADO");
    }

    /**
     * Envía un paquete a todos los clientes conectados en la sala.
     *
     * @param p El paquete que se enviará a todos los clientes.
     */
    private void broadcast(Paquete p) {
        for (ClienteHandler c : sala.getClientes()) {
            c.enviarPaquete(p);
        }
    }

    /**
     * Agrega una actividad al registro de logs del servidor y la muestra en la interfaz de usuario.
     *
     * @param log Mensaje de actividad a registrar.
     */
    private void addActivity(String log) {
        logs.add(log);
        showActivity();
        Platform.runLater(() -> controlador.mostrarLog(log)); // controlador es la instancia de ServidorController
    }

    /**
     * Muestra el historial de actividades registradas en la consola.
     */
    private void showActivity() {
        System.out.println(logs);
    }



    /**
     * Registra la actividad de un paquete recibido en los logs del servidor.
     *
     * @param p       El paquete recibido.
     * @param cliente El cliente que envió el paquete. Si es null, se usa la IP del paquete.
     */
    public void logPaquete(Paquete p, ClienteHandler cliente) {
        LocalTime time = LocalTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

        if (cliente == null) {
            addActivity(p.getIP() + "@" + p.getTipo() + " - " + time.format(formato));
        } else {
            addActivity(cliente.getNickname() + "@" + p.getTipo() + "//" + p.getIP() + " - " + time.format(formato));
        }
    }

    /**
     * Envía la lista de usuarios conectados a todos los clientes en la sala.
     */
    private synchronized void enviarListaUsuarios() {
        // Obtener la lista de clientes conectados
        List<String> listaUsuarios = sala.getClientesNickname();
        System.out.println("ENVIAR---" + sala.getClientesNickname());

        // Enviar la lista de usuarios a todos los clientes conectados
        for (ClienteHandler cliente : sala.getClientes()) {
            try {
                cliente.getOut().writeObject(listaUsuarios);
                cliente.getOut().flush();
            } catch (IOException e) {
                System.err.println("Error al enviar lista de usuarios a " + cliente.getNickname() + ": " + e.getMessage());
            }
        }
    }

    /**
     * Notifica a todos los clientes cuando un usuario se conecta o desconecta.
     *
     * @param p       El paquete recibido que indica la acción de conexión o desconexión.
     * @param cliente El cliente que se ha conectado o desconectado.
     */
    private void broadcastNotify(Paquete p, ClienteHandler cliente) {
        // Preparar mensaje de notificación
        String notificacion = "";
        if (p.getTipo() == TipoPaquete.CONECTAR) {
            notificacion = " se ha unido!!";
        } else if (p.getTipo() == TipoPaquete.DESCONECTAR) {
            notificacion = " ha abandonado la sala";
        }

        // Crear y enviar el paquete de notificación
        p = PaqueteFactory.crearPaquete(TipoPaquete.NOTIFICACION, cliente.getNickname(), cliente.getNickname() + notificacion);
        broadcast(p);
        System.out.println("BROADCAST NOTIFY ENVIADO");
    }



    /**
     * Detiene el servidor cerrando todas las conexiones activas y notificando a los clientes.
     *
     * <p>Este método realiza los siguientes pasos:</p>
     * <ul>
     *   <li>Notifica a los clientes que el servidor se está cerrando.</li>
     *   <li>Marca a los clientes como desconectados y cierra sus sockets.</li>
     *   <li>Cierra el socket del servidor para detener la escucha de nuevas conexiones.</li>
     * </ul>
     */
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