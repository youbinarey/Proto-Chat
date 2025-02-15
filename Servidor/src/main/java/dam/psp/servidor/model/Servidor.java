package dam.psp.servidor.model;


import dam.psp.cliente.model.Paquete;
import dam.psp.cliente.model.TipoPaquete;
import dam.psp.servidor.config.Config;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Servidor {

    private ServerSocket serverSocket;
    private final int PUERTO = Config.SERVER_PORT;
    private String logs;
    private Sala sala;
    private Map<Socket, String> clientesConectados = new HashMap<>();

    public Servidor() {
        try {
            serverSocket = new ServerSocket(PUERTO);
            logs = "";
            sala = new Sala();
        } catch (IOException e) {
            System.err.println("Error al crear el servidor en el puerto " + PUERTO + " " + e.getMessage());
        }
    }


    public void servidorUp()  {
        addActivity();
        while(true){
            Socket clienteSocket = esperarConexion(); // Aceptar conexion
            if(clienteSocket != null){
                conexionCliente(clienteSocket);

            }
        }


    }

    private Socket esperarConexion() {
        try{
            return serverSocket.accept();
        } catch (IOException e) {
            System.out.println("Error esperando Conexión " + e.getMessage());
            return null;
        }
    }

    private void conexionCliente(Socket clienteSocket) {
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        try {
            out = new ObjectOutputStream(clienteSocket.getOutputStream());
            in = new ObjectInputStream(clienteSocket.getInputStream());


            while (true) {
                Paquete pRecibido = (Paquete) in.readObject();

                if (pRecibido == null) {
                    break;
                }
                System.out.print("Paquete recibido");
                infoPaquete(pRecibido);

                procesarPaquete(pRecibido, out, in, clienteSocket);

                out.flush();
                out.reset();

            }
        } catch (EOFException e) {
            System.out.println("Cliente desconectado");

        } catch (IOException | ClassNotFoundException e) {
            //System.err.println("Error en la conexión con el cliente: " + e.getMessage());
        } finally {
            String nickname = clientesConectados.get(clienteSocket);
            if (nickname != null) {
                Paquete pDesconexion = new Paquete();
                pDesconexion.setTipo(TipoPaquete.DESCONECTAR);
                pDesconexion.setRemitente(nickname);
                sala.leaveCliente(pDesconexion); // Eliminar al cliente de la sala
                clientesConectados.remove(clienteSocket);
                System.out.println("Cliente " + nickname + " eliminado de la sala por desconexion no controlada.");
            }
        }
    }

    private void procesarPaquete(Paquete p, ObjectOutputStream out, ObjectInputStream in,  Socket clienteSocket){

        switch (p.getTipo()){
            case CONECTAR -> {if(sala.contieneCliente(p.getRemitente())){
                System.out.println("Cliente duplicado:" + p.getRemitente());
            }else {
                sala.joinCliente(p);
                clientesConectados.put(clienteSocket, p.getRemitente());
            };}

            case MENSAJE -> {System.out.println("Mensaje_Recibido");

               addChat(p);
            actualizarChat(out);}
            /*
            case ARCHIVO -> {p.setMensajeCliente("Archivo Recibido: ");
                p.setTipo(TipoPaquete.ARCHIVO);}

            case NOTIFICACION -> {p.setMensajeCliente("Notificación Recibida: ");
                p.setTipo(TipoPaquete.NOTIFICACION);}

            case AUTENTICACION -> {p.setMensajeCliente("AUTENTICACION Recibida: ");
                p.setTipo(TipoPaquete.AUTENTICACION);}
            */
            case DESCONECTAR -> {desconectarCliente(clienteSocket,out,in,p);
                clientesConectados.remove(clienteSocket);
            }

            default -> System.out.println
                    ("Tipo de Paquete no reconocido");
        }
    }
    private void desconectarCliente(Socket clienteSocket, ObjectOutputStream out, ObjectInputStream in, Paquete p) {
        if (p != null) {
            sala.leaveCliente(p); // Eliminar al cliente de la sala
        }

        try {
            if (out != null) out.close(); // Cerrar el ObjectOutputStream
            if (in != null) in.close();  // Cerrar el ObjectInputStream
            if (clienteSocket != null && !clienteSocket.isClosed()) {
                clienteSocket.close(); // Cerrar el socket
            }
            System.out.println("Cliente desconectado correctamente.");
        } catch (IOException e) {
           // System.err.println("Error al desconectar al cliente: " + e.getMessage());
        }
    }

    private void actualizarChat(ObjectOutputStream out){
        Paquete p = new Paquete();
        p.setTipo(TipoPaquete.MENSAJE);
        p.setRemitente("Server");
        p.setMensajeCliente(sala.getChat());
        p.setDestinatario("TODOS");
        try {
                out.writeObject(p);
                out.flush();
                out.reset();

        } catch (IOException e) {
            System.err.println("Error en actualizarChat "+ e.getMessage());
        }
        infoChat();
    }

    private void addActivity(){
        logs += "Servidor iniciado en el puerto 9999" + "\n";
        showActivity();
    }

    private void showActivity(){
        System.out.println(logs);
    }
    private void infoPaquete(Paquete p){
        System.out.println("----------------------");
        System.out.println("Paquete tipo: " + p.getTipo().toString());
        System.out.println("Contenido del mensaje: " + p.getMensajeCliente());
        System.out.println("Hash del objeto: " + p.hashCode());
        System.out.println("----------------------");

    }

    private void infoChat(){
        System.out.println(sala.getChat());

    }


    public void addChat(Paquete p){
        sala.setChat(sala.getChat() + "\n" + p.getRemitente() + " dice: "+p.getMensajeCliente());
    }
    


    public static void main(String[] args) {
        Servidor servidor = new Servidor();
        servidor.servidorUp();
    }


}


