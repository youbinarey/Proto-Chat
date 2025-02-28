package dam.psp.cliente.model.paquete;

public class PaqueteFactory {

    public static Paquete crearPaquete(TipoPaquete tipo, Object... parametros){
        switch (tipo){
            case AUTENTICACION -> {return crearPaqueteAutenticacion(parametros);}
            case CONECTAR -> {return crearPaqueteConectar(parametros);}
            case PING -> {return crearPaquetePING(parametros);}
            case DESCONECTAR -> {return crearPaqueteDesconectar(parametros);}
            case NOTIFICACION -> {return crearPaqueteNotificacion(parametros);}
            case MENSAJE -> {return crearPaqueteMensaje(parametros);}
            default -> throw new IllegalArgumentException("Tipo de paquete no válido: " + tipo);
        }
    }

    private static Paquete crearPaqueteMensaje(Object... parametros) {
        String remitente = (String) parametros[0];
        String mensaje = (String) parametros[1];
        return new PaqueteMensaje(remitente, mensaje);

    }

    private static Paquete crearPaqueteNotificacion(Object... parametros) {
        String usuario = (String) parametros[0];
        String evento = (String) parametros[1];
        return new PaqueteNotificacion(usuario, evento);

    }

    private static Paquete crearPaqueteDesconectar(Object... parametros) {
        String usuario =(String ) parametros[0];

        return new PaqueteDesconectar(usuario);

    }

    private static Paquete crearPaquetePING(Object... parametros) {
        String usuario = (String) parametros[0];
        return new PaquetePing(usuario);

    }

    private static Paquete crearPaqueteConectar(Object... parametros) {
        String usuario =(String ) parametros[0];
        return new PaqueteConectar( usuario);
    }

    private static Paquete crearPaqueteAutenticacion(Object... parametros) {
        String usuario = (String) parametros[0];
        String password = (String) parametros[1];
        return new PaqueteAutenticacion(usuario, password);
    }


}
