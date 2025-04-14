package observer;

import model.Registration;
import services.IObserver;

public class ClientObserver implements IObserver {
    @Override
    public void update(){
        System.out.println("!!!New update from server");
    }

}
