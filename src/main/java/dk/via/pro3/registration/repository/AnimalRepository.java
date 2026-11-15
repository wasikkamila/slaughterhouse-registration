package dk.via.pro3.registration.repository;

import dk.via.pro3.registration.model.Animal;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AnimalRepository
{
  void save(Animal animal);
  boolean existsByRegistrationNumber(String registrationNumber);
  Optional<Animal> findByRegistrationNumber(String registrationNumber);
  List<Animal> findByDate(LocalDate date);
  List<Animal> findByOrigin(String origin);
  List<Animal> findAll();
}
