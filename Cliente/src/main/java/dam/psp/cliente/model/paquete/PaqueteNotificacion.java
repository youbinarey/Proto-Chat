package dam.psp.cliente.model.paquete;

import java.io.Serializable;

/**
 * Clase que representa un paquete de notificación.
 * Esta clase extiende de {@link Paquete} y se utiliza para enviar eventos informativos a todos los clientes,
 * que se deben procesar en un hilo independiente.
 */
public class PaqueteNotificacion extends Paquete implements Serializable {

    /** Evento que describe la notificación. */
    private String evento;

    /** Usuario asociado a la notificación. */
    private String usuario;

    /**
     * Constructor de la clase PaqueteNotificacion.
     * Inicializa el evento y el usuario, y establece el tipo de paquete como {@link TipoPaquete#NOTIFICACION}.
     *
     * @param usuario El nombre del usuario relacionado con la notificación.
     * @param evento La descripción del evento informativo.
     */
    public PaqueteNotificacion(String usuario, String evento){
        super(TipoPaquete.NOTIFICACION); // Establece el tipo de paquete como NOTIFICACION.
        this.evento = evento;
        this.usuario = usuario;
    }

    /**
     * Obtiene el evento asociado a la notificación.
     *
     * @return El evento informativo.
     */
    public String getEvento() {
        return evento;
    }

    /**
     * Obtiene el usuario asociado a la notificación.
     *
     * @return El nombre del usuario relacionado con la notificación.
     */
    public String getUsuario() {
        return usuario;
    }
}
