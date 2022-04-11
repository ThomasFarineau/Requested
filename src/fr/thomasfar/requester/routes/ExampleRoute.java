package fr.thomasfar.requester.routes;

import fr.thomasfar.requester.properties.NullableProperty;
import fr.thomasfar.requester.properties.Property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum ExampleRoute implements Route {

    EXAMPLE("/api/test/:id", DefaultType.GET, new Property("id", Integer.class)),
    EXAMPLE2("/api/test/", DefaultType.POST, new Property("id", Integer.class), new NullableProperty("name", String.class));

    private final String uri;
    private final DefaultType method;
    private final List<Property> properties = new ArrayList<>();

    ExampleRoute(String uri, DefaultType method, Property... properties) {
        this.uri = uri;
        this.method = method;
        Collections.addAll(this.properties, properties);
    }

    public String getUri() {
        return uri;
    }

    public DefaultType getMethod() {
        return method;
    }

    public List<Property> getProperties() {
        return properties;
    }

}
