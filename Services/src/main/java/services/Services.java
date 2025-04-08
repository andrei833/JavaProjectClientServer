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

public class Services implements IServices {
    private final IRepo<Integer, Race> raceRepo;
    private final IRepo<Integer, Participant> participantRepo;
    private final IRepo<Integer, Registration> registrationRepo;
    private final Map<String, IObserver> loggedClients;

    public Services(
            RaceRepository raceRepo,
            ParticipantRepository participantRepo,
            RegistrationRepository registrationRepo
    ) {
        this.raceRepo = raceRepo;
        this.participantRepo = participantRepo;
        this.registrationRepo = registrationRepo;
        loggedClients = new ConcurrentHashMap<>();
    }

    public Services() {
        this.raceRepo = new RaceRepository();
        this.participantRepo = new ParticipantRepository();
        this.registrationRepo = new RegistrationRepository();
        loggedClients = new ConcurrentHashMap<>();
    }


    // Notify all registered clients (observers) about the new registration
    private void notifyObservers(Registration registration) {
        for (IObserver observer : loggedClients.values()) {
            try {
                observer.createdRegistration(registration);  // Notify the observer
            } catch (ServiceException e) {
                System.err.println("Error notifying observer: " + e.getMessage());
            }
        }
    }

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

            // Notify all registered clients (observers)
            notifyObservers(newRegistration);  // Notify clients of the new registration
        }
    }

    @Override
    public synchronized void login(String clientId, IObserver clientObserver) throws ServiceException {
        if (loggedClients.containsKey(clientId)) {
            throw new ServiceException("Client already logged in.");
        }

        loggedClients.put(clientId, clientObserver);
        System.out.println("Client " + clientId + " logged in.");

    }

    @Override
    public synchronized void logout(String clientId, IObserver clientObserver) throws ServiceException {
        // Check if the client is logged in
        clientObserver = loggedClients.remove(clientId);
        if (clientObserver == null) {
            throw new ServiceException("Client not logged in.");
        }

        System.out.println("Client " + clientId + " logged out.");

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
        ArrayList<Registration> registrations = registrationRepo.getAll().stream()
                .filter(registration -> registration.getRaceId().equals(race.getId()))
                .collect(Collectors.toCollection(ArrayList::new));

        return participantRepo.getAll().stream()
                .filter(participant -> registrations.stream()
                        .anyMatch(registration -> registration.getParticipantId().equals(participant.getId())))
                .collect(Collectors.toList());
    }
}