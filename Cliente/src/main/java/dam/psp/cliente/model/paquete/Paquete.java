package dam.psp.cliente.model.paquete;

import dam.psp.cliente.util.Network;

import java.io.Serializable;

/**
 * Clase abstracta que representa un paquete de datos.
 * Los paquetes son objetos que se envían entre el cliente y el servidor.
 */
public abstract class Paquete implements Serializable {

    /** Dirección IP del dispositivo que envía el paquete. */
    protected final String IP;

    /** Tipo del paquete que se envía. */
    protected final TipoPaquete tipo;

    /**
     * Constructor de la clase Paquete.
     * Establece la dirección IP del emisor y el tipo del paquete.
     *
     * @param tipo Tipo de paquete que se está creando.
     */
    public Paquete(TipoPaquete tipo) {
        this.IP = Network.getMyIp(); // Obtiene la IP local del dispositivo.
        this.tipo = tipo;
    }

    /**
     * Obtiene el tipo del paquete.
     *
     * @return El tipo de paquete.
     */
    public TipoPaquete getTipo() {
        return tipo;
    }

    /**
     * Obtiene la dirección IP del dispositivo que envió el paquete.
     *
     * @return Dirección IP del emisor.
     */
    public String getIP() {
        return IP;
    }
}
