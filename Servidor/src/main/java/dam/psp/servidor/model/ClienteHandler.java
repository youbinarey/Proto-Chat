package dam.psp.servidor.model;

import dam.psp.cliente.model.Paquete;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Objects;

public class ClienteHandler implements Runnable {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Servidor servidor;
    private String nickname;

    public ClienteHandler(Socket socket, Servidor servidor) {
        this.socket = socket;
        this.servidor = servidor;
        try {
            // Inicializar los streams de entrada y salida
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.err.println("Error al inicializar los streams del cliente: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                Paquete pRecibido = (Paquete) in.readObject();
                if (pRecibido == null) break;
                servidor.procesarPaquete(pRecibido, out, in, socket, this);
            }
        } catch (EOFException e) {
            System.out.println("Cliente desconectado: " + nickname);
        } catch (ClassNotFoundException e) {
            System.err.println("Clase no encontrada: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error de I/O en cliente " + nickname + ": " + e.getMessage());
        } finally {
            servidor.desconectarCliente(socket, out, in, this, new Paquete());
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
     * Establece el nickname del cliente.
     *
     * @param nickname El nickname del cliente.
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Obtiene el nickname del cliente.
     *
     * @return El nickname del cliente.
     */
    public String getNickname() {
        return nickname;
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
}