package dam.psp.cliente.model;

import dam.psp.cliente.controller.ConexionServidor;
import dam.psp.cliente.controller.PaqueteListener;
import dam.psp.cliente.model.paquete.*;
import dam.psp.cliente.service.OpenMeteoWeather;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Clase {@link Cliente} que representa a un usuario en el sistema de mensajería.
 * Se encarga de gestionar la comunicación con el servidor mediante paquetes.
 */
public class Cliente {

    /** Nombre del cliente (usuario). */
    private final String nickname;

    /** Instancia de la conexión con el servidor. */
    private final ConexionServidor conexionServidor;


    /**
     * Constructor de la clase {@link Cliente}.
     * @param nombre Nombre del usuario.
     * @param listener Listener para recibir paquetes del servidor.
     */
    public Cliente(String nombre, PaqueteListener listener) {
        this.nickname = nombre;
        this.conexionServidor = ConexionServidor.getInstance();
        this.conexionServidor.setMessageListener(listener);
    }

    /**
     * Envía un mensaje al servidor.
     * @param mensaje Contenido del mensaje.
     */
    public void enviarMensaje(String mensaje) {
        Paquete p = PaqueteFactory.crearPaquete(TipoPaquete.MENSAJE, this.nickname, mensaje);
        enviarPaquete(p);
    }

    /**
     * Envía un paquete de desconexión al servidor.
     */
    public void desconectar() {
        Paquete p = PaqueteFactory.crearPaquete(TipoPaquete.DESCONECTAR, this.nickname);
        enviarPaquete(p);
    }

    /**
     * Envía un paquete de conexión al servidor.
     */
    public void conectar() {
        Paquete p = PaqueteFactory.crearPaquete(TipoPaquete.CONECTAR, this.nickname);
        enviarPaquete(p);
    }

    /**
     * Envía un paquete al servidor.
     * @param paquete Paquete a enviar.
     */
    public void enviarPaquete(Paquete paquete) {
        conexionServidor.procesarPaquete(paquete);
    }

    /**
     * Obtiene la instancia de la conexión con el servidor.
     * @return Objeto {@link ConexionServidor}.
     */
    public ConexionServidor getConexionServidor() {
        return conexionServidor;
    }

    /**
     * Envía un paquete de "ping" al servidor para comprobar la conexión.
     */
    public void ping() {
        Paquete p = PaqueteFactory.crearPaquete(TipoPaquete.PING, this.nickname);
        enviarPaquete(p);
    }

    /**
     * Envía un archivo al servidor.
     * @param archivo Archivo a enviar.
     * @param tipo Tipo de archivo.
     */
    public void archivo(File archivo, String tipo) {
        try (FileInputStream fileInputStream = new FileInputStream(archivo)) {
            // Leer el contenido del archivo
            byte[] contenidoArchivo = new byte[(int) archivo.length()];
            fileInputStream.read(contenidoArchivo);

            // Crear el paquete de tipo archivo
            Paquete p = PaqueteFactory.crearPaquete(TipoPaquete.ARCHIVO, this.nickname, archivo.getName(), tipo, contenidoArchivo);

            // Enviar el paquete al servidor
            enviarPaquete(p);
            System.out.println("Archivo enviado: " + archivo.getName());
        } catch (IOException e) {
            System.err.println("Error al enviar el archivo: " + e.getMessage());
        }
    }

    /**
     * Obtiene el tipo de archivo basado en la conexión con el servidor.
     * @param file Archivo del cual obtener el tipo.
     * @return Tipo del archivo.
     */
    public String getTipoArchivo(File file) {
        return conexionServidor.getTipoArchivo(file);
    }

    /**
     * Obtiene el nombre del usuario.
     * @return Nombre del usuario.
     */
    public String getNickname() {
        return nickname;
    }

    public String getWeather (){
        return OpenMeteoWeather.getWeather();
    }

}
