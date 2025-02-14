package dam.psp.servidor.model;

import dam.psp.servidor.config.Config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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



    public void servidorUp() throws IOException {

        addActivity("Servidor iniciado en el puerto " + PUERTO);
        showActivity();

        while (true) {
            try (
                Socket clienteSocket = serverSocket.accept();
                BufferedReader in = new BufferedReader((new InputStreamReader(clienteSocket.getInputStream())));
                PrintWriter out = new PrintWriter(clienteSocket.getOutputStream(), true)){

                addActivity("Cliente conectado desde la IP " + clienteSocket.getInetAddress());
                showActivity();

                String mensaje;
                while ((mensaje = in.readLine()) != null) {
                    addActivity(clienteSocket.getInetAddress() +": "+ mensaje);
                    showActivity();
                    out.println("SEVER_STATUS: OK");
                }
                clienteSocket.close();
            } catch (IOException e) {
                System.err.println("Error al conectar con el cliente " + e.getMessage());
            }
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


