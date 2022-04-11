package fr.thomasfar.requester.properties;

public class Property {
    public static final Property TOKEN_PROPERTY = new Property("token", String.class);
    public  static final Property SESSION_TOKEN_PROPERTY = new Property("session_token", String.class);
    private final String name;
    private final Object type;

    public Property(String property, Object type) {
        this.name = property;
        this.type = type;
    }

    public String name() {
        return name;
    }

    public Object type() {
        return type;
    }

}
