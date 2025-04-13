package service;


import model.Participant;
import model.Race;
import model.Registration;
import repository.IRepo;
import repository.ParticipantRepository;
import repository.RaceRepository;
import repository.RegistrationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class LocalService {
    private final IRepo<Integer, Race> raceRepo;
    private final IRepo<Integer, Participant> participantRepo;
    private final IRepo<Integer, Registration> registrationRepo;

    public LocalService(
            RaceRepository raceRepo,
            ParticipantRepository participantRepo,
            RegistrationRepository registrationRepo
    ) {
        this.raceRepo = raceRepo;
        this.participantRepo = participantRepo;
        this.registrationRepo = registrationRepo;
    }

    public LocalService() {
        this.raceRepo = new RaceRepository();
        this.participantRepo = new ParticipantRepository();
        this.registrationRepo = new RegistrationRepository();
    }

    public Map<Race, Integer> getAllRacesWithParticipantCount() {
        return raceRepo.getAll().stream()
                .collect(Collectors.toMap(
                        race -> race,
                        race -> (int) registrationRepo.getAll().stream()
                                .filter(registration -> registration.getRaceId().equals(race.getId())).count()
                ));
    }

    public List<Participant> getAllParticipantsFromSpecificRace(Race race) {
        ArrayList<Registration> registrations = registrationRepo.getAll().stream().filter(registration -> registration.getRaceId().equals(race.getId())).collect(Collectors.toCollection(ArrayList::new));
        return participantRepo.getAll().stream().filter(participant -> {
            for (Registration registration : registrations) {
                if (registration.getParticipantId().equals(participant.getId())) {
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
    }

    private final Object idLock = new Object();

    public void createRegistration(String participantName, int participantAge, Integer raceID) {
        if(raceRepo.getAll().stream().noneMatch(race -> race.getId().equals(raceID))){
        }

        synchronized (idLock) {
            Optional<Participant> existingParticipant = participantRepo.getAll().stream()
                    .filter(p -> p.getName().equals(participantName) && p.getAge() == participantAge)
                    .findFirst();

            Integer participantId;
            if (existingParticipant.isPresent()) {
                participantId = existingParticipant.get().getId();
            } else {
                participantId = participantRepo.getAll().stream()
                        .map(Participant::getId)
                        .max(Integer::compareTo)
                        .orElse(0) + 1;

                participantRepo.add(new Participant(participantId, participantName, participantAge));
            }

            int newRegistrationId = registrationRepo.getAll().stream()
                    .map(Registration::getId)
                    .max(Integer::compareTo)
                    .orElse(0) + 1; // Default to 1 if the repository is empty

            registrationRepo.add(new Registration(newRegistrationId, participantId, raceID));
        }
    }

}
