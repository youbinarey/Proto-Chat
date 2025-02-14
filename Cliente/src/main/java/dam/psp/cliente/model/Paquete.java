package dam.psp.cliente.model;


public class Paquete {
    private String nombreCliente;
    private String ip;
    private String mensajeCliente;
    private TipoPaquete tipo;

    public Paquete(){

    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMensajeCliente() {
        return mensajeCliente;
    }

    public void setMensajeCliente(String mensajeCliente) {
        this.mensajeCliente = mensajeCliente;
    }

    public TipoPaquete getTipo() {
        return tipo;
    }

    public void setTipo(TipoPaquete tipo) {
        this.tipo = tipo;
    }



}


