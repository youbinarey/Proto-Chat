package dam.psp.cliente.model.paquete;

import java.io.Serializable;
import java.time.LocalTime;

public class PaquetePing extends  Paquete implements Serializable {

    private LocalTime time;

    public PaquetePing() {
        super(TipoPaquete.PING);
        this.time = LocalTime.now();
    }

    public LocalTime getTime() {
        return time;
    }
}
