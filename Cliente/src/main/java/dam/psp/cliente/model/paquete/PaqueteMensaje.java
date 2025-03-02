package dam.psp.cliente.model.paquete;

import java.io.Serializable;

/**
 * Clase que representa un paquete de mensaje.
 * Esta clase extiende de {@link Paquete} y contiene la información necesaria para enviar un mensaje entre usuarios.
 */
public class PaqueteMensaje extends Paquete implements Serializable {

    /** Remitente del mensaje. */
    private String remitente;

    /** Contenido del mensaje. */
    private String mensaje;

    /**
     * Constructor de la clase PaqueteMensaje.
     * Inicializa el remitente y el mensaje, y establece el tipo de paquete como {@link TipoPaquete#MENSAJE}.
     *
     * @param remitente El nombre del remitente del mensaje.
     * @param mensaje El contenido del mensaje.
     */
    public PaqueteMensaje(String remitente, String mensaje){
        super(TipoPaquete.MENSAJE); // Establece el tipo de paquete como MENSAJE.
        this.remitente = remitente;
        this.mensaje = mensaje;
    }

    /**
     * Obtiene el nombre del remitente del mensaje.
     *
     * @return El nombre del remitente.
     */
    public String getRemitente() {
        return remitente;
    }

    /**
     * Obtiene el contenido del mensaje.
     *
     * @return El contenido del mensaje.
     */
    public String getMensaje() {
        return mensaje;
    }
}
