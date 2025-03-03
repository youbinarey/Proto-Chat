package dam.psp.cliente.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Network {
   

    public static String getMyIp() {
        StringBuilder response = new StringBuilder();

        try {                                                                         
                // URL de un servicio que devuelve la IP pública
                URL url = new URL("https://api.ipify.org");

                // Crear una conexión HTTP
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");

                // Leer la respuesta
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Imprimir la IP pública
                System.out.println("Tu dirección IP pública es: " + response.toString());
                

            } catch (Exception e) {
                System.out.println("No se pudo obtener la IP pública.");
                e.printStackTrace();
            }
            return response.toString();
    }

}
