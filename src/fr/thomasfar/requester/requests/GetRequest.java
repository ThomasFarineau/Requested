package fr.thomasfar.requester.requests;

import fr.thomasfar.requester.Requester;
import fr.thomasfar.requester.properties.Property;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetRequest extends Request {

    public GetRequest(String uri, Map<Property, Object> parameters) {
        super.httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
        String builtUri = Requester.HOST + uri;

        String regex = ":[^\\/\\n]*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(builtUri);

        while (matcher.find()) {
            if (Objects.equals(matcher.group(0), ":")) continue;
            String replacement = getParametersElement(parameters, matcher.group(0).substring(1));
            builtUri = builtUri.replace(matcher.group(0), replacement);
        }

        super.httpRequest = HttpRequest.newBuilder().GET().uri(URI.create(builtUri)).setHeader("User-Agent", Requester.AGENT_NAME).build();
    }

    private String getParametersElement(Map<Property, Object> parameters, String key) {
        return parameters.entrySet().stream().filter(entry -> entry.getKey().name().equals(key)).findFirst().map(entry -> entry.getValue().toString()).orElse(null);
    }
}
