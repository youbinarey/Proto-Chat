package dam.psp.cliente.model;

import dam.psp.cliente.config.Config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


public class Cliente {
    private String nombre;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;


    public Cliente(String nombre) {
        this.nombre = nombre;
    }


    public String getNombre() {
        return nombre;
    }

    //ESTO TIENE QUE SER SINGLETON
    public void clienteJoin(){
        try {
            socket = new Socket(Config.SERVER_IP, Config.SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println(nombre);
        } catch (IOException e) {
            System.err.println("Error al conectar con el servidor " + e.getMessage());
        }

    }

    // Crear un hilo anónimo para el método
    private void recibirmsg() {
        new Thread(()-> {
            String respuesta;
            while (true) {
                try {
                    if ((respuesta = in.readLine()) != null) {
                        System.out.println("Servidor: " + respuesta);
                    }
                } catch (IOException e) {
                    System.err.println("Error al recibir el mensaje del servidor " + e.getMessage());
                }
            }
        }).start();
    }

    // Crear un hilo anónimo para el método
    private void enviarMsg() {
        new Thread(()-> {
        Scanner sc = new Scanner(System.in);
        String input;
        while(!(input = sc.nextLine()).equals("\n")){
            out.println(input);
            recibirmsg();
        }
        }).start();
    }

    public static void main(String[] args) {
        Cliente cliente = new Cliente("Yeray");
        cliente.clienteJoin();
        cliente.enviarMsg();
    }
}
