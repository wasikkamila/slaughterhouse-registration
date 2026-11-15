package dk.via.pro3.registration.model;

import java.time.LocalDate;

public record Animal(String registrationNumber, LocalDate date, double weight, String origin)
{
}
