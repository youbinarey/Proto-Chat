package dam.psp.cliente.model.paquete;

/**
 * Clase de fábrica para la creación de diferentes tipos de paquetes.
 * Esta clase proporciona métodos estáticos para crear paquetes de tipos específicos según el tipo proporcionado.
 * Los tipos de paquetes incluyen autenticación, conexión, desconexión, ping, notificación, mensaje y archivo.
 */
public class PaqueteFactory {

    /**
     * Crea un paquete según el tipo especificado.
     *
     * @param tipo El tipo de paquete que se desea crear.
     * @param parametros Parámetros adicionales que serán utilizados en la creación del paquete.
     * @return Un objeto de tipo {@link Paquete} correspondiente al tipo y parámetros proporcionados.
     * @throws IllegalArgumentException Si el tipo de paquete no es válido.
     */
    public static Paquete crearPaquete(TipoPaquete tipo, Object... parametros){
        switch (tipo){
            case AUTENTICACION -> {return crearPaqueteAutenticacion(parametros);}
            case CONECTAR -> {return crearPaqueteConectar(parametros);}
            case PING -> {return crearPaquetePING(parametros);}
            case DESCONECTAR -> {return crearPaqueteDesconectar(parametros);}
            case NOTIFICACION -> {return crearPaqueteNotificacion(parametros);}
            case MENSAJE -> {return crearPaqueteMensaje(parametros);}
            case ARCHIVO -> {return  crearPaqueteArchivo(parametros);}
            case ERROR -> {
                return crearPaqueteError(parametros);
            }
            default -> throw new IllegalArgumentException("Tipo de paquete no válido: " + tipo);
        }
    }

    private static Paquete crearPaqueteError(Object[] parametros) {
        String usuario = (String) parametros[0];
        return new PaqueteError(usuario);
    }

    /**
     * Crea un paquete de tipo archivo con los parámetros proporcionados.
     *
     * @param parametros Parámetros necesarios para crear el paquete de archivo: usuario, nombre del archivo, tipo de archivo y contenido.
     * @return Un objeto de tipo {@link PaqueteArchivo}.
     */
    private static Paquete crearPaqueteArchivo(Object[] parametros) {
        String usuario = (String) parametros[0];
        String nombre = (String) parametros[1];
        String tipoArchivo = (String) parametros[2];
        byte[] contenido = (byte[]) parametros[3];
        return new PaqueteArchivo(usuario, nombre, tipoArchivo, contenido);
    }

    /**
     * Crea un paquete de tipo mensaje con los parámetros proporcionados.
     *
     * @param parametros Parámetros necesarios para crear el paquete de mensaje: remitente y mensaje.
     * @return Un objeto de tipo {@link PaqueteMensaje}.
     */
    private static Paquete crearPaqueteMensaje(Object... parametros) {
        String remitente = (String) parametros[0];
        String mensaje = (String) parametros[1];
        return new PaqueteMensaje(remitente, mensaje);
    }

    /**
     * Crea un paquete de tipo notificación con los parámetros proporcionados.
     *
     * @param parametros Parámetros necesarios para crear el paquete de notificación: usuario y evento.
     * @return Un objeto de tipo {@link PaqueteNotificacion}.
     */
    private static Paquete crearPaqueteNotificacion(Object... parametros) {
        String usuario = (String) parametros[0];
        String evento = (String) parametros[1];
        return new PaqueteNotificacion(usuario, evento);
    }

    /**
     * Crea un paquete de tipo desconexión con los parámetros proporcionados.
     *
     * @param parametros Parámetros necesarios para crear el paquete de desconexión: usuario.
     * @return Un objeto de tipo {@link PaqueteDesconectar}.
     */
    private static Paquete crearPaqueteDesconectar(Object... parametros) {
        String usuario = (String) parametros[0];
        return new PaqueteDesconectar(usuario);
    }

    /**
     * Crea un paquete de tipo ping con los parámetros proporcionados.
     *
     * @param parametros Parámetros necesarios para crear el paquete de ping: usuario.
     * @return Un objeto de tipo {@link PaquetePing}.
     */
    private static Paquete crearPaquetePING(Object... parametros) {
        String usuario = (String) parametros[0];
        return new PaquetePing(usuario);
    }

    /**
     * Crea un paquete de tipo conexión con los parámetros proporcionados.
     *
     * @param parametros Parámetros necesarios para crear el paquete de conexión: usuario.
     * @return Un objeto de tipo {@link PaqueteConectar}.
     */
    private static Paquete crearPaqueteConectar(Object... parametros) {
        String usuario = (String) parametros[0];
        return new PaqueteConectar(usuario);
    }

    /**
     * Crea un paquete de tipo autenticación con los parámetros proporcionados.
     *
     * @param parametros Parámetros necesarios para crear el paquete de autenticación: usuario y contraseña.
     * @return Un objeto de tipo {@link PaqueteAutenticacion}.
     */
    private static Paquete crearPaqueteAutenticacion(Object... parametros) {
        String usuario = (String) parametros[0];
        String password = (String) parametros[1];
        return new PaqueteAutenticacion(usuario, password);
    }
}
