package dam.psp.servidor.model;

import dam.psp.cliente.model.Paquete;

import java.util.HashSet;
import java.util.Set;

public class Sala {
    private final int MAX_CLIENTES = 10;
    private int numClientes;
    private Set<String> clientes;
    private String chat;

    public Sala(){
        numClientes = 0;
        clientes = new HashSet<>();
        chat = "<------ CHAT ------>";
    }

    public int getNumClientes() {
        return numClientes;
    }

    public void setNumClientes(int numClientes) {
        this.numClientes = numClientes;
    }

    public Set<String> getClientes() {
        return clientes;
    }

    public void setClientes(Set<String> clientes) {
        this.clientes = clientes;
    }

    public String getChat() {
        return chat;
    }

    public void setChat(String chat) {
        this.chat = chat;
    }

    public void joinCliente(String nickname) {
        if (clientes.contains(nickname)) {
            System.out.println("Cliente duplicado con nickname: " + nickname);
        } else {
            System.out.println(nickname + " se ha unido");
            clientes.add(nickname);
            numClientes++;
            infoSala();
        }
    }

    public void leaveCliente(String nickname) {
        if (clientes.remove(nickname)) {
            System.out.println("Cliente con nickname " + nickname + " ha salido de la sala.");
            numClientes--;
        } else {
            System.out.println("Cliente con nickname " + nickname + " no se encontró en la sala.");
        }
        infoSala();
    }

    public void infoSala(){
        System.out.println("Clientes en la sala: " + numClientes + " de " + MAX_CLIENTES);
        for(String s : clientes){
            System.out.println(s);
        }
    }

    public boolean contieneCliente(String nickname){
        return clientes.contains(nickname);
    }
}