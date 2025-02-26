package dam.psp.servidor.model;

import dam.psp.cliente.model.paquete.Paquete;
import dam.psp.cliente.model.paquete.PaqueteConectar;
import dam.psp.cliente.model.paquete.Paquetes;
import dam.psp.cliente.model.paquete.Paquetes;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Objects;

public class ClienteHandler implements Runnable {
    private final Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private final Servidor servidor;
    private String nickname;
    private String IP;
    boolean isConected;

    public ClienteHandler(String nickname, String IP,ObjectOutputStream out, ObjectInputStream in, Socket socket, Servidor servidor) {
        this.socket = socket;
        this.servidor = servidor;
        this.nickname = nickname;
        this.IP = IP;
        this.out = out;
        this.in = in;
        System.out.println("ClienteHandler: creado cliente -> " + this.getNickname() +  " con IP " + this.getIP()  );
        isConected = true;
    }

    private void closeConnection() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null && !socket.isClosed()) socket.close();
           isConected = false;
            System.out.println("ClienteHandler -> Conexión cerrada con el cliente: " + nickname);
        } catch (IOException e) {
            System.err.println("Error al cerrar la conexión con el cliente " + nickname + ": " + e.getMessage());
        }
    }

    @Override
    public void run() {

        try {
            while (isConected) {
                //Paquetes pRecibido = (Paquetes) in.readObject();
                Paquete pRecibido = (Paquete) in.readObject();

                if (pRecibido == null) break;
                //servidor.procesarPaquete(pRecibido, out, in, socket, this);
                servidor.procesarPaquete(pRecibido, out, in, socket,this);
            }
        } catch (EOFException e) {
            System.out.println("Cliente desconectado: " + nickname);
        } catch (ClassNotFoundException e) {
            System.err.println("Clase no encontrada: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error de I/O en cliente " + nickname + ": " + e.getMessage());
        } finally {
            servidor.desconectarCliente(socket, out, in, this, null); // Desconectar al cliente
        }
    }

    /**
     * Envía un paquete al cliente.
     *
     * @param p El paquete a enviar.
     */
    public void enviarPaquete(Paquete p) {
        try {
            out.writeObject(p);
            out.flush();
            out.reset();
        } catch (IOException e) {
            System.err.println("Error al enviar paquete al cliente " + nickname + ": " + e.getMessage());
        }
    }

    /**
     * Obtiene el nickname del cliente.
     *
     * @return El nickname del cliente.
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Establece el nickname del cliente.
     *
     * @param nickname El nickname del cliente.
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClienteHandler that = (ClienteHandler) o;
        return Objects.equals(socket, that.socket) && Objects.equals(servidor, that.servidor) && Objects.equals(nickname, that.nickname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(socket, servidor, nickname);
    }

    @Override
    public String toString() {
        return nickname;
    }
    public boolean isConected() {
        return isConected;
    }
    public void setConnected(boolean conected) {
        this.isConected = conected;
    }

    public String getIP() {
        return IP;
    }
}