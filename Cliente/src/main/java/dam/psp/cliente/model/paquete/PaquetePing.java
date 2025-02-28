package dam.psp.cliente.model.paquete;

import java.io.Serializable;
import java.time.LocalTime;

public class PaquetePing extends  Paquete implements Serializable {

    private long timestamp;
    private String usuario;

    public PaquetePing(String usuario) {
        super(TipoPaquete.PING);
        this.usuario = usuario;
        this.timestamp = System.currentTimeMillis();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getUsuario() {
        return usuario;
    }
}
