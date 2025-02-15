package dam.psp.servidor.model;

import dam.psp.cliente.model.Paquete;

import java.util.HashSet;
import java.util.List;
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

    public void joinCliente(Paquete p) {
        if (clientes.contains(p.getRemitente())) {
            System.out.println("Cliente duplicado con nickname: " + p.getRemitente());

        } else {
            System.out.println(p.getRemitente() + " se ha unido");
            clientes.add(p.getRemitente());
            numClientes++;
            infoSala();

        }
    }


    public void leaveCliente(Paquete p) {
        if (clientes.remove(p.getRemitente())) {
            System.out.println("Cliente con nickname " + p.getRemitente() + " ha salido de la sala.");
            numClientes--;
        } else {
            System.out.println("Cliente con nickname " + p.getRemitente() + " no se encontró en la sala.");
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
