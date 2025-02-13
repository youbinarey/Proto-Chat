package dam.psp.servidor.model;

import dam.psp.servidor.config.Config;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {

    private static Servidor servidor;
    private ServerSocket serverSocket;
    private final int PUERTO = Config.SERVER_PORT;

    private Servidor(){
        try {
            serverSocket = new ServerSocket(PUERTO);
            System.out.println("Servidor iniciado en el puerto " + PUERTO);

        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor en el puerto " + PUERTO + " " + e.getMessage());
        }
    }

    public static  Servidor getInstance(){
        if(servidor == null){
            servidor = new Servidor();
        }
        return servidor;
    }

    public void servidorUp(){
        System.out.println("Servidor escuchando en el puerto " + PUERTO);
        while(true){
            try (Socket clietneSocket = serverSocket.accept()) {
                System.out.println("Cliente conectado desde " + clietneSocket.getInetAddress().getHostAddress());

            } catch (IOException e) {
                System.err.println("Error al aceptar la conexión del cliente " + e.getMessage());
            }

        }
    }

    public static void main(String[] args) {
        Servidor servidor = Servidor.getInstance();
        servidor.servidorUp();
    }


}


