package dam.psp.cliente.model.paquete;

import java.io.Serializable;

//Esta clase notificara a todos acciones informativas que se han de plasmar en unhilo independiente de todos los clientes
public class PaqueteNotificacion extends Paquete implements Serializable {
    private String evento;
    private String usuario;

    public PaqueteNotificacion(String usuario,String evento){
        super(TipoPaquete.NOTIFICACION);
        this.evento = evento;
        this.usuario = usuario;
    }

    public String getEvento() {
        return evento;
    }

    public String getUsuario() {
        return usuario;
    }
}
