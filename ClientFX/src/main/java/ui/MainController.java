package ui;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Participant;
import model.Race;
import model.Registration;
import network.jsonprotocol.ServicesJsonProxy;
import service.ClientService;
import services.IObserver;
import services.IServices;
import services.ServiceException;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class MainController implements IObserver {

    @FXML
    private TableView<Race> raceTableView;
    @FXML
    private TableColumn<Race, String> raceColumn;
    @FXML
    private TableColumn<Race, Integer> participantColumn;
    @FXML
    private ListView<String> participantListView;
    @FXML
    private TextField participantNameField;
    @FXML
    private TextField participantAgeField;
    @FXML
    private Button registerButton;

    private IServices server;
    private ClientService localService;

    private Race selectedRace = null;

    private static final int defaultChatPort = 55555;
    private static final String defaultServer = "localhost";

    @FXML
    public void initialize() {
        Properties clientProps = new Properties();
        try {
            clientProps.load(MainController.class.getResourceAsStream("/client.properties"));
            System.out.println("Client properties set. ");
            clientProps.list(System.out);
        } catch (IOException e) {
            System.err.println("Cannot find client.properties " + e);
            return;
        }

        String serverIP = clientProps.getProperty("chat.server.host", defaultServer);
        int serverPort = defaultChatPort;

        try {
            serverPort = Integer.parseInt(clientProps.getProperty("chat.server.port"));
        } catch (NumberFormatException ex) {
            System.err.println("Wrong port number " + ex.getMessage());
            System.out.println("Using default port: " + defaultChatPort);
        }

        server = new ServicesJsonProxy(serverIP, serverPort);

        try {
            // Generate a random client ID
            String clientId = String.valueOf((int) (Math.random() * 100000));
            server.login(clientId, this);

            // Initial data load from server
            List<Race> races = server.getRaces();
            List<Participant> participants = server.getParticipants();
            List<Registration> registrations = server.getRegistrations();

            // Initialize local service with server data
            localService = new ClientService(races, participants, registrations);

            setupRaceTable();

            // Set listener for selection in race table
            raceTableView.getSelectionModel().selectedItemProperty().addListener(
                    (obs, oldSelection, newSelection) -> {
                        if (newSelection != null) {
                            loadParticipantsForRace(newSelection);
                        }
                    }
            );

            // Register button action
            registerButton.setOnAction(event -> registerParticipant());
            update();

        } catch (ServiceException e) {
            e.printStackTrace();
            showAlert("Error", "Could not connect to server", e.getMessage());
        }
    }

    private void setupRaceTable() {
        Map<Race, Integer> racesWithParticipantCount = localService.getAllRacesWithParticipantCount();

        raceTableView.getItems().clear();
        raceColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStyle()));
        participantColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(racesWithParticipantCount.getOrDefault(cellData.getValue(), 0)).asObject()
        );

        raceTableView.getItems().addAll(racesWithParticipantCount.keySet());
    }

    private void loadParticipantsForRace(Race race) {
        List<Participant> participants = localService.getAllParticipantsFromSpecificRace(race);
        ObservableList<String> participantStrings = FXCollections.observableArrayList();

        for (Participant p : participants) {
            participantStrings.add(p.getName() + " (ID: " + p.getId() + ")");
        }

        participantListView.setItems(participantStrings);
    }

    private void registerParticipant() {
        String name = participantNameField.getText().trim();
        String ageText = participantAgeField.getText().trim();

        if (name.isEmpty() || ageText.isEmpty()) {
            showAlert("Input Error", "Missing Data", "Please fill in both name and age fields.");
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageText);
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Invalid Age", "Age must be a number.");
            return;
        }

        Race selectedRace = raceTableView.getSelectionModel().getSelectedItem();
        if (selectedRace == null) {
            showAlert("Selection Error", "No Race Selected", "Please select a race.");
            return;
        }

        // Send the registration request to the server
        try {
            server.createRegistration(name, age, selectedRace.getId());
        } catch (ServiceException e) {
            showAlert("Error", "Registration Failed", e.getMessage());
        }
    }

    @Override
    public void update() {
        javafx.application.Platform.runLater(() -> {
            // Declare the lists for races, participants, and registrations
            List<Race> races = null;
            List<Participant> participants = null;
            List<Registration> registrations = null;

            // Fetch the latest data from the server
            try {
                races = server.getRaces();
                participants = server.getParticipants();
                registrations = server.getRegistrations();
            } catch (ServiceException e) {
                throw new RuntimeException(e);
            }

            // Update the local service with the new data
            localService.updateData(races, participants, registrations);

            // Store the previously selected race (if any) before updating the table
            Race currentSelection = raceTableView.getSelectionModel().getSelectedItem();
            if (currentSelection != null) {
                selectedRace = currentSelection; // Save the selected race
            }

            // Refresh the race table with the updated data
            setupRaceTable();

            // After the table is refreshed, check if the selected race still exists in the new list by ID
            if (selectedRace != null) {
                // Check if any race in the updated list has the same ID as the selected race
                Race raceById = races.stream()
                        .filter(race -> race.getId() == selectedRace.getId())
                        .findFirst()
                        .orElse(null);

                // If the race exists by ID, reselect it in the table
                if (raceById != null) {
                    raceTableView.getSelectionModel().select(raceById);
                    loadParticipantsForRace(raceById);  // Reload the participants for the selected race
                }
            }
        });
    }



    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
