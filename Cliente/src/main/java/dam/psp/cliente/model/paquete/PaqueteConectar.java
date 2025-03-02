package dam.psp.cliente.model.paquete;

import java.io.Serializable;

/**
 * Clase que representa un paquete de conexión.
 * Esta clase extiende de {@link Paquete} y contiene la información necesaria para conectar a un usuario al sistema.
 */
public class PaqueteConectar extends Paquete implements Serializable {

    /** Nombre de usuario para la conexión. */
    private String usuario;

    /**
     * Constructor de la clase PaqueteConectar.
     * Inicializa el atributo usuario y establece el tipo de paquete como {@link TipoPaquete#CONECTAR}.
     *
     * @param usuario Nombre del usuario que se conecta al sistema.
     */
    public PaqueteConectar(String usuario) {
        super(TipoPaquete.CONECTAR); // Se establece el tipo de paquete como CONECTAR.
        this.usuario = usuario;
    }

    /**
     * Obtiene el nombre del usuario que se conecta al sistema.
     *
     * @return Nombre del usuario.
     */
    public String getUsuario() {
        return usuario;
    }
}
