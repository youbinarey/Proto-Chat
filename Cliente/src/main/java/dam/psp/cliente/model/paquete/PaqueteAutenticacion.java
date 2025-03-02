package dam.psp.cliente.model.paquete;

import java.io.Serializable;

/**
 * Clase que representa un paquete de autenticación.
 * Esta clase extiende de {@link Paquete} y contiene la información necesaria para autenticar a un usuario en el sistema.
 */
public class PaqueteAutenticacion extends Paquete implements Serializable {

    /** Nombre de usuario para autenticación. */
    private String usuario;

    /** Contraseña del usuario para autenticación. */
    private String password;

    /**
     * Constructor de la clase PaqueteAutenticacion.
     * Inicializa los atributos de usuario y contraseña y establece el tipo de paquete como {@link TipoPaquete#AUTENTICACION}.
     *
     * @param usuario Nombre del usuario para autenticarse.
     * @param password Contraseña del usuario.
     */
    public PaqueteAutenticacion(String usuario, String password) {
        super(TipoPaquete.AUTENTICACION); // Se establece el tipo de paquete como AUTENTICACION.
        this.usuario = usuario;
        this.password = password;
    }

    /**
     * Obtiene el nombre de usuario para la autenticación.
     *
     * @return Nombre de usuario.
     */
    public String getUsuario() {
        return usuario;
    }

    /**
     * Obtiene la contraseña del usuario para la autenticación.
     *
     * @return Contraseña del usuario.
     */
    public String getPassword() {
        return password;
    }
}
