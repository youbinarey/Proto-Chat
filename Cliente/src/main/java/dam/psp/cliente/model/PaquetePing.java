package dam.psp.cliente.model;

import dam.psp.cliente.util.Network;

import java.time.LocalTime;

public class PaquetePing extends  Paquete{

    private LocalTime time;

    public PaquetePing() {
        super(TipoPaquete.PING);
        this.time = LocalTime.now();
    }

    public LocalTime getTime() {
        return time;
    }
}
