package dam.psp.cliente.model.paquete;

import java.io.Serializable;

/**
 * Clase que representa un paquete de tipo archivo.
 * Esta clase extiende de {@link Paquete} y contiene la información relacionada con un archivo que se envía entre el cliente y el servidor.
 */
public class PaqueteArchivo extends Paquete implements Serializable {

    /** Nombre del usuario que envía el archivo. */
    private String usuario;

    /** Nombre del archivo. */
    private String nombre;

    /** Tipo de archivo (por ejemplo, "imagen", "texto"). */
    private String tipoArchivo;

    /** Contenido del archivo en formato de bytes. */
    private byte[] contenido;

    /**
     * Constructor de la clase PaqueteArchivo.
     *
     * @param usuario Nombre del usuario que envía el archivo.
     * @param nombre Nombre del archivo.
     * @param tipoArchivo Tipo de archivo (por ejemplo, "pdf", "jpg").
     * @param contenido Contenido del archivo en bytes.
     */
    public PaqueteArchivo(String usuario, String nombre, String tipoArchivo, byte[] contenido) {
        super(TipoPaquete.ARCHIVO); // Se establece el tipo de paquete como "ARCHIVO".
        this.usuario = usuario;
        this.nombre = nombre;
        this.tipoArchivo = tipoArchivo;
        this.contenido = contenido;
    }

    /**
     * Obtiene el nombre del usuario que envía el archivo.
     *
     * @return Nombre del usuario.
     */
    public String getUsuario() {
        return usuario;
    }

    /**
     * Obtiene el nombre del archivo.
     *
     * @return Nombre del archivo.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Obtiene el tipo de archivo.
     *
     * @return Tipo de archivo (por ejemplo, "jpg", "txt").
     */
    public String getTipoArchivo() {
        return tipoArchivo;
    }

    /**
     * Obtiene el contenido del archivo en formato de bytes.
     *
     * @return Contenido del archivo.
     */
    public byte[] getContenido() {
        return contenido;
    }
}
