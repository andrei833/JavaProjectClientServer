package network.jsonprotocol;

public class JsonRequest {
    private String type;
    private String data;

    public JsonRequest() {
    }

    public JsonRequest(String type, String data) {
        this.type = type;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
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
                "type='" + type + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
