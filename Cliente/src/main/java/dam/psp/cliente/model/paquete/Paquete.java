package dam.psp.cliente.model.paquete;

import dam.psp.cliente.util.Network;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

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
        //this.IP = Network.getMyIp();//
        this.IP  = getPrivateIp();// Obtiene la IP publica local del dispositivo.
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

    public static void main(String[] args) {
            System.out.println("Ip privada "+  getPrivateIp());

    }
    public static String getPrivateIp() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress inetAddress = addresses.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress.isSiteLocalAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "127.0.0.1"; // Default to localhost if no private IP is found
    }
}
