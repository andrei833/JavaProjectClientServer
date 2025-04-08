package observer;

import model.Registration;
import services.IObserver;

public class ClientObserver implements IObserver {


    @Override
    public void createdRegistration(Registration reg) {
        System.out.println("[ClientObserver] Received update from server: " + reg);
    }

}
