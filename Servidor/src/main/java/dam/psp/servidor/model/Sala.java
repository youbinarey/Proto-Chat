package dam.psp.servidor.model;

import dam.psp.cliente.model.Paquete;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Sala {
    private final int MAX_CLIENTES = 10;
    private int numClientes;
    private Map<String, ClienteHandler> clientes;
    private String chat;

    public Sala(){
        numClientes = 0;
        clientes = new HashMap<>();
        chat = "<------ CHAT ------>";
    }

    public String getChat() {
        return chat;
    }

    public void setChat(String chat) {
        this.chat = chat;
    }

    public void joinCliente(ClienteHandler cliente) {
        if (clientes.size() < MAX_CLIENTES && !clientes.containsKey(cliente.getNickname())) {
            clientes.put(cliente.getNickname(),cliente);
            numClientes++;
            System.out.println(cliente.getNickname() + " se ha unido");
            infoSala();
        } else {
            System.out.println("No se puede agregar a " + cliente.getNickname());
        }
    }

    public void leaveCliente(ClienteHandler cliente) {
        if (clientes.remove(cliente.getNickname())!=null) {
            numClientes--;
            System.out.println(cliente.getNickname() + " ha salido de la sala.");
            infoSala();
        }
    }

    public void infoSala(){
        System.out.println("Clientes en la sala: " + numClientes + " de " + MAX_CLIENTES);
        for(String s : clientes.keySet()){
            System.out.println(s);
        }
    }

    public boolean contieneCliente(ClienteHandler cliente){
        return clientes.containsKey(cliente.getNickname());
    }



    public void broadcastMensaje(Paquete p) {
        for (ClienteHandler cliente : clientes.values()) {
            cliente.enviarPaquete(p);
        }
    }
}