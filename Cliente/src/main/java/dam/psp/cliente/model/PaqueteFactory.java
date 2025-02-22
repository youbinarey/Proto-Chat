package dam.psp.cliente.model;

public class PaqueteFactory {

    public static Paquete crearPaquete(TipoPaquete tipo, Object... parametros){
        switch (tipo){
            case AUTENTICACION -> {return crearPaqueteAutenticacion(parametros);}
            case CONECTAR -> {return crearPaqueteConectar();}
            case PING -> {return crearPaquetePING(parametros);}
            case DESCONECTAR -> {return crearPaqueteDesconectar();}
            case NOTIFICACION -> {return crearPaqueteNotificacion(parametros);}
            case MENSAJE -> {return crearPaqueteMensaje(parametros);}
            default -> throw new IllegalArgumentException("Tipo de paquete no válido: " + tipo);
        }
    }

    private static Paquete crearPaqueteMensaje(Object... parametros) {
        return null;

    }

    private static Paquete crearPaqueteNotificacion(Object... parametros) {
        return null;

    }

    private static Paquete crearPaqueteDesconectar() {
        return new PaqueteDesconectar();

    }

    private static Paquete crearPaquetePING(Object... parametros) {
        return null;

    }

    private static Paquete crearPaqueteConectar() {

        return new PaqueteConectar();
    }

    private static Paquete crearPaqueteAutenticacion(Object... parametros) {
        String usuario = (String) parametros[0];
        String password = (String) parametros[1];
        return new PaqueteAutenticacion(usuario, password);
    }


}
