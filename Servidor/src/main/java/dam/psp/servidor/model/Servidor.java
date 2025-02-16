package dam.psp.servidor.model;

import dam.psp.cliente.model.Paquete;
import dam.psp.cliente.model.TipoPaquete;
import dam.psp.servidor.config.Config;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class Servidor {

    private ServerSocket serverSocket;
    private final int PUERTO = Config.SERVER_PORT;
    private String logs;
    private Sala sala;
    private Set<ClienteHandler> clientes;

    public Servidor() {
        try {
            serverSocket = new ServerSocket(PUERTO);
            logs = "";
            sala = new Sala();
            clientes = new HashSet<>();
        } catch (IOException e) {
            System.err.println("Error al crear el servidor en el puerto " + PUERTO + " " + e.getMessage());
        }
    }

    public void servidorUp() {
        addActivity();
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
        clientes.add(clienteHandler);
        new Thread(clienteHandler).start();
    }

    public void procesarPaquete(Paquete p, ObjectOutputStream out, ObjectInputStream in, Socket clienteSocket, ClienteHandler clienteHandler) {
        switch (p.getTipo()) {
            case CONECTAR -> {
                if (sala.contieneCliente(p.getRemitente())) {
                    System.out.println("Cliente duplicado:" + p.getRemitente());
                } else {
                    clienteHandler.setNickname(p.getRemitente());
                    sala.joinCliente(p.getRemitente());
                    broadcastMensaje(p);
                }
            }
            case MENSAJE -> {
                //capturar quien lo envia y el mensaje
                System.out.println(leerMensaje(p));
                addChat(p);
                broadcastMensaje(p);
            }
            case DESCONECTAR -> {
                desconectarCliente(clienteSocket, out, in, p.getRemitente());
                clientes.remove(clienteHandler);
                broadcastMensaje(p);
            }
            default -> System.out.println("Tipo de Paquete no reconocido");
        }
    }

    void desconectarCliente(Socket clienteSocket, ObjectOutputStream out, ObjectInputStream in, String nickname) {
        sala.leaveCliente(nickname); // Eliminar al cliente de la sala

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
    public void addChat(Paquete p) {
        sala.setChat(sala.getChat() + "\n" + p.getRemitente() + " dice: " + p.getMensajeCliente());
    }

    private void broadcastMensaje(Paquete pRecibido) {

        Paquete pEnviar = new Paquete();
        pEnviar.setTipo(TipoPaquete.MENSAJE);
        pEnviar.setRemitente(pRecibido.getRemitente());
        pEnviar.setMensajeCliente((leerMensaje(pRecibido)));
        pEnviar.setDestinatario("TODOS");

        System.out.println("handeler envia  -> " + pEnviar.getMensajeCliente());
        for (ClienteHandler cliente : clientes) {
            cliente.enviarPaquete(pEnviar);
        }
    }

    private void addActivity() {
        logs += "Servidor iniciado en el puerto 9999" + "\n";
        showActivity();
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