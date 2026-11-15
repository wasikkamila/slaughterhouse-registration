package dk.via.pro3.registration.service;

public class AnimalNotFoundException extends RuntimeException
{
  public AnimalNotFoundException(String registrationNumber)
  {
    super("No animal with registration number "+registrationNumber);
  }
}
