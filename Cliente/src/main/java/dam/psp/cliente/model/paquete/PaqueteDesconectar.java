package dam.psp.cliente.model.paquete;

import java.io.Serializable;

public class PaqueteDesconectar extends Paquete implements Serializable {
    private String usuario;
    public PaqueteDesconectar(String usuario) {

        super(TipoPaquete.DESCONECTAR);
        this.usuario = usuario;

    }

    public String getUsuario() {
        return usuario;
    }
}
