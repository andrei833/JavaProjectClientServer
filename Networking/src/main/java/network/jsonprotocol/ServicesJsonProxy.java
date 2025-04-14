package network.jsonprotocol;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Participant;
import model.Race;
import model.Registration;
import network.dto.*;
import network.types.ResponseType;
import services.IObserver;
import services.IServices;
import services.ServiceException;

import java.io.*;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

public class ServicesJsonProxy implements IServices {
    private final String host;
    private final int port;
    private final Gson gson;

    private IObserver client;

    private BufferedReader input;
    private PrintWriter output;
    private Socket connection;

    private final BlockingQueue<JsonResponse> responses;
    private volatile boolean finished;

    public ServicesJsonProxy(String host, int port) {
        gson = new Gson();
        this.host = host;
        this.port = port;
        responses = new LinkedBlockingQueue<>();
    }

    @Override
    public void login(String clientId, IObserver client) throws ServiceException {
        initializeConnection();

        JsonRequest req = JsonProtocolUtils.createLoginRequest(clientId);
        sendRequest(req);

        JsonResponse response = readResponse();
        if ("OK".equals(response.getStatus())) {
            this.client = client;
        }else {
            closeConnection();
            throw new ServiceException(response.getData());
        }
    }

    @Override
    public void logout(String clientId, IObserver clientObserver) throws ServiceException {

        JsonRequest request = JsonProtocolUtils.createLogoutRequest(clientId);
        sendRequest(request);

        JsonResponse response = readResponse();

        closeConnection();
        if (!"OK".equals(response.getStatus())) {
            throw new ServiceException(response.getData());
        }
    }

    @Override
    public void createRegistration(String name, int age, Integer raceId) throws ServiceException {
        JsonRequest req = JsonProtocolUtils.createRegistrationRequest(new CreateRegistrationDTO(name,age,raceId));
        sendRequest(req);

        JsonResponse response = readResponse();
        System.out.println("createRegistration " + response);

    }

    public List<Race> getRaces() throws ServiceException {
        JsonRequest req = JsonProtocolUtils.createGetRacesRequest();
        sendRequest(req);

        JsonResponse response = readResponse();

        if (!"OK".equals(response.getStatus())) {
            throw new ServiceException("Failed to fetch races: " + response.getData());
        }

        Type listDtoType = new TypeToken<ListDTO<RaceDTO>>() {}.getType();
        ListDTO<RaceDTO> listDTO = gson.fromJson(response.getData(), listDtoType);

        ArrayList<Race> races = listDTO.getItems().stream()
                .map(raceDTO -> new Race(raceDTO.getId(), raceDTO.getDistance(), raceDTO.getStyle(), raceDTO.getNumberOfParticipants()))
                .collect(Collectors.toCollection(ArrayList::new));

        return races;
    }

    public List<Participant> getParticipants() throws ServiceException {
        JsonRequest req = JsonProtocolUtils.createGetParticipantsRequest();
        sendRequest(req);

        JsonResponse response = readResponse();

        if (!"OK".equals(response.getStatus())) {
            throw new ServiceException("Failed to fetch participants: " + response.getData());
        }

        Type listDtoType = new TypeToken<ListDTO<ParticipantDTO>>() {}.getType();
        ListDTO<ParticipantDTO> listDTO = gson.fromJson(response.getData(), listDtoType);

        ArrayList<Participant> participants = listDTO.getItems().stream()
                .map(participantDTO -> new Participant(participantDTO.getId(), participantDTO.getName(), participantDTO.getAge()))  // Assuming ParticipantDTO has these fields
                .collect(Collectors.toCollection(ArrayList::new));

        return participants;
    }

    public List<Registration> getRegistrations() throws ServiceException {
        JsonRequest req = JsonProtocolUtils.createGetRegistrationsRequest();
        sendRequest(req);

        JsonResponse response = readResponse();

        if (!"OK".equals(response.getStatus())) {
            throw new ServiceException("Failed to fetch registrations: " + response.getData());
        }

        Type listDtoType = new TypeToken<ListDTO<RegistrationDTO>>() {}.getType();
        ListDTO<RegistrationDTO> listDTO = gson.fromJson(response.getData(), listDtoType);

        ArrayList<Registration> registrations = listDTO.getItems().stream()
                .map(registrationDTO -> new Registration(registrationDTO.getId(), registrationDTO.getParticipantId(), registrationDTO.getRaceId()))  // Assuming RegistrationDTO has these fields
                .collect(Collectors.toCollection(ArrayList::new));

        return registrations;
    }

    @Override
    public Map<Race, Integer> getAllRacesWithParticipantCount() throws ServiceException {
        return null;

    }

    @Override
    public List<Participant> getAllParticipantsFromSpecificRace(Race race) throws ServiceException {
        return null;
    }



    private void handleUpdate(JsonResponse response) {
        String registrationData = response.getData();
        if (client != null) {
            client.update();
        }
    }

    private void sendRequest(JsonRequest request) throws ServiceException {
        String reqJson = gson.toJson(request);
        try {
            output.println(reqJson);
            output.flush();
        } catch (Exception e) {
            throw new ServiceException("Failed to send request: " + e.getMessage());
        }
    }

    private void initializeConnection() throws ServiceException {
        if (connection != null && !connection.isClosed()) {
            return; // connection already open, reuse it!
        }
        try {
            connection = new Socket(host, port);
            output = new PrintWriter(connection.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            finished = false;
            startReader();
        } catch (IOException e) {
            throw new ServiceException("Failed to connect: " + e.getMessage());
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

    private class ReaderThread implements Runnable {
        public void run() {
            while (!finished) {
                try {
                    String responseLine = input.readLine();
                    JsonResponse response = gson.fromJson(responseLine, JsonResponse.class);


                    if (ResponseType.UPDATE_RESPONSE.equals(response.getType())) {
                        handleUpdate(response);
                    }


                    else {
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
