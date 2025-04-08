package model;

import java.io.Serializable;

public class Race implements Serializable, HasId<Integer> {
    private int id;
    private int distance;
    private String style;
    private int numberOfParticipants;

    public Race() {
        this.id = 0;
        this.distance = 0;
        this.style = "";
        this.numberOfParticipants = 0;
    }

    public Race(int id, int distance, String style, int numberOfParticipants) {
        this.id = id;
        this.distance = distance;
        this.style = style;
        this.numberOfParticipants = numberOfParticipants;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public int getNumberOfParticipants() {
        return numberOfParticipants;
    }

    public void setNumberOfParticipants(int numberOfParticipants) {
        this.numberOfParticipants = numberOfParticipants;
    }

    @Override
    public String toString() {
        return "Race [id=" + id + ", distance=" + distance + ", style=" + style + ", numberOfParticipants=" + numberOfParticipants + "]";
    }
}
