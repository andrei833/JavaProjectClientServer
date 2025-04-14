package network.types;

public enum RequestType {
    LOGIN,
    LOGOUT,
    CREATE_REGISTRATION,
    GET_RACES,
    GET_PARTICIPANTS,
    GET_REGISTRATIONS,
    UPDATE;

    @Override
    public String toString() {
        return name();
    }
}

