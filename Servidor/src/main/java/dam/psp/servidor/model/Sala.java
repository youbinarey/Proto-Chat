package dam.psp.servidor.model;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa una sala en la que los clientes pueden unirse o salir.
 * La sala tiene un número máximo de clientes, y mantiene una lista de clientes conectados.
 */
public class Sala {

    // Máximo número de clientes que puede tener la sala
    private final int MAX_CLIENTES = 1;
    // Número actual de clientes conectados a la sala
    private int numClientes;

    // Lista observable de clientes conectados a la sala
    private final ObservableList<ClienteHandler> clientesObservables;

    /**
     * Constructor de la clase Sala. Inicializa la lista de clientes y el número de clientes a cero.
     */
    public Sala() {
        numClientes = 0;
        clientesObservables = FXCollections.observableArrayList();
    }

    /**
     * Permite a un cliente unirse a la sala si hay espacio disponible y no está ya conectado.
     * Este método es sincronizado para asegurar que no haya modificaciones concurrentes a la lista de clientes.
     *
     * @param cliente El cliente que intenta unirse a la sala.
     */
    public synchronized void joinCliente(ClienteHandler cliente) {
        Platform.runLater(() -> {
            if (clientesObservables.size() < MAX_CLIENTES && !clientesObservables.contains(cliente)) {
                clientesObservables.add(cliente);
                numClientes++;
                System.out.println(cliente.getNickname() + " se ha unido");
                infoSala();
            } else {
                System.out.println("No se puede agregar a " + cliente.getNickname());
            }
        });
    }

    /**
     * Permite a un cliente salir de la sala.
     * Este método es sincronizado para asegurar que no haya modificaciones concurrentes a la lista de clientes.
     *
     * @param cliente El cliente que desea salir de la sala.
     */
    public synchronized void leaveCliente(ClienteHandler cliente) {
        Platform.runLater(() -> {
            if (clientesObservables.remove(cliente)) {
                cliente.setConnected(false);
                numClientes--;
                System.out.println(cliente.getNickname() + " ha salido de la sala.");
                infoSala();
            }
        });
    }

    /**
     * Muestra la información de la sala, incluyendo el número actual de clientes y sus apodos.
     * Este método es sincronizado para evitar modificaciones concurrentes.
     */
    public synchronized void infoSala() {
        System.out.println("Clientes en la sala: " + numClientes + " de " + MAX_CLIENTES);
        for (ClienteHandler c : clientesObservables) {
            System.out.println(c.getNickname());
        }
    }

    /**
     * Verifica si un cliente está presente en la sala.
     *
     * @param cliente El cliente que se quiere verificar si está en la sala.
     * @return true si el cliente está en la sala, false en caso contrario.
     */
    public synchronized boolean contieneCliente(ClienteHandler cliente) {
        return clientesObservables.contains(cliente);
    }

    /**
     * Obtiene la lista observable de todos los clientes en la sala.
     *
     * @return La lista observable de clientes en la sala.
     */
    public ObservableList<ClienteHandler> getClientes() {
        return clientesObservables;
    }

    /**
     * Obtiene una lista con los apodos (nicknames) de todos los clientes conectados a la sala.
     *
     * @return Una lista de strings con los apodos de los clientes.
     */
    public synchronized List<String> getClientesNickname() {
        List<String> clientesNickname = new ArrayList<>();
        for (ClienteHandler c : clientesObservables) {
            clientesNickname.add(c.getNickname());
        }
        return clientesNickname;
    }

    /**
     * Verifica si un usuario ya está en uso en la sala.
     *
     * @param nickname El usuario que se quiere verificar.
     * @return true si el usuario ya está en uso, false si el cliente puede unirse.
     */
    public synchronized boolean isNicknameInSala(String nickname) {
        List<String> clientesNickname = getClientesNickname();
        if (clientesNickname.contains(nickname)) {
            System.out.println("SALA: El cliente ya esta conectado");
            return true;
        }
        System.out.println("SALA: El cliente puede unirse a la sala");
        return false;
    }
}
