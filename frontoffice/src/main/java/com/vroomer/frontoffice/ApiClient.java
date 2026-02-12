package com.vroomer.frontoffice;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

public class ApiClient {
    public static void main(String[] args) throws Exception {
        // Lire le token dans application.properties du FO
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream("src/main/resources/application.properties")) {
            props.load(fis);
        }
        String token = props.getProperty("security.token");
        if (token == null || token.isEmpty()) {
            System.err.println("Token non trouvé dans application.properties");
            return;
        }

        String url = "http://localhost:8089/api/reservations";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", token);

        int responseCode = con.getResponseCode();
        System.out.println("Code HTTP : " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(
                con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        System.out.println(response.toString());
    }
}
