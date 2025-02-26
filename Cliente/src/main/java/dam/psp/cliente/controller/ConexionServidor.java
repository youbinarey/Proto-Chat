package dam.psp.cliente.controller;

import dam.psp.cliente.config.Config;
import dam.psp.cliente.model.paquete.*;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

import static dam.psp.cliente.model.paquete.TipoPaquete.CONECTAR;

public class ConexionServidor {
    private static ConexionServidor instance;
    private Socket socket;
    private ObjectOutputStream out;
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

    public synchronized void conectar(Paquete p) {
        if (!clienteConectado) {
            try {
                socket = new Socket(Config.SERVER_IP, Config.SERVER_PORT);
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());

                out.writeObject(p);
                out.flush();
                out.reset();
                System.out.println("Paquete de conexión enviado.");

                clienteConectado = true;
                escucharServidor();

            } catch (IOException e) {
                System.err.println("Error al conectar con el servidor: " + e.getMessage());
            }
        } else {
            System.out.println("Ya hay una conexión iniciada.");
        }
    }

    public synchronized boolean autenticar(Paquete p) {
        if (clienteConectado) {
            System.err.println("Ya hay una sesión iniciada. No es necesario autenticar nuevamente.");
            return false;
        }

        try {
            socket = new Socket(Config.SERVER_IP, Config.SERVER_PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());

            System.out.println("Enviando paquete de autenticación...");
            out.writeObject(p);
            out.flush();

            Boolean acceso = (Boolean) in.readObject();
            if (acceso) {
                System.out.println("Autenticación exitosa.");

                return true;
            } else {
                System.out.println("Autenticación fallida.");

            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error en autenticación: " + e.getMessage());
            cerrarConexion();
        }
        return false;
    }

    public synchronized void enviarMensaje(Paquete p) {
        try {
            if (!clienteConectado || socket == null || socket.isClosed()) {
                System.err.println("No hay conexión establecida.");
                return;
            }

            PaqueteMensaje pm = (PaqueteMensaje) p;
            out.writeObject(p);
            out.flush();
            out.reset();
            System.out.println("Mensaje enviado: " + pm.getMensaje());

        } catch (IOException e) {
            System.err.println("Error al enviar mensaje: " + e.getMessage());
            clienteConectado = false;
        }
    }
    public synchronized void enviarDesconexion(Paquete p) {
        try {
            if (!clienteConectado || socket == null || socket.isClosed()) {
                System.err.println("No hay conexión establecida.");
                return;
            }
            PaqueteDesconectar pd = (PaqueteDesconectar) p;

            out.writeObject(pd);
            out.flush();
            out.reset();
            System.out.println("Paquete desconectar enviado: ");

            try {
                Paquete desconectar = (Paquete) in.readObject();
                    System.out.println("El servidor te autoriza a abandonar la sala");

                    cerrarConexion();

            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

        } catch (IOException e) {
            System.err.println("Error al enviar mensaje: " + e.getMessage());
            clienteConectado = false;
        }
    }

    public synchronized void escucharServidor() {
        new Thread(() -> {
            while (clienteConectado) {
                try {
                    Paquete paqueteRecibido = (Paquete) in.readObject();

                    if (paqueteRecibido != null) {
                        System.out.println("Paquete recibido: " + paqueteRecibido.getTipo());

                        if (messageListener != null) {
                            messageListener.mensajeRecibido(paqueteRecibido);
                        } else {
                            System.err.println("Advertencia: messageListener es null, paquete no procesado.");
                        }

                        if (paqueteRecibido.getTipo() == TipoPaquete.DESCONECTAR) {
                            cerrarConexion();
                            break;
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    System.err.println("Error al recibir paquete: " + e.getMessage());
                    clienteConectado = false;
                    cerrarConexion();
                    break;
                }
            }
        }).start();
    }

    public synchronized void cerrarConexion() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();

            clienteConectado = false;

            if (messageListener != null) {
                messageListener.updateUsuariosConectados(new ArrayList<>());
            }

            System.out.println("Conexión cerrada.");

        } catch (IOException e) {
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }

    public synchronized void procesarPaquete(Paquete p) {
        switch (p.getTipo()) {
            case AUTENTICACION -> {
                autenticar(p);


            }
            case CONECTAR -> conectar(p);
            case MENSAJE -> enviarMensaje(p);
            case DESCONECTAR -> {
                System.out.println("manda desconexion en case SI");

                enviarDesconexion(p);
            }
            default -> System.out.println("Tipo de paquete no reconocido.");
        }
    }

    public boolean isClienteConectado() {
        return clienteConectado;
    }

    public void setMessageListener(PaqueteListener listener) {
        this.messageListener = listener;
    }
}
