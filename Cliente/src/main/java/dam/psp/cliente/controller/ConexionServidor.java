package dam.psp.cliente.controller;

import dam.psp.cliente.config.Config;
import dam.psp.cliente.model.paquete.*;
import javafx.application.Platform;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Esta clase maneja la conexión con el servidor y la comunicación entre el cliente y el servidor.
 * Proporciona métodos para conectar, autenticar, enviar mensajes, enviar archivos, y gestionar la desconexión.
 *
 * <p>La clase sigue el patrón Singleton para asegurar que solo exista una instancia de la conexión.</p>
 */
public class ConexionServidor {
    private static ConexionServidor instance;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean clienteConectado;
    private PaqueteListener messageListener;

    /**
     * Constructor privado para implementar el patrón Singleton.
     */
    private ConexionServidor() {
        clienteConectado = false;
    }

    /**
     * Obtiene la instancia única de la clase ConexionServidor.
     *
     * @return La instancia única de ConexionServidor.
     */
    public static ConexionServidor getInstance() {
        if (instance == null) {
            instance = new ConexionServidor();
        }
        return instance;
    }

    // Métodos relacionados con la conexión y autenticación

    /**
     * Conecta con el servidor y envía un paquete de conexión.
     *
     * @param p El paquete de conexión a enviar.
     */
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

