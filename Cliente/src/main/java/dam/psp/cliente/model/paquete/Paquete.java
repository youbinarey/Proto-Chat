package dam.psp.cliente.model.paquete;

import dam.psp.cliente.util.Network;

import java.io.Serializable;

public abstract class Paquete implements Serializable {
    protected final String IP;
    protected final TipoPaquete tipo;


    public Paquete(TipoPaquete tipo) {
        this.IP = Network.getMyIp();
        this.tipo = tipo;
    }


    public  TipoPaquete getTipo(){
        return tipo;
    }


    public String getIP() {
        return IP;
    }
}
