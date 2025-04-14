package network.utils;

import model.Registration;
import network.jsonprotocol.ClientJsonWorker;
import services.IServices;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class JsonConcurrentServer extends AbsConcurrentServer{
    private final IServices chatServer;

    public JsonConcurrentServer(int port, IServices chatServer) {
        super(port);
        this.chatServer = chatServer;
        System.out.println("Server has started");
    }

    @Override
    protected Thread createWorker(Socket client) {
        ClientJsonWorker worker = new ClientJsonWorker(chatServer, client);
        Thread tw = new Thread(worker);
        return tw;
    }

}
