package network.jsonprotocol;

import com.google.gson.Gson;
import network.dto.CreateRegistrationDTO;
import network.types.RequestType;
import network.types.ResponseType;

public class JsonProtocolUtils {

    private static final Gson gson = new Gson();

    public static JsonRequest createRegistrationRequest(CreateRegistrationDTO data) {
        JsonRequest req = new JsonRequest();
        req.setType(RequestType.CREATE_REGISTRATION);
        req.setData(gson.toJson(data));
        return req;
    }

    public static JsonRequest createGetRacesRequest(){
        JsonRequest req = new JsonRequest();
        req.setType(RequestType.GET_RACES);
        req.setData(gson.toJson(null));
        return req;
    }

    public static JsonRequest createGetParticipantsRequest() {
        JsonRequest req = new JsonRequest();
        req.setType(RequestType.GET_PARTICIPANTS);
        req.setData(gson.toJson(null));  // No extra data needed
        return req;
    }

    public static JsonRequest createGetRegistrationsRequest() {
        JsonRequest req = new JsonRequest();
        req.setType(RequestType.GET_REGISTRATIONS);
        req.setData(gson.toJson(null));  // No extra data needed
        return req;
    }

    public static JsonRequest createLoginRequest(String clientId) {
        JsonRequest req = new JsonRequest();
        req.setType(RequestType.LOGIN);
        req.setData(gson.toJson(clientId));  // serialize clientId as JSON string
        return req;
    }

    public static JsonRequest createLogoutRequest(String clientId) {
        JsonRequest req = new JsonRequest();
        req.setType(RequestType.LOGOUT);
        req.setData(gson.toJson(clientId));  // serialize clientId as JSON string
        return req;
    }

    // For generic response types
    public static JsonResponse createResponse(String status, String data, ResponseType responseType) {
        return new JsonResponse(status, data, responseType);
    }

    public static JsonResponse createListResponse(String status, String data) {
        return new JsonResponse(status, data, ResponseType.LIST_RESPONSE);
    }

    public static JsonResponse createLoginResponse(String status, String data) {
        return new JsonResponse(status, data, ResponseType.LOGIN_RESPONSE);
    }

    public static JsonResponse createLogoutResponse(String status, String data) {
        return new JsonResponse(status, data, ResponseType.LOGOUT_RESPONSE);
    }

    public static JsonResponse createCreateRegistrationResponse(String status, String data) {
        return new JsonResponse(status, data, ResponseType.CREATE_REGISTRATION_RESPONSE);
    }

    public static JsonResponse createUpdateResponse(String status, String data){
        return new JsonResponse(status,data, ResponseType.UPDATE_RESPONSE);
    }


    public static JsonResponse createErrorResponse(String errorMessage) {
        return new JsonResponse("ERROR", errorMessage, ResponseType.ERROR_RESPONSE);
    }

}

