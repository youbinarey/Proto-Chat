package dam.psp.cliente.model;

public class PaqueteNotificacion extends Paquete{
    private String evento;

    public PaqueteNotificacion(String evento){
        super(TipoPaquete.NOTIFICACION);
        this.evento = evento;
    }

    public String getEvento() {
        return evento;
    }
}
