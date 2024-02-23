package com.example.demo.Controllers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ExchangeRateApiUtil {

    private static final String API_KEY = "56da95dd81328e609646beaf";
    private static final String API_BASE_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/";

    public static double getExchangeRate(String baseCurrency, String targetCurrency) throws Exception {
        String apiUrl = API_BASE_URL + baseCurrency;
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();

            // Analysez la réponse JSON pour obtenir le taux de change
            double exchangeRate = parseExchangeRate(response.toString(), targetCurrency);
            return exchangeRate;
        } else {
            throw new Exception("Erreur lors de la requête à l'API. Code de réponse : " + responseCode);
        }
    }

    private static double parseExchangeRate(String jsonResponse, String targetCurrency) {
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(jsonResponse).getAsJsonObject();

        if (jsonObject.has("conversion_rates")) {
            JsonObject conversionRates = jsonObject.getAsJsonObject("conversion_rates");

            if (conversionRates.has(targetCurrency)) {
                // Utilisez la méthode get pour obtenir une représentation générique de la valeur
                return conversionRates.get(targetCurrency).getAsDouble();
            } else {
                System.out.println("La devise cible " + targetCurrency + " n'a pas été trouvée dans la réponse JSON.");
            }
        } else {
            System.out.println("La clé 'conversion_rates' n'a pas été trouvée dans la réponse JSON.");
        }

        // En cas d'échec, retournez une valeur par défaut (0.0 dans ce cas)
        return 0.0;
    }

//    public static void main(String[] args) {
//        try {
//            double exchangeRateUSDToEUR = getExchangeRate("USD", "EUR");
//            System.out.println("Taux de change de USD vers EUR : " + exchangeRateUSDToEUR);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}