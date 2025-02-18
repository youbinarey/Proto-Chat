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
    public void recibirPaquete(){
    }

    public void desconectar(){
        //TODO
    }

    public void setTipoPaquete(String tipo,Scanner sc){
        switch (tipo){
            case "1" -> p.setTipo(TipoPaquete.CONECTAR);
            case "2" -> crearMesaje(sc);
            case "3" -> p.setTipo(TipoPaquete.ARCHIVO);
            case "4" -> p.setTipo(TipoPaquete.NOTIFICACION);
            case "5" -> p.setTipo(TipoPaquete.AUTENTICACION);
            case "0" -> p.setTipo(TipoPaquete.DESCONECTAR);
            default -> System.out.println
                    ("Tipo de Paquete no reconocido");
        }

    }

    public void crearMesaje(Scanner sc){
        String opt = "";
        System.out.println("<- Escribe aqui: ");


        while(true){
            p.setTipo(TipoPaquete.MENSAJE);
            opt = sc.nextLine();
            if(opt.equals(".")) break;

            p.setMensajeCliente(opt);
            //System.out.println("<- Mensje enviado");
            conexionServidor.procesarPaquete(p);
        };

        resetPaquete();
    }


    public void showMenu(){
        System.out.println("1. CONECTAR");
        System.out.println("2. MENSAJE");
        System.out.println("3. ARCHIVO");
        System.out.println("4. NOTIFICACION");
        System.out.println("5. AUTENTICACION");
        System.out.println("0. DESCONECTAR");


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
