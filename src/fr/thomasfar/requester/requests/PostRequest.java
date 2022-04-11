package fr.thomasfar.requester.requests;

import fr.thomasfar.requester.Requester;
import fr.thomasfar.requester.properties.Property;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.Map;
import java.util.Objects;

public class PostRequest extends Request {

    public PostRequest(String uri, Map<Property, Object> parameters) {
        HttpRequest.BodyPublisher payLoad = null;
        if(Objects.equals(Requester.REQUEST_TYPE, "application/json"))
            payLoad = mapToJson(parameters);

        super.httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
        super.httpRequest = HttpRequest.newBuilder().POST(payLoad).uri(URI.create(Requester.HOST + uri)).setHeader("User-Agent", Requester.AGENT_NAME).header("Content-Type", Requester.REQUEST_TYPE).build();
    }
}
