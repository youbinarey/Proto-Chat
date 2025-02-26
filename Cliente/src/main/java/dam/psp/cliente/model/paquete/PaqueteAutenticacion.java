package dam.psp.cliente.model.paquete;

import java.io.Serializable;

public class PaqueteAutenticacion extends Paquete implements Serializable {

    private  String usuario;
    private String password;


    public PaqueteAutenticacion(String usuario, String password){
        super(TipoPaquete.AUTENTICACION);
        this.usuario = usuario;
        this.password = password;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getPassword() {
        return password;
    }
}

