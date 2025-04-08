package services;

import model.Registration;

public interface IObserver {
  void createdRegistration(Registration reg) throws ServiceException;

}
