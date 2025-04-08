package network.utils;

import network.jsonprotocol.ClientJsonWorker;
import services.IServices;

import java.net.Socket;

public class JsonConcurrentServer extends AbsConcurrentServer{
    private final IServices chatServer;

    public JsonConcurrentServer(int port, IServices chatServer) {
        super(port);
        this.chatServer = chatServer;
        System.out.println("Chat - ChatJsonConcurrentServer");
    }

    @Override
    protected Thread createWorker(Socket client) {
        ClientJsonWorker worker = new ClientJsonWorker(chatServer, client);
        Thread tw = new Thread(worker);
        return tw;
    }
}
