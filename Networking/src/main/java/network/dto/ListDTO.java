package network.dto;

import java.util.ArrayList;

public class ListDTO<T> {
    private ArrayList<T> items;

    public ListDTO(ArrayList<T> items) {
        this.items = items;
    }

    public ListDTO() {
    }

    public ArrayList<T> getItems() {
        return items;
    }

    public void setItems(ArrayList<T> items) {
        this.items = items;
    }

}
