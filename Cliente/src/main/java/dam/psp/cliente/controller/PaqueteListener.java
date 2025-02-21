package dam.psp.cliente.controller;

import dam.psp.cliente.model.Paquete;

import java.util.List;

public interface PaqueteListener {
    void mensajeRecibido(Paquete p);
    void updateUsuariosConectados(List<String> listaUsuarios);
}
