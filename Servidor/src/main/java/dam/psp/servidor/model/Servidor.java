package dam.psp.servidor.model;


import dam.psp.cliente.model.Paquete;
import dam.psp.cliente.model.TipoPaquete;
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
        Socket clienteSocket = esperarConexion(); // Aceptar conexion

        if(clienteSocket != null){
            conexionCliente(clienteSocket);

        }

    }

    private Socket esperarConexion() {
        try{
            return serverSocket.accept();
        } catch (IOException e) {
            System.out.println("Error esperando Conexión " + e.getMessage());
            return null;
        }
    }

    private void conexionCliente(Socket clienteSocket) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(clienteSocket.getOutputStream());

            ObjectInputStream in = new ObjectInputStream(clienteSocket.getInputStream());

            while(true){
                Paquete pRecibido = (Paquete) in.readObject();
                
                Paquete pEnviar;

                if(pRecibido == null){
                    break;
                }
                System.out.print("Paquete recibido");
                infoPaquete(pRecibido);

                pEnviar = procesarPaquete(pRecibido);
                System.out.print("Paquete para enviar");
                infoPaquete(pEnviar);
                out.writeObject(pEnviar);
                out.flush();
                out.reset();

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private Paquete procesarPaquete(Paquete p){
       
        switch (p.getTipo()){
            case CONECTAR -> {p.setMensajeCliente("Conexion Exitosa");
                p.setTipo(TipoPaquete.CONECTAR);}

            case MENSAJE -> {p.setMensajeCliente("Mensaje Recibido: ");
                p.setTipo(TipoPaquete.MENSAJE);}
            case ARCHIVO -> {p.setMensajeCliente("Archivo Recibido: ");
                p.setTipo(TipoPaquete.ARCHIVO);}
            case NOTIFICACION -> {p.setMensajeCliente("Notificación Recibida: ");
                p.setTipo(TipoPaquete.NOTIFICACION);}
            case AUTENTICACION -> {p.setMensajeCliente("AUTENTICACION Recibida: ");
                p.setTipo(TipoPaquete.AUTENTICACION);}
            case DESCONECTAR -> {p.setMensajeCliente("Desconexión Recibida: ");
                p.setTipo(TipoPaquete.DESCONECTAR);}
            default -> System.out.println
                    ("Tipo de Paquete no reconocido");
        }
        return p;
    }

    private void addActivity(String activity){
        logs += activity + "\n";
        showActivity();
    }

    private void showActivity(){
        System.out.println(logs);
    }
    private void infoPaquete(Paquete p){
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


}


