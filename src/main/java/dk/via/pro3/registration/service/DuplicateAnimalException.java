package dk.via.pro3.registration.service;

public class DuplicateAnimalException extends RuntimeException
{
  public DuplicateAnimalException(String registrationNumber)

  {
    super("Animal "+registrationNumber+" is already registered");
  }
}
