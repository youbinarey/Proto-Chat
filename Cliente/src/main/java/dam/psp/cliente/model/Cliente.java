package dam.psp.cliente.model;

import dam.psp.cliente.config.Config;

import java.io.IOException;
import java.net.Socket;


public class Cliente {
    private String nombre;

    public Cliente(String nombre) {
        this.nombre = nombre;
    }

    public Cliente(){};

    public String getNombre() {
        return nombre;
    }


    public void clienteJoin(){

        try (Socket socket = new Socket(Config.SERVER_IP, Config.SERVER_PORT)) {

        } catch (IOException e) {
            System.err.println("Error al conectar con el servidor " + e.getMessage());
        }


    }

    public static void main(String[] args) {
        Cliente cliente = new Cliente();
        cliente.clienteJoin();
    }
}
