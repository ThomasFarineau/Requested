package fr.thomasfar.requester.requests;

import fr.thomasfar.requester.properties.Property;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.Map;

public abstract class Request {
    HttpClient httpClient;
    HttpRequest httpRequest;

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public HttpRequest getHttpRequest() {
        return httpRequest;
    }

    HttpRequest.BodyPublisher mapToJson(Map<Property, Object> map) {
        StringBuilder builtJson = new StringBuilder();
        map.forEach((key, value) -> {
            if (!builtJson.isEmpty()) builtJson.append(",");
            builtJson.append("\"").append(key.name()).append("\":");
            if (key.type() == String.class) builtJson.append("\"");
            builtJson.append(value);
            if (key.type() == String.class) builtJson.append("\"");
        });
        String json = "{" + builtJson + "}";
        return HttpRequest.BodyPublishers.ofString(json);
    }
}
