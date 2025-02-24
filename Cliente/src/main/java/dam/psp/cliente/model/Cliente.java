package dam.psp.cliente.model;



import dam.psp.cliente.controller.ConexionServidor;
import dam.psp.cliente.controller.PaqueteListener;



public class Cliente  {
    private final String nickname;

    private final ConexionServidor conexionServidor;
    private Paquetes p;



    public Cliente(String nombre, PaqueteListener listener) {
        this.nickname = nombre;
        p = new Paquetes();
        this.p.setRemitente(this.nickname);

        conexionServidor = ConexionServidor.getInstance();
        conexionServidor.setMessageListener(listener);

    }

    public void enviarMensaje(String mensaje){
        p.setTipo(TipoPaquete.MENSAJE);
        p.setMensajeCliente(mensaje);
        p.setDestinatario("TODOS");
        enviarPaquete(p);
    }

    public void enviarPaquete(Paquetes p) {
        conexionServidor.procesarPaquete(p);
    }


    public void desconectar(){
        p.setTipo(TipoPaquete.DESCONECTAR);
        enviarPaquete(p);
    }

    private void resetPaquete(){
        p.setTipo(null);
        p.setMensajeCliente(null);
    }

    public void conectar(){
        this.p.setTipo(TipoPaquete.CONECTAR);
        enviarPaquete(p);
    }



}