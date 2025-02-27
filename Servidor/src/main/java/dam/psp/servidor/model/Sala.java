package dam.psp.servidor.model;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class Sala {
    private final int MAX_CLIENTES = 10;
    private int numClientes;
    private String chat;
    private final ObservableList<ClienteHandler> clientesObservables;


    public Sala() {
        numClientes = 0;
        clientesObservables = FXCollections.observableArrayList();
        chat = "<------ CHAT ------>";
    }

    public String getChat() {
        return chat;
    }

    public void setChat(String chat) {
        this.chat = chat;
    }

    public synchronized void  joinCliente(ClienteHandler cliente) {
        Platform.runLater(()-> {
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

    public synchronized void leaveCliente(ClienteHandler cliente) {
        Platform.runLater(() ->{
        if (clientesObservables.remove(cliente)) {
                cliente.setConnected(false);
                numClientes--;
                System.out.println(cliente.getNickname() + " ha salido de la sala.");
                infoSala();

        }
        });
    }

    public synchronized   void infoSala() {
        System.out.println("Clientes en la sala: " + numClientes + " de " + MAX_CLIENTES);
        for (ClienteHandler c : clientesObservables) {
            System.out.println(c.getNickname());
        }
    }

    public synchronized   boolean contieneCliente(ClienteHandler cliente) {
        return clientesObservables.contains(cliente);
    }

    public ObservableList<ClienteHandler> getClientes() {
        return clientesObservables;
    }

    public synchronized List<String> getClientesNickname() {
        List<String> clientesNickname = new ArrayList<>();
        for (ClienteHandler c : clientesObservables) {
            clientesNickname.add(c.getNickname());
        }
        return clientesNickname;
    }

    public synchronized boolean isNicknameInSala(String nickname){
        List<String> clientesNickname = getClientesNickname();
        if(clientesNickname.contains(nickname)){
            System.out.println("SALA: El cliente ya esta conectado");
            return true;
        }
        System.out.println("SALA: El cliente puede unirse a la sla");
        return false;
    }





}