package dam.psp.cliente.model.paquete;

/**
 * Enum que representa los diferentes tipos de paquetes en la aplicación.
 * <p>
 * Cada valor de este enum corresponde a un tipo específico de paquete que puede ser enviado
 * o recibido dentro de la aplicación, y se utiliza para clasificar y manejar los paquetes de manera adecuada.
 * </p>
 */
public enum TipoPaquete {
    CONECTAR,
    MENSAJE,
    PING,
    ARCHIVO,
    NOTIFICACION,
    ERROR,
    AUTENTICACION,
    DESCONECTAR

}
