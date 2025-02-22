package dam.psp.cliente.controller;

import dam.psp.cliente.model.Paquetes;

import java.util.List;

public interface PaqueteListener {
    void mensajeRecibido(Paquetes p);
    void updateUsuariosConectados(List<String> listaUsuarios);
}
