package ui;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Participant;
import model.Race;
import network.jsonprotocol.ServicesJsonProxy;
import observer.ClientObserver;
import service.LocalService;
import services.ServiceException;
import services.IServices;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class MainController {

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
    private LocalService localService; // Local service handling local data
    private ServicesJsonProxy proxyService; // Proxy service handling server communication

    private ObservableList<Race> races;
    private ObservableList<Participant> participants;

    private static final int defaultChatPort = 55555;
    private static final String defaultServer = "localhost";

    @FXML
    public void initialize() {
        System.out.println("In start");

        // Load client properties for server communication
        Properties clientProps = new Properties();
        try {
            clientProps.load(MainController.class.getResourceAsStream("/client.properties"));
            System.out.println("Client properties set.");
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
        System.out.println("Using server IP " + serverIP);
        System.out.println("Using server port " + serverPort);

        // Initialize the proxy service (this will communicate with the server)
        proxyService = new ServicesJsonProxy(serverIP, serverPort);
        server = proxyService;
        // Initialize the local service (this handles local data operations)
        localService = new LocalService();

        // Login to the server
        try {
            String randomString = String.valueOf((int) (Math.random() * 100000) % 100000);
            proxyService.initializeConnection();
            server.login(randomString, new ClientObserver()); // Register as observer
        } catch (ServiceException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }

        raceTableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        loadParticipantsForRace(newSelection);
                    }
                });

        // Setup table and button
        setupRaceTable();
        registerButton.setOnAction(event -> handleRegistration());
    }

    private void setupRaceTable() {
        raceTableView.getItems().clear();

        // Get races using local service
        List<Race> raceList = localService.getAllRacesWithParticipantCount().keySet().stream().collect(Collectors.toList());
        races = FXCollections.observableArrayList(raceList);

        raceColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStyle()));
        participantColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(getParticipantCount(cellData.getValue())).asObject());
        raceTableView.setItems(races);
    }

    private int getParticipantCount(Race race) {
        // Count participants for a specific race using local service
        return localService.getAllParticipantsFromSpecificRace(race).size();
    }

    private void loadParticipantsForRace(Race race) {
        participantListView.getItems().clear();
        ObservableList<String> participantStrings = FXCollections.observableArrayList();

        // Load participants using local service
        List<Participant> participantList = localService.getAllParticipantsFromSpecificRace(race);
        for (Participant p : participantList) {
            participantStrings.add(p.getName() + " (ID: " + p.getId() + ")");
        }
        participantListView.setItems(participantStrings);
    }

    private void handleRegistration() {
        Race selectedRace = raceTableView.getSelectionModel().getSelectedItem();
        if (selectedRace == null) {
            showAlert("Error", "No race selected", "Please select a race first");
            return;
        }

        String name = participantNameField.getText().trim();
        String ageText = participantAgeField.getText().trim();

        if (name.isEmpty() || ageText.isEmpty()) {
            showAlert("Error", "Missing information", "Please enter both name and age");
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageText);
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid age", "Age must be a number");
            return;
        }

        // Register participant locally
        localService.createRegistration(name, age, selectedRace.getId());

        participantNameField.clear();
        participantAgeField.clear();
        loadParticipantsForRace(selectedRace);
        setupRaceTable();
        try {
            server.createRegistration(name,age,selectedRace.getId());
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
        showAlert("Success", "Registration created", "Participant " + name + " registered for " + selectedRace.getStyle());

    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void setServer(IServices server) {
        this.server = server;
    }
}
