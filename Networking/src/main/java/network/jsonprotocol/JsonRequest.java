package network.jsonprotocol;

import network.types.RequestType;

public class JsonRequest {
    private RequestType type;
    private String data;

    public JsonRequest() {}

    public JsonRequest(RequestType type, String data) {
        this.type = type;
        this.data = data;
    }

    public RequestType getType() {
        return type;
    }

    public void setType(RequestType type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "JsonRequest{" +
                "type=" + type +
                ", data='" + data + '\'' +
                '}';
    }
}

