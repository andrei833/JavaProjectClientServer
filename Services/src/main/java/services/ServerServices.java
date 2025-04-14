package services;

import model.Participant;
import model.Race;
import model.Registration;
import repository.IRepo;
import repository.ParticipantRepository;
import repository.RaceRepository;
import repository.RegistrationRepository;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ServerServices implements IServices {
    private final IRepo<Integer, Race> raceRepo;
    private final IRepo<Integer, Participant> participantRepo;
    private final IRepo<Integer, Registration> registrationRepo;
    private final Map<String, IObserver> loggedClients;

    public ServerServices(
            RaceRepository raceRepo,
            ParticipantRepository participantRepo,
            RegistrationRepository registrationRepo
    ) {
        this.raceRepo = raceRepo;
        this.participantRepo = participantRepo;
        this.registrationRepo = registrationRepo;
        loggedClients = new ConcurrentHashMap<>();
    }

    public ServerServices() {
        this.raceRepo = new RaceRepository();
        this.participantRepo = new ParticipantRepository();
        this.registrationRepo = new RegistrationRepository();
        loggedClients = new ConcurrentHashMap<>();
    }

    @Override
    public synchronized void login(String clientId, IObserver workerRefference) throws ServiceException {
        if (loggedClients.containsKey(clientId)) {
            throw new ServiceException("Client already logged in.");
        }
        loggedClients.put(clientId, workerRefference);
    }

    @Override
    public synchronized void logout(String clientId, IObserver workerRefference) throws ServiceException {
        // Check if the client is logged in
        IObserver removed = loggedClients.remove(clientId);
        if (removed == null) {
            throw new ServiceException("Client not logged in.");
        }
        System.out.println("Client " + clientId + " logged out.");
    }

    public List<Race> getRaces() {
        return new ArrayList<>(raceRepo.getAll());
    }

    public List<Participant> getParticipants() {
        return new ArrayList<>(participantRepo.getAll());
    }

    public List<Registration> getRegistrations() {
        return new ArrayList<>(registrationRepo.getAll());
    }

    @Override
    public void createRegistration(String participantName, int participantAge, Integer raceID) throws ServiceException {
        if (raceRepo.getAll().stream().noneMatch(race -> race.getId().equals(raceID))) {
            throw new ServiceException("No race found with the given ID.");
        }

        synchronized (this) {
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
                    .orElse(0) + 1;

            Registration newRegistration = new Registration(newRegistrationId, participantId, raceID);
            registrationRepo.add(newRegistration);
        }


        for (IObserver observer : loggedClients.values()) {
            observer.update();
        }

    }

    @Override
    public Map<Race, Integer> getAllRacesWithParticipantCount() {
        return raceRepo.getAll().stream()
                .collect(Collectors.toMap(
                        race -> race,
                        race -> (int) registrationRepo.getAll().stream()
                                .filter(registration -> registration.getRaceId().equals(race.getId())).count()
                ));
    }

    @Override
    public List<Participant> getAllParticipantsFromSpecificRace(Race race) {
        ArrayList<Registration> registrations = registrationRepo.getAll().stream()
                .filter(registration -> registration.getRaceId().equals(race.getId()))
                .collect(Collectors.toCollection(ArrayList::new));

        return participantRepo.getAll().stream()
                .filter(participant -> registrations.stream()
                        .anyMatch(registration -> registration.getParticipantId().equals(participant.getId())))
                .collect(Collectors.toList());
    }
}