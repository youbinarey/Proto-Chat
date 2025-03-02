package dam.psp.cliente.model.paquete;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa un paquete genérico en la aplicación.
 * <p>
 * Esta clase fue parte del desarrollo original de la aplicación, pero ha quedado en desuso debido a la escalabilidad y modularización
 * de los tipos de paquetes. En versiones posteriores, se separaron los tipos de paquetes en clases específicas,
 * lo que ha hecho que esta clase ya no se utilice más. Sin embargo, fue parte del desarrollo inicial y puede encontrarse
 * en versiones antiguas del código.
 * </p>
 */
public class Paquetes implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /** Remitente del paquete. */
    private String remitente;

    /** Destinatario del paquete. */
    private String destinatario;

    /** Dirección IP asociada al paquete. */
    private String ip;

    /** Mensaje del cliente asociado al paquete. */
    private String mensajeCliente;

    /** Tipo de paquete. */
    private TipoPaquete tipo;

    /** Lista de usuarios relacionados con el paquete. */
    private List<String> listaUsuarios;

    /**
     * Constructor vacío para la clase Paquetes.
     * Este constructor está presente para cumplir con los requisitos de serialización.
     */
    public Paquetes(){
    }

    /**
     * Constructor de la clase Paquetes.
     *
     * @param remitente El remitente del paquete.
     * @param destinatario El destinatario del paquete.
     * @param ip La dirección IP asociada al paquete.
     * @param mensajeCliente El mensaje del cliente.
     * @param tipo El tipo de paquete.
     */
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
