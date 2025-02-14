package dam.psp.cliente.model;



import dam.psp.cliente.controller.ConexionServidor;

import java.io.Serializable;
import java.util.Scanner;


public class Cliente  {
    private final String nickname;

    private final ConexionServidor conexionServidor;
    private Paquete paquete;



    public Cliente(String nombre) {
        this.nickname = nombre;
        conexionServidor = ConexionServidor.getInstance();
    }

    public void conectarServidor(){
       conexionServidor.conectar(new Paquete(this.nickname,null,null, null, TipoPaquete.CONECTAR));
        conexionServidor.escucharServidor();

    }
    public void enviarPaquete() {
        Scanner sc = new Scanner(System.in);

        Paquete p = new Paquete();
        p.setTipo(TipoPaquete.MENSAJE);
        p.setDestinatario("SERVER");

        while (true) {
            System.out.println("Introduce el mensaje (o '0' para salir):");
            String mensaje = sc.nextLine();

            // Salir si el usuario ingresa "0"
            if (mensaje.equals("0")) {
                break;
            }

            // Establecer el mensaje y enviar el paquete
            p.setMensajeCliente(mensaje);
            conexionServidor.enviarDatos(p);

            // Esperar y mostrar la respuesta del servidor
            System.out.println("Esperando respuesta del servidor...");
        }

        // Cerrar el scanner al salir
        sc.close();
    }
    public void recibirPaquete(){
    }

    public void desconectar(){
        //TODO
    }






    public static void main(String[] args) {
        Cliente cliente = new Cliente("Yeray");
        cliente.conectarServidor();
        cliente.enviarPaquete();


    }
}
