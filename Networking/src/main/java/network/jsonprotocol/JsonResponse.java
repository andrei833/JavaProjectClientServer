package network.jsonprotocol;

public class JsonResponse {
    private String status;
    private String data;
    private String type;

    // Required no-arg constructor for Gson
    public JsonResponse() {
    }

    public JsonResponse(String status, String data, String type) {
        this.status = status;
        this.data = data;
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public String getData() {
        return data;
    }

    public String getType() {
        return type;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setType(String type) {
        this.type = type;
    }
}
