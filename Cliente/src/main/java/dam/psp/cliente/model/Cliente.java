package dam.psp.cliente.model;

import dam.psp.cliente.config.Config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


public class Cliente {
    private String nombre = "Cliente1";

    public Cliente(String nombre) {
        this.nombre = nombre;
    }

    public Cliente(){};

    public String getNombre() {
        return nombre;
    }


    public void clienteJoin(){

        try (Socket socket = new Socket(Config.SERVER_IP, Config.SERVER_PORT)) {

            //ENVIAR MENSAJE AL SERVIDOR
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            Scanner sc = new Scanner(System.in);
            String input;
            while(!(input = sc.nextLine()).equals("\n")){
                out.println(input);
            }



            //RECIBIR MENSAJE DEL SERVIDOR
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String respuesta;
            while ((respuesta = in.readLine()) != null) {
                System.out.println("Servidor: " + respuesta);
            }

        } catch (IOException e) {
            System.err.println("Error al conectar con el servidor " + e.getMessage());
        }


    }

    public static void main(String[] args) {
        Cliente cliente = new Cliente();
        cliente.clienteJoin();
    }
}
