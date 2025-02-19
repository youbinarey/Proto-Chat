package dam.psp.cliente.model;



import dam.psp.cliente.controller.ConexionServidor;
import dam.psp.cliente.controller.PaqueteListener;

import java.time.LocalTime;
import java.util.Scanner;


public class Cliente  {
    private final String nickname;

    private final ConexionServidor conexionServidor;
    private Paquete p;



    public Cliente(String nombre, PaqueteListener listener) {
        this.nickname = nombre;
        p = new Paquete();
        this.p.setRemitente(this.nickname);
        this.p.setMensajeCliente("Prueba " + LocalTime.now());
        conexionServidor = ConexionServidor.getInstance();
        conexionServidor.setMessageListener(listener);

    }

    public void enviarMensaje(String mensaje){
        p.setTipo(TipoPaquete.MENSAJE);
        p.setMensajeCliente(mensaje);
        p.setDestinatario("TODOS");
        conexionServidor.procesarPaquete(p);
    }

    public void enviarPaquete(Paquete p) {

        conexionServidor.procesarPaquete(p);

    }


    public void desconectar(){
        p.setTipo(TipoPaquete.DESCONECTAR);
        conexionServidor.procesarPaquete(p);
    }






    private void resetPaquete(){
        p.setTipo(null);
        p.setMensajeCliente(null);
    }

    public void conectar(){
        this.p.setTipo(TipoPaquete.CONECTAR);
        conexionServidor.procesarPaquete(p);
    }



}
