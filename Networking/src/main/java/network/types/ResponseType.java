package network.types;

public enum ResponseType {
    LOGIN_RESPONSE,
    LOGOUT_RESPONSE,
    CREATE_REGISTRATION_RESPONSE,
    UPDATE_RESPONSE, // Add more response types as needed
    LIST_RESPONSE,
    ERROR_RESPONSE;

    @Override
    public String toString() {
        return name();
    }
}
