package network.jsonprotocol;

import com.google.gson.Gson;
import network.dto.RegistrationDTO;

public class JsonProtocolUtils {
    public static JsonRequest createRegistrationRequest(RegistrationDTO data) {
        JsonRequest req = new JsonRequest();
        req.setType("CREATE_REGISTRATION");
        req.setData(new Gson().toJson(data));
        return req;
    }
}
