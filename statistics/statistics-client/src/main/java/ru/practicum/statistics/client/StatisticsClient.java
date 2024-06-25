package ru.practicum.statistics.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.stream.Collectors;
import com.google.gson.Gson;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import ru.practicum.statistics.dto.EndpointHitDto;

public class StatisticsClient {
    private HttpClient client;
    private String url;
    private Properties config;
    private Gson gson;

    public StatisticsClient() throws IOException {
        config = PropertiesLoader.loadProperties();
        url = config.getProperty("url");
        client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(1))
                .build();
        gson = new Gson();
    }

    public void addStatistics(EndpointHitDto endpointHitDto) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/hit"))
                .timeout(Duration.ofSeconds(5))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(endpointHitDto)))
                .build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    public List<EndpointHitDto> getStatistics(String start, String end, List<String> uris, Boolean unique) {
        String urisParams = "";
        if (uris != null) {
            urisParams = uris.stream()
                    .map(x -> "&uris=" + x)
                    .collect(Collectors.joining());
        }
        String fullUrl = url + "/stats?start=" + start + "&end=" + end + urisParams + "&unique=" + unique;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(fullUrl))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();
        String response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .toString();
        Type listType = new TypeToken<List<EndpointHitDto>>(){}.getType();
        return gson.fromJson(response, listType);
    }
}
