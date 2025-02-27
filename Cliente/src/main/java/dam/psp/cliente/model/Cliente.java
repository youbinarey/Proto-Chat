package dam.psp.cliente.model;

import dam.psp.cliente.controller.ConexionServidor;
import dam.psp.cliente.controller.PaqueteListener;
import dam.psp.cliente.model.paquete.*;

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

    /*
    public void autenticar(String usuario, String password) {
        Paquete p = PaqueteFactory.crearPaquete(TipoPaquete.AUTENTICACION, usuario, password);

        if (conexionServidor.autenticar(p)) {
            conectar();
        }
    }

     */

}