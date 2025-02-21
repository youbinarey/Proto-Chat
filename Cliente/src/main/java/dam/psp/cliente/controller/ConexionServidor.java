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
    private PaqueteListener messageListener;

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
        if(!clienteConectado){
            try{
                //Inicializar socket
                socket = new Socket(Config.SERVER_IP, Config.SERVER_PORT);

                //Inicializar Datas
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());

                out.writeObject(p);
                out.flush();
                out.reset();

                clienteConectado = true;

            } catch (IOException e) {
                System.err.println("Error al conectar con el servidor: " + e.getMessage());
            }
        }else{
            System.out.println("Ya hay una conexión iniciada");

        }

    }

    public void enviarMensaje(Paquete p){
        try {
            if(socket == null || socket.isClosed()){
                System.err.println("No existe conexion, Por favor conecta primero");
                return;
            }

            out.writeObject(p);
            //
            System.out.println("ConexionServidor envia un paquete de : " + p.getTipo().toString());
            //
            out.flush();
            out.reset();


        } catch (IOException e) {
            System.err.println("Error al enviar paquete " + e.getMessage());
            clienteConectado = false;
        }
    }




    public void escucharServidor() {
        new Thread(() -> {
            while (isClienteConectado()) {
                try{
                    Paquete paqueteRecibido = (Paquete) in.readObject();
                    if(paqueteRecibido != null){
                        //MUESTRA EL MENSAJE POR TErminal
                        System.out.println(paqueteRecibido.getMensajeCliente());
                        messageListener.mensajeRecibido(paqueteRecibido);
                        if(paqueteRecibido.getListaUsuarios() != null){
                            messageListener.updateUsuariosConectados(paqueteRecibido.getListaUsuarios());
                        }
                        System.out.println("Clietne recibe:");
                                System.out.println(paqueteRecibido.getListaUsuarios());
                    }
                } catch (IOException | ClassNotFoundException e) {
                    clienteConectado = false;
                }
            }
        }).start();
    }

    public void cerrarConexion(){
        try {
            if(in != null) in.close();
            if(out != null) out.close();
            if(socket != null) socket.close();

            System.out.println("---Conexión cerrada");
                clienteConectado = false;

        } catch (IOException e) {
            System.err.println("Error al cerrar la conexion " + e.getMessage());
        }
    }

    public void procesarPaquete(Paquete p){
        switch (p.getTipo()){
            case CONECTAR -> {conectar(p);
            escucharServidor();}

            case MENSAJE -> {
                enviarMensaje(p);
                ;}
            /*
            case ARCHIVO -> {p.setMensajeCliente("Archivo Recibido: ");
                p.setTipo(TipoPaquete.ARCHIVO);}

            case NOTIFICACION -> {p.setMensajeCliente("Notificación Recibida: ");
                p.setTipo(TipoPaquete.NOTIFICACION);}

            case AUTENTICACION -> {p.setMensajeCliente("AUTENTICACION Recibida: ");
                p.setTipo(TipoPaquete.AUTENTICACION);}
            */
            case DESCONECTAR -> {
                enviarMensaje(p);
                cerrarConexion();
            }

            default -> System.out.println
                    ("Tipo de Paquete no reconocido");
        }
    }

    public boolean isClienteConectado() {
        return clienteConectado;
    }

    public void setMessageListener(PaqueteListener listener) {
        this.messageListener = listener;
    }


}
