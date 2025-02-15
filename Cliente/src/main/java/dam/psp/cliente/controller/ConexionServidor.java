package dam.psp.cliente.controller;

import dam.psp.cliente.config.Config;
import dam.psp.cliente.model.Paquete;

import java.io.*;
import java.net.Socket;


public class ConexionServidor {
    private static ConexionServidor instance;
    private Socket socket;
    private ObjectOutputStream  out;
    private ObjectInputStream in;
    private boolean clienteConectado;

    private ConexionServidor() {
        clienteConectado = false;
    }

    public static ConexionServidor getInstance() {
        if (instance == null) {
            instance = new ConexionServidor();
        }
        return instance;
    }

    public void conectar( Paquete p){
        try{
            //Inicializar socket
            socket = new Socket(Config.SERVER_IP, Config.SERVER_PORT);

            //Inicializar Datas
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            out.writeObject(p);
            out.flush();

            clienteConectado = true;

        } catch (IOException e) {
            System.err.println("Error al conectar con el servidor: " + e.getMessage());
        }
    }

    public void enviarDatos(Paquete p){
        try {
            if(socket == null || socket.isClosed()){
                conectar(p);
            }

            out.writeObject(p);
            //
            System.out.println("ConexionServidor envia un paquete de : " + p.getTipo().toString());
            //
            out.flush();
            out.reset();

        } catch (IOException e) {
            System.err.println("Error escucha Servidor " + e.getMessage());
            clienteConectado = false;
            cerrarConexion();
        }
    }




    public void escucharServidor() {
        new Thread(() -> {
            while (isClienteConectado()) {
                try{
                    Paquete paqueteRecibido = (Paquete) in.readObject();
                    if(paqueteRecibido != null){
                        System.out.println(paqueteRecibido.getMensajeCliente());
                        System.out.println("ConexionServidor recibe un paquete de : " + paqueteRecibido.getTipo().toString());


                    }
                } catch (IOException | ClassNotFoundException e) {
                    System.err.println("Error escuchaServidor " + e.getMessage());
                    e.printStackTrace();
                    clienteConectado = false;
                }
            }
            cerrarConexion();
        }).start();
    }








    public void cerrarConexion(){
        try {
            if(in != null) in.close();
            if(out != null) out.close();
            if(socket != null) socket.close();
            System.out.println("Conexión cerrada");
            clienteConectado = false;
        } catch (IOException e) {
            System.err.println("Error al cerrar la conexion " + e.getMessage());
        }
    }

    public boolean isClienteConectado() {
        return clienteConectado;
    }


}
