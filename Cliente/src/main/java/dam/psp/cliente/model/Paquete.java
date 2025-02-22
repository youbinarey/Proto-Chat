package dam.psp.cliente.model;

import dam.psp.cliente.util.Network;

public abstract class Paquete {
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
