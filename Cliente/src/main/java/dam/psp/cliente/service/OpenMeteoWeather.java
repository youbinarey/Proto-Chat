package dam.psp.cliente.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalTime;
import java.util.Arrays;

import org.json.JSONObject;

/**
 * Clase que permite obtener información sobre el clima y la ubicación geográfica de la IP.
 * Utiliza las APIs de Open-Meteo para el clima y de ipinfo.io para obtener la ubicación de la IP.
 */
public class OpenMeteoWeather {

    /**
     * Método para obtener la ubicación geográfica (latitud y longitud) de la IP del usuario.
     * Utiliza la API de ipinfo.io para obtener la ubicación.
     *
     * @return Un array de dos elementos, donde el primer valor es la latitud y el segundo es la longitud.
     * @throws Exception Si ocurre un error al hacer la solicitud HTTP o al procesar la respuesta JSON.
     */
    public static double[] getLocation() throws Exception {
        String ipApiUrl = "https://ipinfo.io/json";
        HttpURLConnection connection = (HttpURLConnection) new URL(ipApiUrl).openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // Parsear la respuesta JSON
        JSONObject jsonResponse = new JSONObject(response.toString());
        String loc = jsonResponse.getString("loc");
        String[] latLon = loc.split(",");

        double lat = Double.parseDouble(latLon[0]);
        double lon = Double.parseDouble(latLon[1]);

        return new double[]{lat, lon};  // Devolver las coordenadas como un array
    }

    /**
     * Método para obtener la temperatura actual en la ubicación del usuario.
     * Utiliza la API de Open-Meteo para obtener la temperatura en la latitud y longitud de la ubicación.
     *
     * @return Un string con la temperatura actual en grados Celsius (°C). Si ocurre un error, devuelve un mensaje de error.
     */
    public static String getWeather() {
        try {
            // Obtener latitud y longitud de la ubicación
            double[] location = getLocation();
            double lat = location[0];
            double lon = location[1];

            // URL de la API de Open-Meteo
            String urlString = "https://api.open-meteo.com/v1/forecast?latitude="
                    + lat + "&longitude=" + lon + "&current_weather=true"
                    + "&timezone=Europe/Madrid";



            // Hacer la solicitud a OpenMeteo
            HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
            connection.setRequestMethod("GET");

            // Leer la respuesta de la API
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Imprimir la respuesta
            System.out.println("Response from Open-Meteo: " + response.toString());

           //respuesta
            JSONObject jsonResponse = new JSONObject(response.toString());

            // Verificar si la clave "current_weather" está presente
            if (jsonResponse.has("current_weather")) {
                JSONObject currentWeather = jsonResponse.getJSONObject("current_weather");

                // Obtener la temperatura actual
                if (currentWeather.has("temperature")) {
                    double currentTemperature = currentWeather.getDouble("temperature");
                    return currentTemperature + "°C";
                } else {
                    return "Error: 'temperature' not found in current_weather.";
                }
            } else {
                return "Error: 'current_weather' key not found in the response.";
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());


            return "Error retrieving weather information";
        }
    }


}
