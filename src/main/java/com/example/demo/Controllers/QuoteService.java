package com.example.demo.Controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class QuoteService {

    private static final String API_URL = "https://api.quotable.io/random?tags=inspirational";

    public String getRandomQuote() throws IOException {
        try {
            // Create URL object
            URL url = new URL(API_URL);

            // Open connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set request method
            connection.setRequestMethod("GET");

            // Get response code
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read response
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Parse JSON response to get the quote
                String jsonResponse = response.toString();
                String quote = jsonResponse.substring(jsonResponse.indexOf("\"content\":\"") + 11, jsonResponse.indexOf("\",\"author\""));
                return quote;
            } else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                System.out.println("No quotes found for the specified tags.");
            } else {
                System.out.println("Failed to get quote. Response code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
