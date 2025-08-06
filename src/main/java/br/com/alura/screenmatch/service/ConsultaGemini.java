package br.com.alura.screenmatch.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

public class ConsultaGemini {
    public static String obterTraducao(String texto) {
        // The client gets the API key from the environment variable `GOOGLE_API_KEY`.
        Client client = Client.builder().apiKey(System.getenv("GEMINI_API_KEY")).build();

        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-2.0-flash",
                        "Apenas traduza, sem me dizer nada alem disso, o seguinte trecho: " + texto,
                        null);

        return response.text();
    }
}