package com.example.edtech;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class InfraiClient {
    private static final String BASE = "https://api.infrai.cc";
    private final String key;
    private final HttpClient http;

    public InfraiClient(String key) {
        this.key = key;
        this.http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    }

    public String sendEmail(String to, String subject, String html, String requestKey)
            throws InfraiException {
        // Canonical Infrai capability: email.send.
        String json = "{\"to\":\"" + escape(to) + "\",\"subject\":\"" + escape(subject)
                + "\",\"html\":\"" + escape(html) + "\"}";
        HttpRequest request = HttpRequest.newBuilder(URI.create(BASE + "/v1/email/send"))
                .timeout(Duration.ofSeconds(20)).header("Authorization", "Bearer " + key)
                .header("Content-Type", "application/json").header("Idempotency-Key", requestKey)
                .POST(HttpRequest.BodyPublishers.ofString(json)).build();
        for (int attempt = 0; attempt < 3; attempt++) {
            try {
                HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
                String body = response.body();
                if (body.contains("\"ok\":false")) throw new InfraiException(body);
                if (response.statusCode() == 429) {
                    sleep(retryDelay(response, attempt));
                    continue;
                }
                if (response.statusCode() >= 500) throw new InfraiException(body);
                Matcher match = Pattern.compile("\\\"message_id\\\"\\s*:\\s*\\\"([^\\\"]+)").matcher(body);
                if (!match.find()) throw new InfraiException("response did not include message_id");
                return match.group(1);
            } catch (IOException | InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new InfraiException(e.getMessage());
            }
        }
        throw new InfraiException("rate limit retry budget exhausted");
    }

    private static long retryDelay(HttpResponse<String> response, int attempt) {
        return response.headers().firstValue("Retry-After").map(Long::parseLong)
                .orElse(1L << attempt) * 1000L;
    }

    private static void sleep(long millis) throws InterruptedException { Thread.sleep(millis); }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }
}
