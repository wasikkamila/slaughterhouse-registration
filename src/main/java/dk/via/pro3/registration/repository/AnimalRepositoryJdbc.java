package dk.via.pro3.registration.repository;

import dk.via.pro3.registration.model.Animal;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class AnimalRepositoryJdbc implements AnimalRepository
{
  private static final String SELECT_ANIMAL =
      "SELECT registration_number, arrival_date, weight, origin FROM animal ";

  private static final RowMapper<Animal> ANIMAL_ROW_MAPPER = (rs, rowNum) -> new Animal(
      rs.getString("registration_number"),
      rs.getDate("arrival_date").toLocalDate(),
      rs.getDouble("weight"),
      rs.getString("origin"));

  private final JdbcClient jdbcClient;

  public AnimalRepositoryJdbc(JdbcClient jdbcClient)
  {
    this.jdbcClient = jdbcClient;
  }

  @Override
  public void save(Animal animal)
  {
    jdbcClient.sql("INSERT INTO animal (registration_number, arrival_date, weight, origin) "
                    + "VALUES (:registrationNumber, :date, :weight, :origin)")
        .param("registrationNumber", animal.registrationNumber())
        .param("date", animal.date())
        .param("weight", animal.weight())
        .param("origin", animal.origin())
        .update();
  }

  @Override
  public boolean existsByRegistrationNumber(String registrationNumber)
  {
    return findByRegistrationNumber(registrationNumber).isPresent();
  }

  @Override
  public Optional<Animal> findByRegistrationNumber(String registrationNumber)
  {
    return jdbcClient.sql(SELECT_ANIMAL + "WHERE registration_number = :registrationNumber")
        .param("registrationNumber", registrationNumber)
        .query(ANIMAL_ROW_MAPPER)
        .optional();
  }
  @Override
  public List<Animal> findByDate(LocalDate date)
  {
    return jdbcClient.sql(SELECT_ANIMAL + "WHERE arrival_date = :date ORDER BY registration_number")
        .param("date", date)
        .query(ANIMAL_ROW_MAPPER)
        .list();
  }

  @Override
  public List<Animal> findByOrigin(String origin)
  {
    return jdbcClient.sql(SELECT_ANIMAL + "WHERE origin = :origin ORDER BY registration_number")
        .param("origin", origin)
        .query(ANIMAL_ROW_MAPPER)
        .list();
  }

  @Override
  public List<Animal> findAll()
  {
    return jdbcClient.sql(SELECT_ANIMAL + "ORDER BY registration_number")
        .query(ANIMAL_ROW_MAPPER)
        .list();
  }
}
