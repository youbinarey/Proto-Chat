package dam.psp.cliente.model;

import dam.psp.cliente.controller.ConexionServidor;
import dam.psp.cliente.controller.PaqueteListener;
import dam.psp.cliente.model.paquete.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Cliente {
    private final String nickname;
    private final ConexionServidor conexionServidor;

    public Cliente(String nombre, PaqueteListener listener) {
        this.nickname = nombre;
        this.conexionServidor = ConexionServidor.getInstance();
        this.conexionServidor.setMessageListener(listener);
    }


    public void enviarMensaje(String mensaje) {
        Paquete p = PaqueteFactory.crearPaquete(TipoPaquete.MENSAJE, this.nickname, mensaje);
        enviarPaquete(p);
    }

    public void desconectar() {
        Paquete p = PaqueteFactory.crearPaquete(TipoPaquete.DESCONECTAR, this.nickname);
        enviarPaquete(p);

    }

    public void conectar() {
        Paquete p = PaqueteFactory.crearPaquete(TipoPaquete.CONECTAR, this.nickname);
        enviarPaquete(p);


    }

    public void enviarPaquete(Paquete paquete) {

        conexionServidor.procesarPaquete(paquete);
    }

    public String getNickname() {
        return nickname;
    }

    public ConexionServidor getConexionServidor() {
        return conexionServidor;
    }

    public void ping() {
        Paquete p = PaqueteFactory.crearPaquete(TipoPaquete.PING, this.nickname);

        enviarPaquete(p);

    }

    public void archivo(File archivo, String tipo) {
        try (FileInputStream fileInputStream = new FileInputStream(archivo)) {
            // Leer el contenido del archivo
            byte[] contenidoArchivo = new byte[(int) archivo.length()];
            fileInputStream.read(contenidoArchivo);

            // Crear el paquete de tipo archivo
            Paquete p = PaqueteFactory.crearPaquete(TipoPaquete.ARCHIVO,this.nickname, archivo.getName(), tipo, contenidoArchivo);

            // Enviar el paquete al servidor
            enviarPaquete(p);

            System.out.println("Archivo enviado: " + archivo.getName());
        } catch (IOException e) {
            System.err.println("Error al enviar el archivo: " + e.getMessage());
        }
    }

    public String getTipoArchivo(File file){
        return conexionServidor.getTipoArchivo(file);
    }
    /*
    public void autenticar(String usuario, String password) {
        Paquete p = PaqueteFactory.crearPaquete(TipoPaquete.AUTENTICACION, usuario, password);

        if (conexionServidor.autenticar(p)) {
            conectar();
        }
    }

     */

}