import network.utils.AbstractServer;
import network.utils.JsonConcurrentServer;
import repository.ParticipantRepository;
import repository.RaceRepository;
import repository.RegistrationRepository;
import services.IServices;
import services.Services;

import java.rmi.ServerException;
import java.util.Properties;

public class StartJsonServer {
    private static final int defaultPort = 55555;
    public static void main(String[] args) {
        Properties serverProps = new Properties();
        try {
            serverProps.load(StartJsonServer.class.getResourceAsStream("/Server.properties"));
            System.out.println("Server properties set. ");
            serverProps.list(System.out);
        } catch (Exception e) {
            System.err.println("Cannot find server.properties " + e);
        }

        //To add jdbc connection to repository
        RegistrationRepository registrationRepo = new RegistrationRepository();
        ParticipantRepository participantRepo = new ParticipantRepository();
        RaceRepository raceRepo = new RaceRepository();

        IServices service = new Services(raceRepo,participantRepo,registrationRepo);

        int chatServerPort = defaultPort;
        try {
            chatServerPort = Integer.parseInt(serverProps.getProperty("server.port"));
        } catch (NumberFormatException nef) {
            System.err.println("Wrong Port Number" + nef.getMessage());
            System.err.println("Using default port " + defaultPort);
        }

        System.out.println("Starting server on port: " + chatServerPort);

        AbstractServer server = new JsonConcurrentServer(chatServerPort, service);

        try {
            server.start();
        } catch (ServerException e) {
            System.err.println("Error starting the server" + e.getMessage());
        }

    }
}
