package dk.via.pro3.registration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
public class AnimalControllerTest
{
  @Autowired
  private WebApplicationContext context;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
  }

  //POST /animals

  @Test
  void registerAnimalReturns201AndCanBeReadAfterwards() throws Exception {
    String json = """
                { "registrationNumber": "A-2000", "date": "2026-11-14", "weight": 101.5, "origin": "Jensen Farm" }
                """;

    mockMvc.perform(post("/animals").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", "/animals/A-2000"))
        .andExpect(jsonPath("$.registrationNumber").value("A-2000"));

    mockMvc.perform(get("/animals/A-2000"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.date").value("2026-11-14"))
        .andExpect(jsonPath("$.weight").value(101.5))
        .andExpect(jsonPath("$.origin").value("Jensen Farm"));
  }

  @Test
  void registerExistingNumberReturns409() throws Exception {
    String json = """
                { "registrationNumber": "A-1001", "date": "2026-10-01", "weight": 100, "origin": "Hansen Farm" }
                """;
    mockMvc.perform(post("/animals").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isConflict());
  }

  @Test
  void registerWithInvalidWeightReturns400() throws Exception {
    String json = """
                { "registrationNumber": "A-2001", "date": "2026-11-14", "weight": -3, "origin": "Hansen Farm" }
                """;
    mockMvc.perform(post("/animals").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isBadRequest());
  }

  @Test
  void registerWithoutOriginReturns400() throws Exception {
    String json = """
                { "registrationNumber": "A-2002", "date": "2026-11-14", "weight": 100 }
                """;
    mockMvc.perform(post("/animals").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isBadRequest());
  }

  //GET /animals/{registrationNumber}

  @Test
  void getAnimalReturnsDetails() throws Exception {
    mockMvc.perform(get("/animals/A-1001"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.registrationNumber").value("A-1001"))
        .andExpect(jsonPath("$.date").value("2026-10-01"))
        .andExpect(jsonPath("$.weight").value(112.5))
        .andExpect(jsonPath("$.origin").value("Hansen Farm"));
  }

  @Test
  void getUnknownAnimalReturns404() throws Exception {
    mockMvc.perform(get("/animals/A-9999"))
        .andExpect(status().isNotFound());
  }

  //GET /animals?date

  @Test
  void getAnimalsByDate() throws Exception {
    mockMvc.perform(get("/animals").param("date", "2026-10-01"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(3)))
        .andExpect(jsonPath("$[*].registrationNumber", contains("A-1001", "A-1002", "A-1003")));
  }

  @Test
  void getAnimalsByDateWithNoArrivalsReturnsEmptyList() throws Exception {
    mockMvc.perform(get("/animals").param("date", "2020-01-01"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  void getAnimalsByInvalidDateReturns400() throws Exception {
    mockMvc.perform(get("/animals").param("date", "01-10-2026"))
        .andExpect(status().isBadRequest());
  }

  //GET /animals?origin

  @Test
  void getAnimalsByOrigin() throws Exception {
    mockMvc.perform(get("/animals").param("origin", "Hansen Farm"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(3)))
        .andExpect(jsonPath("$[*].registrationNumber", contains("A-1001", "A-1003", "A-1005")));
  }

  @Test
  void getAnimalsByUnknownOriginReturnsEmptyList() throws Exception {
    mockMvc.perform(get("/animals").param("origin", "Unknown Farm"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  void getAnimalsWithBothFiltersReturns400() throws Exception {
    mockMvc.perform(get("/animals").param("date", "2026-10-01").param("origin", "Hansen Farm"))
        .andExpect(status().isBadRequest());
  }

  //GET /animals

  @Test
  void getAllAnimals() throws Exception {
    mockMvc.perform(get("/animals"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(5)));
  }
}
