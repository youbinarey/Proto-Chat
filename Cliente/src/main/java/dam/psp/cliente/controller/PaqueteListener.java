package dam.psp.cliente.controller;

import dam.psp.cliente.model.paquete.Paquete;

import java.util.List;

/**
 * Interfaz para recibir y procesar paquetes desde el servidor.
 */
public interface PaqueteListener {
    /**
     * Se llama cuando se recibe un paquete.
     *
     * @param p El paquete recibido.
     */
    void mensajeRecibido(Paquete p);

    /**
     * Se llama para actualizar la lista de usuarios conectados.
     *
     * @param listaUsuarios La lista de usuarios conectados.
     */
    void updateUsuariosConectados(List<String> listaUsuarios);
}


