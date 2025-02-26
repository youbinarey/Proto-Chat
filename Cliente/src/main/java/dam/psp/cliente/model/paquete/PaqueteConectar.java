package dam.psp.cliente.model.paquete;

import java.io.Serializable;

public class PaqueteConectar extends Paquete implements Serializable {
    private String usuario;
    public PaqueteConectar(String usuario) {

        super(TipoPaquete.CONECTAR);
        this.usuario = usuario;
    }

    public String getUsuario() {
        return usuario;
    }
}
