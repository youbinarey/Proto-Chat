package dam.psp.cliente.model.paquete;

import java.io.Serializable;

/**
 * Clase que representa un paquete de desconexión.
 * Esta clase extiende de {@link Paquete} y contiene la información necesaria para desconectar a un usuario del sistema.
 */
public class PaqueteDesconectar extends Paquete implements Serializable {

    /** Nombre del usuario para la desconexión. */
    private String usuario;

    /**
     * Constructor de la clase PaqueteDesconectar.
     * Inicializa el atributo usuario y establece el tipo de paquete como {@link TipoPaquete#DESCONECTAR}.
     *
     * @param usuario Nombre del usuario que se desconecta del sistema.
     */
    public PaqueteDesconectar(String usuario) {
        super(TipoPaquete.DESCONECTAR); // Se establece el tipo de paquete como DESCONECTAR.
        this.usuario = usuario;
    }

    /**
     * Obtiene el nombre del usuario que se desconecta del sistema.
     *
     * @return Nombre del usuario.
     */
    public String getUsuario() {
        return usuario;
    }
}
