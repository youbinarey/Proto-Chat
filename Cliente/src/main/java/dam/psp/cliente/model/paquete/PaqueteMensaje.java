package dam.psp.cliente.model.paquete;

import java.io.Serializable;

public class PaqueteMensaje extends  Paquete implements Serializable {
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
