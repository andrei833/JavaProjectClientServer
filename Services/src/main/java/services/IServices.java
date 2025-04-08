package services;


import model.Participant;
import model.Race;

import java.util.List;
import java.util.Map;

public interface IServices {
  Map<Race, Integer> getAllRacesWithParticipantCount() throws ServiceException;
  List<Participant> getAllParticipantsFromSpecificRace(Race race) throws ServiceException;
  void createRegistration(String participantName, int participantAge, Integer raceID) throws ServiceException;
  void login(String clientId, IObserver client) throws ServiceException;
  void logout(String clientId, IObserver client) throws ServiceException;

}
