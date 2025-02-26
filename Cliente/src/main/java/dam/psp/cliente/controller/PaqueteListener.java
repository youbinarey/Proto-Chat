package dam.psp.cliente.controller;

import dam.psp.cliente.model.paquete.Paquete;
import dam.psp.cliente.model.paquete.Paquetes;

import java.util.List;

public interface PaqueteListener {
    void mensajeRecibido(Paquete p);



    void updateUsuariosConectados(List<String> listaUsuarios);
}
