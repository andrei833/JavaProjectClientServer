package network.utils;

import model.Registration;
import network.jsonprotocol.ClientJsonWorker;
import services.IServices;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class JsonConcurrentServer extends AbsConcurrentServer{
    private final IServices chatServer;
    private final List<ClientJsonWorker> workers;

    public JsonConcurrentServer(int port, IServices chatServer) {
        super(port);
        workers = new ArrayList<>();
        this.chatServer = chatServer;
        System.out.println("Chat - ChatJsonConcurrentServer");
    }

    @Override
    protected Thread createWorker(Socket client) {
        ClientJsonWorker worker = new ClientJsonWorker(chatServer,this ,client);
        workers.add(worker);
        Thread tw = new Thread(worker);
        return tw;
    }

    public void broadcastNewRegistration(Registration reg) {
        for (ClientJsonWorker worker : workers) {
            worker.sendNotification(reg);
        }
    }


}
