package fr.thomasfar.requester.routes;

import fr.thomasfar.requester.properties.Property;

import java.util.List;

public interface Route {

    String getUri();

    DefaultType getMethod();

    List<Property> getProperties();
}