    /**
     * Autentica al cliente en el servidor.
     *
     * @param p El paquete de autenticación a enviar.
     * @return {@code true} si la autenticación fue exitosa, {@code false} en caso contrario.

     */
    public synchronized Boolean autenticar(Paquete p) {
        if (clienteConectado) {
            System.err.println("Ya hay una sesión iniciada. No es necesario autenticar nuevamente.");
            return null;
        }

        try {
            socket = new Socket(Config.SERVER_IP, Config.SERVER_PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());

            System.out.println("Enviando paquete de autenticación...");
            out.writeObject(p);
            out.flush();


            Object response = in.readObject();
            if (response instanceof Boolean acceso) {
                if (acceso) {
                    System.out.println("Autenticación exitosa.");
                    return true;
                } else {
                    System.out.println("Autenticación fallida.");
                    return false;
                }
            } else if (response instanceof PaqueteError paqueteError) {
                System.err.println("Error de autenticación: " + paqueteError.getUsuario() + " ya está en el chat");
                return false;
            } else {
                System.err.println("Respuesta desconocida del servidor.");
                return false;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error en autenticación: " + e.getMessage());
            cerrarConexion();
        }
        return null;
    }

    // Métodos relacionados con el envío de mensajes y archivos

    /**
     * Envía un mensaje al servidor.
     *
     * @param p El paquete de mensaje a enviar.
     */
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

    /**
     * Envía un paquete de desconexión al servidor.
     *
     * @param p El paquete de desconexión a enviar.
     */
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
            System.out.println("Paquete desconectar enviado.");

        } catch (IOException e) {
            System.err.println("Error al enviar mensaje: " + e.getMessage());
            clienteConectado = false;
        }
    }

    /**
     * Envía un paquete PING al servidor.
     *
     * @param p El paquete PING a enviar.
     */
    public void enviarPING(Paquete p) {
        try {
            out.writeObject(p);
            out.flush();
            System.out.println("Ping enviado al servidor.");
        } catch (IOException e) {
            System.err.println("Error al enviar el ping: " + e.getMessage());
        }
    }

    /**
     * Envía un archivo al servidor.
     *
     * @param p El paquete de archivo a enviar.
     */
    public void enviarArchivo(Paquete p) {
        try {
            out.writeObject(p);
            out.flush();
            System.out.println("Archivo enviado al servidor.");
        } catch (IOException e) {
            System.err.println("Error al enviar el archivo: " + e.getMessage());
        }
    }

    // Métodos relacionados con la recepción de datos

    /**
     * Escucha las respuestas del servidor en un hilo separado.
     */
    public synchronized void escucharServidor() {
        new Thread(() -> {
            while (clienteConectado) {
                try {
                    Object objetoRecibido = in.readObject();

                    if (objetoRecibido instanceof Paquete paqueteRecibido) {
                        System.out.println("Paquete recibido: " + paqueteRecibido.getTipo());

                        if (messageListener != null) {
                            messageListener.mensajeRecibido(paqueteRecibido);
                        } else {
                            System.err.println("Advertencia: messageListener es null, paquete no procesado.");
                        }

                        if (paqueteRecibido.getTipo() == TipoPaquete.DESCONECTAR) {
                            System.out.println("Servidor autoriza desconexión");
                            cerrarConexion();
                            break; // Rompe el bucle de escucha
                        }

                    } else if (objetoRecibido instanceof List<?> listaUsuarios) {
                        System.out.println("Lista de usuarios recibida: " + listaUsuarios);
                        messageListener.updateUsuariosConectados((List<String>) listaUsuarios);
                    } else {
                        System.err.println("Objeto recibido de tipo desconocido: " + objetoRecibido.getClass().getName());
                    }
                } catch (IOException | ClassNotFoundException e) {
                    System.err.println("Error al recibir objeto: " + e.getMessage());

                    clienteConectado = false;
                    cerrarConexion();

                    // Notificar al ClienteController que se ha desconectado
                    if (messageListener != null) {
                        Platform.runLater(() -> {
                            ((ClienteController) messageListener).onDesconexionServidor();
                        });
                    }
                    break;
                }
            }
        }).start();
    }

    // Métodos relacionados con el cierre de la conexión

    /**
     * Cierra la conexión con el servidor.
     */
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

    // Métodos auxiliares

    /**
     * Procesa un paquete recibido y realiza la acción correspondiente según el tipo de paquete.
     *
     * Este método identifica el tipo de paquete y llama al método correspondiente para procesarlo,
     * ya sea para autenticar, conectar, enviar mensajes, enviar archivos, etc.
     *
     * @param p El paquete a procesar. No debe ser {@code null}.
     */
    public synchronized void procesarPaquete(Paquete p) {
        if (p == null) {
            System.err.println("Advertencia: Se recibió un paquete nulo.");
            return; // Evita que el programa crashee
        }
        switch (p.getTipo()) {
            case AUTENTICACION -> autenticar(p); // Autentica al usuario
            case CONECTAR -> conectar(p); // Establece la conexión
            case MENSAJE -> enviarMensaje(p); // Envía un mensaje
            case PING -> enviarPING(p); // Envía un ping al servidor
            case DESCONECTAR -> enviarDesconexion(p); // Envia solicitud de desconexión
            case ARCHIVO -> enviarArchivo(p); // Envía un archivo
            default -> System.out.println("Tipo de paquete no reconocido.");
        }
    }

    /**
     * Establece el listener para recibir notificaciones de mensajes.
     *
     * @param listener El listener a establecer. Este debe implementar la interfaz {@code PaqueteListener}.
     */
    public void setMessageListener(PaqueteListener listener) {
        this.messageListener = listener;
    }

    /**
     * Obtiene el tipo de archivo basado en su extensión.
     *
     * @param archivo El archivo del cual se desea obtener el tipo. No debe ser {@code null}.
     * @return El tipo de archivo ("imagen", "documento" o "archivo").
     */
    public String getTipoArchivo(File archivo) {
        String nombreArchivo = archivo.getName().toLowerCase();
        if (nombreArchivo.endsWith(".png") || nombreArchivo.endsWith(".jpg") || nombreArchivo.endsWith(".jpeg") || nombreArchivo.endsWith(".gif")) {
            return "imagen"; // Si el archivo es una imagen
        } else if (nombreArchivo.endsWith(".pdf") || nombreArchivo.endsWith(".docx") || nombreArchivo.endsWith(".txt")) {
            return "documento"; // Si el archivo es un documento
        } else {
            return "archivo"; // Si el archivo es otro tipo
        }
    }
}
