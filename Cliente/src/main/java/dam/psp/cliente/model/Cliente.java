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
        String opt;

        Paquete p = new Paquete();
        p.setDestinatario("SERVER");

        while (true) {
            setTipoPaquete();
            opt = sc.nextLine();

            p = crearTipoPaquete(p, opt);

            // Salir si el usuario ingresa "0"
            if (opt.equals("0")) {
                break;
            }
            System.out.println("Cliente envia datos");

            conexionServidor.enviarDatos(p);
        }

        // Cerrar el scanner al salir
        conexionServidor.cerrarConexion();
        sc.close();
    }
    public void recibirPaquete(){
    }

    public void desconectar(){
        //TODO
    }

    public Paquete crearTipoPaquete(Paquete p,String tipo){
        switch (tipo){
            case "1" -> p.setTipo(TipoPaquete.CONECTAR);
            case "2" -> p.setTipo(TipoPaquete.MENSAJE);
            case "3" -> p.setTipo(TipoPaquete.ARCHIVO);
            case "4" -> p.setTipo(TipoPaquete.NOTIFICACION);
            case "5" -> p.setTipo(TipoPaquete.AUTENTICACION);
            case "6" -> p.setTipo(TipoPaquete.DESCONECTAR);
            default -> System.out.println
                    ("Tipo de Paquete no reconocido");
        }
        return p;
    }

    public void setTipoPaquete(){
        System.out.println("1. CONECTAR");
        System.out.println("2. MENSAJE");
        System.out.println("3. ARCHIVO");
        System.out.println("4. NOTIFICACION");
        System.out.println("5. AUTENTICACION");
        System.out.println("0. DESCONECTAR");


    }






    public static void main(String[] args) {
        Cliente cliente = new Cliente("Yeray");
        cliente.conectarServidor();
        cliente.enviarPaquete();


    }
}
