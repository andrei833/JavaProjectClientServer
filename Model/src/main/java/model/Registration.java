package model;

import java.io.Serializable;

public class Registration implements Serializable, HasId<Integer> {
    private int id;
    private int participantId;
    private int raceId;

    public Registration() {
        this.id = 0;
        this.participantId = 0;
        this.raceId = 0;
    }

    public Registration(int id, int participantId, int raceId) {
        this.id = id;
        this.participantId = participantId;
        this.raceId = raceId;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public void setRaceId(int raceId) {
        this.raceId = raceId;
    }

    public Integer getParticipantId() {
        return participantId;
    }

    public void setParticipantId(int participantId) {
        this.participantId = participantId;
    }

    public Integer getRaceId() {
        return raceId;
    }

    @Override
    public String toString() {
        return "Registration [id=" + id + ", participantId=" + participantId + ", raceId=" + raceId;
    }
}
