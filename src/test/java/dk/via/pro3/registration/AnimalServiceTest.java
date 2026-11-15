package dk.via.pro3.registration;


import dk.via.pro3.registration.model.Animal;
import dk.via.pro3.registration.repository.AnimalRepository;
import dk.via.pro3.registration.service.AnimalNotFoundException;
import dk.via.pro3.registration.service.AnimalService;
import dk.via.pro3.registration.service.DuplicateAnimalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;

public class AnimalServiceTest
{
  private AnimalService service;

  @BeforeEach
  void setUp() {
    service = new AnimalService(new InMemoryAnimalRepository());
    service.register(new Animal("A-1001", LocalDate.of(2026, 10, 1), 112.5, "Hansen Farm"));
    service.register(new Animal("A-1002", LocalDate.of(2026, 10, 1), 108.0, "Nielsen Farm"));
    service.register(new Animal("A-1004", LocalDate.of(2026, 10, 2), 104.75, "Hansen Farm"));
  }

  @Test
  void registerStoresAllFields() {
    service.register(new Animal("A-2000", LocalDate.of(2026, 11, 1), 99.9, "Jensen Farm"));

    Animal found = service.getAnimal("A-2000");
    assertEquals(LocalDate.of(2026, 11, 1), found.date());
    assertEquals(99.9, found.weight());
    assertEquals("Jensen Farm", found.origin());
  }

  @Test
  void registerWithoutDateUsesToday() {
    Animal registered = service.register(new Animal("A-2001", null, 100.0, "Jensen Farm"));
    assertEquals(LocalDate.now(), registered.date());
  }

  @Test
  void registerTrimsTextFields() {
    Animal registered = service.register(new Animal("  A-2002 ", null, 100.0, " Jensen Farm "));
    assertEquals("A-2002", registered.registrationNumber());
    assertEquals("Jensen Farm", registered.origin());
  }

  @Test
  void registerSameNumberTwiceThrowsDuplicate() {
    assertThrows(DuplicateAnimalException.class,
        () -> service.register(new Animal("A-1001", null, 100.0, "Hansen Farm")));
  }

  @Test
  void registerWithInvalidDataThrows() {
    assertThrows(IllegalArgumentException.class,
        () -> service.register(new Animal("", null, 100.0, "Hansen Farm")));
    assertThrows(IllegalArgumentException.class,
        () -> service.register(new Animal("A-3000", null, 0, "Hansen Farm")));
    assertThrows(IllegalArgumentException.class,
        () -> service.register(new Animal("A-3001", null, -5, "Hansen Farm")));
    assertThrows(IllegalArgumentException.class,
        () -> service.register(new Animal("A-3002", null, 100.0, " ")));
    assertThrows(IllegalArgumentException.class,
        () -> service.register(new Animal("A-3003", LocalDate.now().plusDays(1), 100.0, "Hansen Farm")));
  }

  @Test
  void getUnknownAnimalThrowsNotFound() {
    assertThrows(AnimalNotFoundException.class, () -> service.getAnimal("A-9999"));
  }

  @Test
  void getAnimalsByDate() {
    List<Animal> animals = service.getAnimalsByDate(LocalDate.of(2026, 10, 1));
    assertEquals(List.of("A-1001", "A-1002"), numbers(animals));
  }

  @Test
  void getAnimalsByDateWithNoAnimalsReturnsEmptyList() {
    assertTrue(service.getAnimalsByDate(LocalDate.of(2020, 1, 1)).isEmpty());
  }

  @Test
  void getAnimalsByOrigin() {
    List<Animal> animals = service.getAnimalsByOrigin("Hansen Farm");
    assertEquals(List.of("A-1001", "A-1004"), numbers(animals));
  }

  @Test
  void getAnimalsByUnknownOriginReturnsEmptyList() {
    assertTrue(service.getAnimalsByOrigin("Unknown Farm").isEmpty());
  }

  private static List<String> numbers(List<Animal> animals) {
    return animals.stream().map(Animal::registrationNumber).toList();
  }


  private static class InMemoryAnimalRepository implements AnimalRepository {

    private final List<Animal> animals = new ArrayList<>();

    @Override
    public void save(Animal animal) {
      animals.add(animal);
    }

    @Override
    public boolean existsByRegistrationNumber(String registrationNumber) {
      return findByRegistrationNumber(registrationNumber).isPresent();
    }

    @Override
    public Optional<Animal> findByRegistrationNumber(String registrationNumber) {
      return animals.stream().filter(a -> a.registrationNumber().equals(registrationNumber)).findFirst();
    }

    @Override
    public List<Animal> findByDate(LocalDate date) {
      return sorted(animals.stream().filter(a -> a.date().equals(date)).toList());
    }

    @Override
    public List<Animal> findByOrigin(String origin) {
      return sorted(animals.stream().filter(a -> a.origin().equals(origin)).toList());
    }

    @Override
    public List<Animal> findAll() {
      return sorted(animals);
    }

    private static List<Animal> sorted(List<Animal> list) {
      return list.stream().sorted(Comparator.comparing(Animal::registrationNumber)).toList();
    }
  }
}
