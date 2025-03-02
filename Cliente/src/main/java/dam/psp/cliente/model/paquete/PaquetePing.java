package dam.psp.cliente.model.paquete;

import java.io.Serializable;

/**
 * Clase que representa un paquete de tipo "ping".
 * Esta clase extiende de {@link Paquete} y se utiliza para verificar la conexión entre el cliente y el servidor.
 * Contiene un timestamp que marca el momento en que el paquete fue enviado.
 */
public class PaquetePing extends Paquete implements Serializable {

    /** Marca de tiempo cuando se envía el paquete. */
    private long timestamp;

    /** Usuario que envía el ping. */
    private String usuario;

    /**
     * Constructor de la clase PaquetePing.
     * Inicializa el usuario que envía el ping y establece la marca de tiempo del paquete como el tiempo actual.
     * Establece el tipo de paquete como {@link TipoPaquete#PING}.
     *
     * @param usuario El nombre del usuario que envía el ping.
     */
    public PaquetePing(String usuario) {
        super(TipoPaquete.PING); // Establece el tipo de paquete como PING.
        this.usuario = usuario;
        this.timestamp = System.currentTimeMillis(); // Marca el tiempo de envío.
    }


    public long getTimestamp() {
        return timestamp;
    }

    public String getUsuario() {
        return usuario;
    }
}
