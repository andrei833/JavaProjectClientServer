package network.dto;

import java.io.Serializable;

public class RaceDTO implements Serializable {
    private int id;
    private int distance;
    private String style;
    private int numberOfParticipants;

    public RaceDTO() {
        // Default constructor
    }

    public RaceDTO(int id, int distance, String style, int numberOfParticipants) {
        this.id = id;
        this.distance = distance;
        this.style = style;
        this.numberOfParticipants = numberOfParticipants;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
        return "RaceDTO [id=" + id + ", distance=" + distance + ", style=" + style + ", numberOfParticipants=" + numberOfParticipants + "]";
    }
}
