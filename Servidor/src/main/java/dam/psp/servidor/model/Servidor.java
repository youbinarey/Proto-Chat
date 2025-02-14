package dam.psp.servidor.model;


import dam.psp.cliente.model.Paquete;
import dam.psp.servidor.config.Config;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {

    private ServerSocket serverSocket;
    private final int PUERTO = Config.SERVER_PORT;
    private String logs;

    public Servidor(){
        try {
            serverSocket = new ServerSocket(PUERTO);
           logs ="";
        } catch (IOException e) {
            System.err.println("Error al crear el servidor en el puerto " + PUERTO + " " + e.getMessage());
        }
    }



    public void servidorUp()  {

        addActivity("Servidor iniciado en el puerto " + PUERTO);
        showActivity();

            try {
                Socket clienteSocket = serverSocket.accept(); // Aceptar conexion

                ObjectOutputStream out = new ObjectOutputStream(clienteSocket.getOutputStream());// Inicializar out

                ObjectInputStream in = new ObjectInputStream(clienteSocket.getInputStream());// Inicializar in

            // recibir y enviar paquetes
                while (true) {
                    try {
                        // Leer el paquete del cliente
                        Paquete paqueteRecibido = (Paquete) in.readObject();
                        System.out.println("Paquete recibido: " + paqueteRecibido.getTipo().toString());

                        // Procesar el paquete y enviar una respuesta
                        paqueteRecibido.setRemitente("Servidor");
                        paqueteRecibido.setMensajeCliente("ConexionExitosa");
                        out.writeObject(paqueteRecibido);
                        out.flush();
                    } catch (IOException e) {
                        System.err.println("Error al recibir/enviar datos: " + e.getMessage());
                        break;
                    } catch (ClassNotFoundException e) {
                        System.err.println("Error de deserialización: " + e.getMessage());
                        break;
                    }
                }

                // Cerrar la conexión con el cliente
                clienteSocket.close();
                addActivity("Cliente desconectado: " + clienteSocket.getInetAddress().getHostAddress());
            } catch (IOException e) {
                System.err.println("Error al aceptar conexión del cliente: " + e.getMessage());
            }
    }

    private void addActivity(String activity){
        logs += activity + "\n";
    }

    private void showActivity(){
        System.out.println(logs);
    }


    public static void main(String[] args) {
        Servidor servidor = new Servidor();
        servidor.servidorUp();
    }


}


