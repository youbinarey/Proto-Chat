package dam.psp.cliente.model.paquete;

import java.io.Serializable;

/**
 * Clase que representa un paquete de conexión.
 * Esta clase extiende de {@link Paquete} y contiene la información necesaria para conectar a un usuario al sistema.
 */
public class PaqueteError extends Paquete implements Serializable {

    /** Nombre de usuario para la conexión. */
    private String usuario;


    public PaqueteError(String usuario) {
        super(TipoPaquete.ERROR); // Se establece el tipo de paquete como CONECTAR.
        this.usuario = usuario;
    }


    public String getUsuario() {
        return usuario;
    }
}
