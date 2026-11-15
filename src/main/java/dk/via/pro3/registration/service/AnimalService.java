package dk.via.pro3.registration.service;

import dk.via.pro3.registration.model.Animal;
import dk.via.pro3.registration.repository.AnimalRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AnimalService
{
  private final AnimalRepository animalRepository;

  public AnimalService(AnimalRepository animalRepository) {
    this.animalRepository = animalRepository;
  }

  public Animal register(Animal animal) {
    if (animal == null) {
      throw new IllegalArgumentException("Request body is missing");
    }
    if (isBlank(animal.registrationNumber())) {
      throw new IllegalArgumentException("registrationNumber must not be empty");
    }
    if (animal.weight() <= 0) {
      throw new IllegalArgumentException("weight must be greater than 0");
    }
    if (isBlank(animal.origin())) {
      throw new IllegalArgumentException("origin must not be empty");
    }

    // If no date is given, the animal is registered with today's date
    LocalDate date = animal.date() != null ? animal.date() : LocalDate.now();
    if (date.isAfter(LocalDate.now())) {
      throw new IllegalArgumentException("date cannot be in the future");
    }

    Animal toSave = new Animal(animal.registrationNumber().trim(), date,
        animal.weight(), animal.origin().trim());

    if (animalRepository.existsByRegistrationNumber(toSave.registrationNumber())) {
      throw new DuplicateAnimalException(toSave.registrationNumber());
    }
    animalRepository.save(toSave);
    return toSave;
  }

  public Animal getAnimal(String registrationNumber) {
    return animalRepository.findByRegistrationNumber(registrationNumber)
        .orElseThrow(() -> new AnimalNotFoundException(registrationNumber));
  }

  public List<Animal> getAnimalsByDate(LocalDate date) {
    return animalRepository.findByDate(date);
  }

  public List<Animal> getAnimalsByOrigin(String origin) {
    if (isBlank(origin)) {
      throw new IllegalArgumentException("origin must not be empty");
    }
    return animalRepository.findByOrigin(origin.trim());
  }

  public List<Animal> getAllAnimals() {
    return animalRepository.findAll();
  }

  private static boolean isBlank(String s) {
    return s == null || s.isBlank();
  }
}
