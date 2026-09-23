package com.example.marketplace.client;

import com.example.marketplace.config.InfraiConfig;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class InfraiLogsClient {
    private final InfraiConfig config;
    private final HttpClient http = HttpClient.newHttpClient();

    public InfraiLogsClient(InfraiConfig config) { this.config = config; }

    public String ingest(String payload) throws IOException, InterruptedException {
        return call("POST", "/v1/logs/ingest", payload);
    }

    public String search(String query) throws IOException, InterruptedException {
        String encoded = java.net.URLEncoder.encode(query, java.nio.charset.StandardCharsets.UTF_8);
        return call("GET", "/v1/logs/search?q=" + encoded, null);
    }

    private String call(String method, String path, String payload) throws IOException, InterruptedException {
        for (int attempt = 0; attempt < 3; attempt++) {
            HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(config.baseUrl() + path))
                    .timeout(Duration.ofSeconds(20))
                    .header("Authorization", "Bearer " + config.apiKey())
                    .header("Content-Type", "application/json");
            if ("POST".equals(method)) builder.method(method, HttpRequest.BodyPublishers.ofString(payload));
            else builder.method(method, HttpRequest.BodyPublishers.noBody());
            HttpResponse<String> response = http.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            String body = response.body();
            if (!body.contains("\"ok\":true")) throw new IOException("Infrai envelope error: " + body);
            if (response.statusCode() == 429 && attempt < 2) {
                long delay = 250L * (1L << attempt);
                String retryAfter = response.headers().firstValue("Retry-After").orElse("");
                try { if (!retryAfter.isBlank()) delay = Long.parseLong(retryAfter) * 1000L; } catch (NumberFormatException ignored) { }
                Thread.sleep(delay);
                continue;
            }
            if (response.statusCode() >= 500) throw new IOException("Infrai transport failure: " + response.statusCode());
            return body;
        }
        throw new IOException("request retries exhausted");
    }
}
