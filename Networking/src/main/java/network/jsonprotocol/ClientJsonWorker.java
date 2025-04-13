package network.jsonprotocol;

import com.google.gson.Gson;
import model.Race;
import model.Registration;
import network.dto.RaceCountDTO;
import network.dto.RegistrationDTO;
import network.utils.JsonConcurrentServer;
import services.IObserver;
import services.IServices;
import services.ServiceException;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

public class ClientJsonWorker implements Runnable, IObserver {
  private final IServices server;
  private final Socket connection;
  private BufferedReader input;
  private PrintWriter output;
  private final Gson gsonFormatter;
  private volatile boolean connected;
  private final JsonConcurrentServer jsonConcurrentServer;  // Add this reference


  public ClientJsonWorker(IServices server, JsonConcurrentServer jsonConcurrentServer, Socket connection) {
    this.server = server;
    this.jsonConcurrentServer = jsonConcurrentServer;  // Initialize the reference
    this.connection = connection;
    gsonFormatter = new Gson();
    try {
      output = new PrintWriter(connection.getOutputStream(), true);
      input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      connected = true;
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void run() {
    while (connected) {
      try {
        String requestLine = input.readLine();
        if (requestLine != null) {
          JsonRequest request = gsonFormatter.fromJson(requestLine, JsonRequest.class);
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

  private JsonResponse handleRequest(JsonRequest request) {
    try {
      switch (request.getType()) {
        case "LOGIN":
          String clientId = request.getData();
          server.login(clientId, this);  // Login: register observer
          return new JsonResponse("OK", "Login successful", "LOGIN_RESPONSE");

        case "LOGOUT":
          clientId = request.getData();
          server.logout(clientId, this);  // Logout: unregister observer
          return new JsonResponse("OK", "Logout successful", "LOGOUT_RESPONSE");

        case "CREATE_REGISTRATION":
          RegistrationDTO registration = gsonFormatter.fromJson(request.getData(), RegistrationDTO.class);
          server.createRegistration(registration.getName(), registration.getAge(), registration.getRaceId());
          Registration newRegistration = new Registration();
          jsonConcurrentServer.broadcastNewRegistration(newRegistration);
          return new JsonResponse("OK", gsonFormatter.toJson(registration), "NEW_REGISTRATION");

        case "GET_PARTICIPANTS_FOR_RACE":
          // TODO: Implement as needed
          return new JsonResponse("ERROR", "Not implemented yet", "GET_PARTICIPANTS_FOR_RACE_RESPONSE");

        case "GET_ALL_RACES":
          Map<Race, Integer> raceMap = server.getAllRacesWithParticipantCount();
          List<RaceCountDTO> raceList = raceMap.entrySet().stream()
                  .map(entry -> new RaceCountDTO(entry.getKey(), entry.getValue()))
                  .collect(Collectors.toList());
          String raceMapJson = gsonFormatter.toJson(raceList);
          return new JsonResponse("OK", raceMapJson, "GET_ALL_RACES_RESPONSE");

        default:
          return new JsonResponse("ERROR", "Unknown request type: " + request.getType(), "ERROR_RESPONSE");
      }
    } catch (ServiceException e) {
      return new JsonResponse("ERROR", "Service error: " + e.getMessage(), "SERVICE_ERROR");
    } catch (Exception e) {
      return new JsonResponse("ERROR", "Unexpected error: " + e.getMessage(), "UNEXPECTED_ERROR");
    }
  }

  public void sendNotification(Registration reg) {
    JsonResponse response = new JsonResponse("OK", gsonFormatter.toJson(reg), "NEW_REGISTRATION");
    sendResponse(response);
  }

  private void sendResponse(JsonResponse response) {
    String json = gsonFormatter.toJson(response);
    output.println(json);
    output.flush();
  }

  @Override
  public void createdRegistration(Registration reg) {
    // You can notify the client here if needed
    JsonResponse response = new JsonResponse("OK", gsonFormatter.toJson(reg), "NEW_REGISTRATION");
    sendResponse(response);
  }
}
