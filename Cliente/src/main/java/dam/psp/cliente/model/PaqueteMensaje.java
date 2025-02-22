package dam.psp.cliente.model;

import dam.psp.cliente.util.Network;

public class PaqueteMensaje extends  Paquete{
    private  String remitente;
    private String mensaje;

    public PaqueteMensaje(String remitente, String mensaje){
        super(TipoPaquete.MENSAJE);
        this.remitente = remitente;
        this.mensaje = mensaje;
    }

    public String getRemitente() {
        return remitente;
    }

    public String getMensaje() {
        return mensaje;
    }
}
