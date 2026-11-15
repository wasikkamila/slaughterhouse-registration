package dk.via.pro3.registration.controller;

import dk.via.pro3.registration.model.Animal;
import dk.via.pro3.registration.service.AnimalService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/animals")
public class AnimalController
{
  private final AnimalService animalService;

  public AnimalController(AnimalService animalService) {
    this.animalService = animalService;
  }

  //register an animal
  @PostMapping
  public ResponseEntity<Animal> registerAnimal(@RequestBody Animal animal) {
    Animal registered = animalService.register(animal);
    URI location = URI.create("/animals/" + registered.registrationNumber());
    return ResponseEntity.created(location).body(registered);
  }

  // GET /animals/A-1001
  @GetMapping("/{registrationNumber}")
  public Animal getAnimal(@PathVariable String registrationNumber) {
    return animalService.getAnimal(registrationNumber);
  }

  // GET /animals?date=2026-10-01 or /animals?origin=Hansen Farm or /animals
  @GetMapping
  public List<Animal> getAnimals(
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
      @RequestParam(required = false) String origin) {

    if (date != null && origin != null) {
      throw new IllegalArgumentException("Use either 'date' or 'origin', not both");
    }
    if (date != null) {
      return animalService.getAnimalsByDate(date);
    }
    if (origin != null) {
      return animalService.getAnimalsByOrigin(origin);
    }
    return animalService.getAllAnimals();
  }
}
