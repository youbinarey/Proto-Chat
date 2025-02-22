package dam.psp.cliente.model;


import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Paquetes implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String remitente;
    private String destinatario;
    private String ip;
    private String mensajeCliente;
    private TipoPaquete tipo;
    private List<String> listaUsuarios;

    public Paquetes(){

    }

    public Paquetes(String remitente, String destinatario, String ip, String mensajeCliente, TipoPaquete tipo) {
        this.remitente = remitente;
        this.destinatario = destinatario;
        this.ip = ip;
        this.mensajeCliente = mensajeCliente;
        this.tipo = tipo;
        listaUsuarios = new ArrayList<>();
    }

    public String getRemitente() {
        return remitente;
    }

    public void setRemitente(String remitente) {
        this.remitente = remitente;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
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

    public List<String> getListaUsuarios() {
        return listaUsuarios;
    }

    public void setListaUsuarios(List<String> listaUsuarios) {
        this.listaUsuarios = listaUsuarios;
    }
}


