package dam.psp.cliente.model.paquete;

import java.io.Serializable;

//Esta clase notificara a todos acciones informativas que se han de plasmar en unhilo independiente de todos los clientes
public class PaqueteNotificacion extends Paquete implements Serializable {
    private String evento;

    public PaqueteNotificacion(String evento){
        super(TipoPaquete.NOTIFICACION);
        this.evento = evento;
    }

    public String getEvento() {
        return evento;
    }
}
