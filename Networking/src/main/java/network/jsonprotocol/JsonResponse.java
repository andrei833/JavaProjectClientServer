package network.jsonprotocol;

import network.types.ResponseType;

public class JsonResponse {
    private String status;
    private String data;
    private ResponseType type;

    public JsonResponse(String status, String data, ResponseType type) {
        this.status = status;
        this.data = data;
        this.type = type;
    }

    // Getters and setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public ResponseType getType() {
        return type;
    }

    public void setType(ResponseType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "JsonResponse{" +
                "status='" + status + '\'' +
                ", data='" + data + '\'' +
                ", type=" + type +
                '}';
    }
}

