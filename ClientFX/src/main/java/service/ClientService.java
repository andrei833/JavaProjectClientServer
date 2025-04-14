package service;


import model.Participant;
import model.Race;
import model.Registration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientService {

    private List<Race> races = new ArrayList<>();
    private List<Participant> participants = new ArrayList<>();
    private List<Registration> registrations = new ArrayList<>();

    public ClientService(List<Race> races, List<Participant> participants, List<Registration> registrations) {
        this.races = races;
        this.participants = participants;
        this.registrations = registrations;
    }

    public void updateData(List<Race> newRaces, List<Participant> newParticipants, List<Registration> newRegistrations) {
        this.races = new ArrayList<>(newRaces);
        this.participants = new ArrayList<>(newParticipants);
        this.registrations = new ArrayList<>(newRegistrations);
    }

    public List<Race> getAllRaces() {
        return new ArrayList<>(races);
    }

    public List<Participant> getAllParticipants() {
        return new ArrayList<>(participants);
    }

    public List<Registration> getAllRegistrations() {
        return new ArrayList<>(registrations);
    }

    public Map<Race, Integer> getAllRacesWithParticipantCount() {
        Map<Race, Integer> result = new HashMap<>();
        for (Race race : races) {
            int count = 0;
            for (Registration reg : registrations) {
                if (reg.getRaceId().equals(race.getId())) {
                    count++;
                }
            }
            result.put(race, count);
        }
        return result;
    }

    public List<Participant> getAllParticipantsFromSpecificRace(Race race) {
        List<Participant> result = new ArrayList<>();
        for (Registration reg : registrations) {
            if (reg.getRaceId().equals(race.getId())) {
                participants.stream()
                        .filter(p -> p.getId().equals(reg.getParticipantId()))
                        .findFirst()
                        .ifPresent(result::add);
            }
        }
        return result;
    }
}