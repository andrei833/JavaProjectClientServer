package network.dto;

import model.Race;

public class RaceCountDTO {
    private Race race;
    private int count;

    public Race getRace() {
        return race;
    }

    public int getCount() {
        return count;
    }

    public RaceCountDTO(Race race, int count) {
        this.race = race;
        this.count = count;
    }
}
