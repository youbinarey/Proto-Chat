package dam.psp.cliente.model;

import dam.psp.cliente.util.Network;

public class PaqueteAutenticacion extends Paquete {

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

