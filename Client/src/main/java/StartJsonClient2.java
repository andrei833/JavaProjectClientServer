import com.sun.tools.jconsole.JConsoleContext;
import model.Race;
import model.Registration;
import network.jsonprotocol.ServicesJsonProxy;
import observer.ClientObserver;
import services.IObserver;
import services.IServices;
import services.ServiceException;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class StartJsonClient2 {
    private static final int defaultChatPort = 55555;
    private static final String defaultServer = "localhost";

    public static void main(String []args){

        System.out.println("In start");
        Properties clientProps = new Properties();
        try {
            clientProps.load(StartJsonClient.class.getResourceAsStream("/client.properties"));
            System.out.println("Client properties set. ");
            clientProps.list(System.out);
        } catch (IOException e) {
            System.err.println("Cannot find chatclient.properties " + e);
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
        IServices server = new ServicesJsonProxy(serverIP, serverPort);

        try {
            String randomString = String.valueOf((int) (Math.random() * 100000) % 100000);
            ((ServicesJsonProxy) server).initializeConnection();

            IObserver observer = new ClientObserver();
            server.login(randomString, observer);


        } catch (ServiceException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }

    }
}
