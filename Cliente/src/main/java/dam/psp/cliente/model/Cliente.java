package dam.psp.cliente.model;



import dam.psp.cliente.controller.ConexionServidor;

import java.time.LocalTime;
import java.util.Scanner;


public class Cliente  {
    private final String nickname;

    private final ConexionServidor conexionServidor;
    private Paquete p;



    public Cliente(String nombre) {
        this.nickname = nombre;
        p = new Paquete();
        this.p.setRemitente(this.nickname);
        this.p.setMensajeCliente("Prueba " + LocalTime.now());
        conexionServidor = ConexionServidor.getInstance();
    }


    public void enviarPaquete() {
        Scanner sc = new Scanner(System.in);
        String opt;

        p.setDestinatario("SERVER");

        while (true) {
            showMenu();
            opt = sc.nextLine();
            setTipoPaquete(opt,sc);

            conexionServidor.procesarPaquete(p);

            // Salir si el usuario ingresa "0"
            if (opt.equals("10")) {
                break;
            }

        }
        sc.close();

        // Cerrar el scanner al salir

    }
    public void recibirPaquete(){
    }

    public void desconectar(){
        //TODO
    }

    public void setTipoPaquete(String tipo,Scanner sc){
        switch (tipo){
            case "1" -> p.setTipo(TipoPaquete.CONECTAR);
            case "2" -> crearMesaje(sc);
            case "3" -> p.setTipo(TipoPaquete.ARCHIVO);
            case "4" -> p.setTipo(TipoPaquete.NOTIFICACION);
            case "5" -> p.setTipo(TipoPaquete.AUTENTICACION);
            case "0" -> p.setTipo(TipoPaquete.DESCONECTAR);
            default -> System.out.println
                    ("Tipo de Paquete no reconocido");
        }

    }

    public void crearMesaje(Scanner sc){
        String opt;
        do{
            p.setTipo(TipoPaquete.MENSAJE);
            System.out.println("<- Escribe aqui: ");
            opt = sc.nextLine();
            p.setMensajeCliente(opt);
            System.out.println("<- Mensje enviado");
            conexionServidor.procesarPaquete(p);
        }while(!opt.equals("."));


    }

    public void showMenu(){
        System.out.println("1. CONECTAR");
        System.out.println("2. MENSAJE");
        System.out.println("3. ARCHIVO");
        System.out.println("4. NOTIFICACION");
        System.out.println("5. AUTENTICACION");
        System.out.println("0. DESCONECTAR");


    }






    public static void main(String[] args) {
        Cliente cliente = new Cliente("Jordi");
        cliente.enviarPaquete();


    }
}
