package dam.psp.cliente.model.paquete;

import java.io.Serializable;

public class PaqueteArchivo extends  Paquete implements Serializable {
    private String usuario;
    private String nombre;
    private String tipoArchivo;
    private byte[] contenido;

    public PaqueteArchivo(String usuario,String nombre, String tipoArchivo, byte[] contenido) {
        super(TipoPaquete.ARCHIVO);
        this.usuario = usuario;
        this.nombre = nombre;
        this.tipoArchivo = tipoArchivo;
        this.contenido = contenido;

    }

    public String getUsuario() {
        return usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipoArchivo() {
        return tipoArchivo;
    }

    public byte[] getContenido() {
        return contenido;
    }
}
