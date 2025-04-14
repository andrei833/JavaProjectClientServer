package network.jsonprotocol;

import com.google.gson.Gson;
import network.dto.*;
import services.IObserver;
import services.IServices;
import services.ServiceException;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.stream.Collectors;


public class ClientJsonWorker implements Runnable, IObserver {
  private final IServices server;
  private final Socket connection;
  private BufferedReader input;
  private PrintWriter output;
  private final Gson gson;
  private volatile boolean connected;


  public ClientJsonWorker(IServices server, Socket connection) {
    this.server = server;
    this.connection = connection;
    gson = new Gson();
    try {
      output = new PrintWriter(connection.getOutputStream(), true);
      input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      connected = true;
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private JsonResponse handleRequest(JsonRequest request) {
    try {
      switch (request.getType()) {
        case LOGIN:
          String clientId = gson.fromJson(request.getData(), String.class);
          server.login(clientId, this);
          return JsonProtocolUtils.createLoginResponse("OK", "Login successful");

        case LOGOUT:
          clientId = gson.fromJson(request.getData(), String.class);
          server.logout(clientId, this);
          return JsonProtocolUtils.createLogoutResponse("OK", "Logout successful");

        case CREATE_REGISTRATION:
          CreateRegistrationDTO regDto = gson.fromJson(request.getData(), CreateRegistrationDTO.class);
          server.createRegistration(regDto.getParticipantName(),regDto.getParticipantAge(),regDto.getRegistrationId());
          return JsonProtocolUtils.createCreateRegistrationResponse("OK","Registration Created Succesfully not checking if the user is in the race already");

        case GET_RACES:
          ArrayList<RaceDTO> raceDTOS = server.getRaces().stream().map(i ->{
              return new RaceDTO(i.getId(),i.getDistance(),i.getStyle(),i.getNumberOfParticipants());
          }).collect(Collectors.toCollection(ArrayList::new));
          ListDTO<RaceDTO> RaceDtoList = new ListDTO<RaceDTO>(raceDTOS);
          return JsonProtocolUtils.createListResponse("OK", gson.toJson(RaceDtoList));

        case GET_PARTICIPANTS:
          ArrayList<ParticipantDTO> participantDTOS = server.getParticipants().stream().map(p -> {
            return new ParticipantDTO(p.getId(), p.getName(), p.getAge());
          }).collect(Collectors.toCollection(ArrayList::new));
          ListDTO<ParticipantDTO> participantDtoList = new ListDTO<>(participantDTOS);
          return JsonProtocolUtils.createListResponse("OK", gson.toJson(participantDtoList));

        case GET_REGISTRATIONS:
          ArrayList<RegistrationDTO> registrationDTOS = server.getRegistrations().stream().map(r -> {
            return new RegistrationDTO(r.getId(), r.getParticipantId(), r.getRaceId());
          }).collect(Collectors.toCollection(ArrayList::new));
          ListDTO<RegistrationDTO> registrationDtoList = new ListDTO<>(registrationDTOS);
          return JsonProtocolUtils.createListResponse("OK", gson.toJson(registrationDtoList));

        default:
          return JsonProtocolUtils.createErrorResponse("Unknown request type");
      }
    } catch (ServiceException e) {
      return JsonProtocolUtils.createErrorResponse("Service error " + e.getMessage());
    } catch (Exception e) {
      return JsonProtocolUtils.createErrorResponse("Unexpected error: " + e.getMessage());
    }
  }

  @Override
  public void update(){
    JsonResponse response = JsonProtocolUtils.createUpdateResponse("OK", "UPDATE BRO UPDATE");
    sendResponse(response);
  }

  public void run() {
    while (connected) {
      try {
        String requestLine = input.readLine();
        if (requestLine != null) {
          JsonRequest request = gson.fromJson(requestLine, JsonRequest.class);
          JsonResponse response = handleRequest(request);
          if (response != null) {
            sendResponse(response);
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
        connected = false;
      }

      try {
        Thread.sleep(100); // a small delay to avoid tight loop
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    try {
      input.close();
      output.close();
      connection.close();
    } catch (IOException e) {
      System.out.println("Error closing connection: " + e);
    }
  }

  private void sendResponse(JsonResponse response) {
    String json = gson.toJson(response);
    output.println(json);
    output.flush();
  }

}
