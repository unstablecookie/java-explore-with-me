package ru.practicum.statistics.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import com.google.gson.Gson;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import ru.practicum.statistics.dto.EndpointHitDto;
import ru.practicum.statistics.dto.ViewStatsDto;

public class StatisticsClient {
    private HttpClient client;
    public String url;
    private Properties config;
    private Gson gson;

    public StatisticsClient() {
        try {
            config = PropertiesLoader.loadProperties();
            url = config.getProperty("client.url");
            client = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_2)
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .connectTimeout(Duration.ofSeconds(1))
                    .build();
            gson = new Gson();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addStatistics(EndpointHitDto endpointHitDto) {
        String uriPath = url + "/hit";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uriPath))
                .timeout(Duration.ofSeconds(5))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(endpointHitDto)))
                .build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    public List<ViewStatsDto> getStatistics(String start, String end, List<String> uris, Boolean unique) {
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
        try {
            HttpResponse response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).get();
            Type listType = new TypeToken<ArrayList<ViewStatsDto>>() {
            }.getType();
            String jsonResponse = response.body().toString();
            return gson.fromJson(jsonResponse, listType);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return List.of();
        }
    }
}
