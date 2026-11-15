package dk.via.pro3.registration.controller;

import dk.via.pro3.registration.service.AnimalNotFoundException;
import dk.via.pro3.registration.service.DuplicateAnimalException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class RestExceptionHandler
{
  @ExceptionHandler(AnimalNotFoundException.class)
  public ProblemDetail handleNotFound(AnimalNotFoundException e) {
    return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());     // 404
  }

  @ExceptionHandler(DuplicateAnimalException.class)
  public ProblemDetail handleDuplicate(DuplicateAnimalException e) {
    return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());      // 409
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ProblemDetail handleBadRequest(IllegalArgumentException e) {
    return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());   // 400
  }
}
