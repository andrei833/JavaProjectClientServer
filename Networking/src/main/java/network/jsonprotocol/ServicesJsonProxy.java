package network.jsonprotocol;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Participant;
import model.Race;
import model.Registration;
import network.dto.RaceCountDTO;
import network.dto.RegistrationDTO;
import services.IObserver;
import services.IServices;
import services.ServiceException;

import java.io.*;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

public class ServicesJsonProxy implements IServices {
    private final String host;
    private final int port;

    private IObserver client;

    private BufferedReader input;
    private PrintWriter output;
    private Gson gsonFormatter;
    private Socket connection;

    private final BlockingQueue<JsonResponse> responses;
    private volatile boolean finished;

    public ServicesJsonProxy(String host, int port) {
        this.host = host;
        this.port = port;
        responses = new LinkedBlockingQueue<>();
    }

    @Override
    public void login(String clientId, IObserver client) throws ServiceException {
        initializeConnection();
        JsonRequest req = new JsonRequest("LOGIN", clientId);
        sendRequest(req);

        JsonResponse response = readResponse();
        if ("OK".equals(response.getStatus())) {
            this.client = client;
            return;
        }else {
            closeConnection();
            throw new ServiceException(response.getData());
        }
    }

    @Override
    public void logout(String clientId, IObserver clientObserver) throws ServiceException {
        JsonRequest request = new JsonRequest("LOGOUT", clientId);
        sendRequest(request);
        JsonResponse response = readResponse();
        closeConnection();
        if (!"OK".equals(response.getStatus())) {
            throw new ServiceException(response.getData());
        }
    }


    @Override
    public Map<Race, Integer> getAllRacesWithParticipantCount() throws ServiceException {
        initializeConnection();

        JsonRequest request = new JsonRequest("GET_ALL_RACES", null);
        sendRequest(request);

        JsonResponse response = readResponse();

        if (!"OK".equals(response.getStatus())) {
            throw new RuntimeException(response.getData()); // handle error
        }

        Type listType = new TypeToken<List<RaceCountDTO>>() {}.getType();
        List<RaceCountDTO> raceCountDTOList = new Gson().fromJson(response.getData(), listType);

        Map<Race, Integer> result = raceCountDTOList.stream()
                .collect(Collectors.toMap(RaceCountDTO::getRace, RaceCountDTO::getCount));

        return result;
    }

    @Override
    public List<Participant> getAllParticipantsFromSpecificRace(Race race) throws ServiceException {
        initializeConnection();

        Gson gson = new Gson();
        String raceJson = gson.toJson(race);

        JsonRequest request = new JsonRequest("GET_PARTICIPANTS_FOR_RACE", raceJson);
        sendRequest(request);

        JsonResponse response = readResponse();
        closeConnection();

        if (!"OK".equals(response.getStatus())) {
            throw new RuntimeException(response.getData());
        }

        Type listType = new TypeToken<List<Participant>>() {}.getType();
        return gson.fromJson(response.getData(), listType);
    }


    @Override
    public void createRegistration(String name, int age, Integer raceId) throws ServiceException {
        initializeConnection();

        RegistrationDTO registration = new RegistrationDTO(name, age, raceId);

        JsonRequest request = JsonProtocolUtils.createRegistrationRequest(registration);
        sendRequest(request);

        JsonResponse response = readResponse();
        closeConnection();

        if (!"OK".equals(response.getStatus())) {
            throw new ServiceException(response.getData());
        }

        JsonResponse newRegistrationResponse = new JsonResponse("OK", gsonFormatter.toJson(registration), "NEW_REGISTRATION");
        try {
            responses.put(newRegistrationResponse); // Notify all connected clients about the new registration
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void initializeConnection() throws ServiceException {
        try {
            gsonFormatter = new Gson();
            connection = new Socket(host, port);
            output = new PrintWriter(connection.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            finished = false;
            startReader();
        } catch (IOException e) {
            throw new ServiceException("Failed to connect: " + e.getMessage());
        }
    }

    private void sendRequest(JsonRequest request) throws ServiceException {
        String reqJson = gsonFormatter.toJson(request);
        try {
            output.println(reqJson);
            output.flush();
        } catch (Exception e) {
            throw new ServiceException("Failed to send request: " + e.getMessage());
        }
    }

    private JsonResponse readResponse() throws ServiceException {
        try {
            return responses.take();
        } catch (InterruptedException e) {
            throw new ServiceException("Failed to read response: " + e.getMessage());
        }
    }

    private void closeConnection() {
        finished = true;
        try {
            if (input != null) input.close();
            if (output != null) output.close();
            if (connection != null) connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startReader() {
        Thread tw = new Thread(new ReaderThread());
        tw.start();
    }


    private void handleNewRegistration(JsonResponse response) {
        String registrationData = response.getData();
        Registration newRegistration = gsonFormatter.fromJson(registrationData, Registration.class);

        // Notify the client observer about the new registration
        if (client != null) {
            try {
                client.createdRegistration(newRegistration);
            } catch (ServiceException e) {
                throw new RuntimeException(e);
            }
        }

        System.out.println("New Registration received: " + newRegistration);
    }

    private boolean isNewRegistration(String data) {
        return data != null && data.contains("NEW_REGISTRATION");
    }

    private class ReaderThread implements Runnable {
        public void run() {
            while (!finished) {
                try {
                    String responseLine = input.readLine();
                    System.out.println("Response received: " + responseLine);

                    JsonResponse response = gsonFormatter.fromJson(responseLine, JsonResponse.class);

                    if ("NEW_REGISTRATION".equals(response.getType())) {
                        handleNewRegistration(response);
                    } else {
                        try {
                            responses.put(response);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Reading error: " + e);
                }
            }
        }
    }


}
