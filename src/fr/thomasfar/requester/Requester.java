package fr.thomasfar.requester;

import fr.thomasfar.requester.routes.DefaultType;
import fr.thomasfar.requester.routes.ExampleRoute;
import fr.thomasfar.requester.properties.NullableProperty;
import fr.thomasfar.requester.properties.Property;
import fr.thomasfar.requester.requests.GetRequest;
import fr.thomasfar.requester.requests.OtherPostRequest;
import fr.thomasfar.requester.requests.PostRequest;
import fr.thomasfar.requester.requests.Request;
import fr.thomasfar.requester.routes.Route;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;


public class Requester {
    public static String AGENT_NAME = "Java 17 HttpClient DWI";
    public static String HOST = "http://polytech-s6-ihm-api.test";
    public static String REQUEST_TYPE = "application/json";

    Map<Property, Object> parameters = new HashMap<>();
    Map<Property, Object> nullableParameters = new HashMap<>();
    private int responseCode;
    private String responseJson;

    public Requester(Route route, Map<String, Object> parameters) {
        init(route, parameters);
    }

    public Requester(Route route, Object... parameters) {
        Map<String, Object> parametersMap = new HashMap<>();
        if (parameters.length % 2 != 0)
            throw new IllegalArgumentException("The parameter length must be an even number, the current parameter length is " + parameters.length + ".");
        int i = 0;
        while (i < parameters.length) {
            if (!(parameters[i] instanceof String))
                throw new IllegalArgumentException("Each odd parameter must be an identifier of type String for the even value (argument " + i + ").");
            parametersMap.put((String) parameters[i], parameters[i + 1]);
            i += 2;
        }
        init(route, parametersMap);
    }

    private void init(Route route, Map<String, Object> parameters) {
        // Première étape : vérifier que le nombre de paramètres est bon
        checkSizeOfParameters(route, parameters);
        // Deuxième étape : vérifier que tous les arguments obligatoires sont mis
        checkMandatoryParameters(route, parameters);
        // Troisième étape : vérifier que les types de paramètres sont correctes et ajouter dans le dictionnaire si c'est le cas
        defineParametersMap(route, parameters);
        // Quatrième étape : fusionner les dictionnaires des paramètres obligatoires et non obligatoires
        Map<Property, Object> sentParameters = new HashMap<>();
        sentParameters.putAll(this.parameters);
        sentParameters.putAll(this.nullableParameters);
        // Dernière étape : envoyer la requête
        Request request = switch ((DefaultType) route.getMethod()) {
            case GET -> new GetRequest(route.getUri(), sentParameters);
            case POST -> new PostRequest(route.getUri(), sentParameters);
            case DELETE -> new OtherPostRequest("DELETE", route.getUri(), sentParameters);
            case PUT -> new OtherPostRequest("PUT", route.getUri(), sentParameters);
            default -> null;
        };
        if (request != null) sendRequest(request.getHttpClient(), request.getHttpRequest());
    }

    private void defineParametersMap(Route route, Map<String, Object> parameters) {
        for (Property property : route.getProperties()) {
            if (property instanceof NullableProperty) {
                if (parameters.containsKey(property.name())) {
                    this.nullableParameters.put(property, parameters.get(property.name()));
                }
            } else {
                this.parameters.put(property, parameters.get(property.name()));
            }
        }
    }

    private void checkMandatoryParameters(Route route, Map<String, Object> parameters) {
        for (Property property : route.getProperties()) {
            if (!(property instanceof NullableProperty) && !parameters.containsKey(property.name())) {
                throw new IllegalArgumentException("The parameters do not contain the " + property.name() + " key which is mandatory for this request.");
            }
        }
    }

    private void checkSizeOfParameters(Route route, Map<String, Object> parameters) {
        int routeNullableParameters = 0;
        int routeMandatoryParameters = 0;
        for (Property property : route.getProperties()) {
            if (property instanceof NullableProperty) {
                routeNullableParameters++;
            } else {
                routeMandatoryParameters++;
            }
        }
        if (parameters.size() < routeMandatoryParameters)
            throw new IllegalArgumentException("The number of combination <String, Object> is not good, at least " + routeMandatoryParameters + " are needed and there is " + parameters.size() + ".");
        if (parameters.size() > routeMandatoryParameters + routeNullableParameters)
            throw new IllegalArgumentException("The number of combination <String, Object> is not good, at most " + (routeMandatoryParameters + routeNullableParameters) + " are needed and there is " + parameters.size() + ".");
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getResponseJson() {
        return responseJson;
    }

    private void sendRequest(HttpClient httpClient, HttpRequest request) {
        HttpResponse<String> response = null;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException | IOException e) {
            System.out.println(e.getMessage());
        }
        assert response != null;
        this.responseCode = response.statusCode();
        this.responseJson = response.body();
    }
}