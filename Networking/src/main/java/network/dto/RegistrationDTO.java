package network.dto;


import java.io.Serializable;

public class RegistrationDTO implements Serializable {
    private int id;
    private int participantId;
    private int raceId;

    public RegistrationDTO() {
        // Default constructor
    }

    public RegistrationDTO(int id, int participantId, int raceId) {
        this.id = id;
        this.participantId = participantId;
        this.raceId = raceId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParticipantId() {
        return participantId;
    }

    public void setParticipantId(int participantId) {
        this.participantId = participantId;
    }

    public int getRaceId() {
        return raceId;
    }

    public void setRaceId(int raceId) {
        this.raceId = raceId;
    }

    @Override
    public String toString() {
        return "RegistrationDTO [id=" + id + ", participantId=" + participantId + ", raceId=" + raceId + "]";
    }
}
