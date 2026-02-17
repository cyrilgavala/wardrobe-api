package sk.cyrilgavala.wardrobeapi.item.presentation.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import sk.cyrilgavala.wardrobeapi.item.domain.model.Item;
import sk.cyrilgavala.wardrobeapi.item.infrastructure.persistence.MongoItemRepository;
import sk.cyrilgavala.wardrobeapi.item.presentation.dto.CreateItemRequest;
import sk.cyrilgavala.wardrobeapi.item.presentation.dto.UpdateItemRequest;
import sk.cyrilgavala.wardrobeapi.shared.config.TestcontainersConfiguration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
class ItemControllerIntegrationTest {

  @Autowired
  private WebApplicationContext context;

  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MongoItemRepository itemRepository;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders
        .webAppContextSetup(context)
        .apply(springSecurity())
        .build();
    itemRepository.deleteAll();
  }

  @AfterEach
  void tearDown() {
    itemRepository.deleteAll();
  }

  @Test
  @WithMockUser(username = "testuser", authorities = {"USER"})
  void createsNewItemSuccessfully() throws Exception {
    CreateItemRequest request = new CreateItemRequest(
        "Blue Jeans",
        "Comfortable denim jeans",
        "Blue",
        "Levi's",
        "M",
        40,
        true,
        false,
        false,
        "https://example.com/image.jpg",
        5
    );

    mockMvc.perform(post("/api/items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Blue Jeans"))
        .andExpect(jsonPath("$.userId").value("testuser"));

    assertThat(itemRepository.count()).isEqualTo(1);
  }

  @Test
  @WithMockUser(username = "testuser", authorities = {"USER"})
  void retrievesItemByIdSuccessfully() throws Exception {
    Item savedItem = itemRepository.save(Item.create(
        "testuser",
        "Blue Jeans",
        "Comfortable denim",
        "Blue",
        "Levi's",
        "M",
        40,
        true,
        false,
        false,
        null,
        5
    ));

    mockMvc.perform(get("/api/items/{id}", savedItem.id()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Blue Jeans"))
        .andExpect(jsonPath("$.userId").value("testuser"));
  }

  @Test
  @WithMockUser(username = "testuser", authorities = {"USER"})
  void retrievesAllUserItems() throws Exception {
    itemRepository.save(
        Item.create("testuser", "Jeans", "Blue jeans", "Blue", null, null, null, null, null, null,
            null, null));
    itemRepository.save(
        Item.create("testuser", "Shirt", "White shirt", "White", null, null, null, null, null, null,
            null, null));

    mockMvc.perform(get("/api/items"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2));
  }

  @Test
  @WithMockUser(username = "testuser", authorities = {"USER"})
  void deletesItemSuccessfully() throws Exception {
    Item savedItem = itemRepository.save(Item.create(
        "testuser",
        "Blue Jeans",
        "Description",
        "Blue",
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null
    ));

    mockMvc.perform(delete("/api/items/{id}", savedItem.id()))
        .andExpect(status().isNoContent());

    assertThat(itemRepository.findById(savedItem.id())).isEmpty();
  }

  @Test
  @WithMockUser(username = "testuser", authorities = {"USER"})
  void returnsBadRequestWhenCreatingWithInvalidPayload() throws Exception {
    CreateItemRequest request = new CreateItemRequest(
        "",
        "",
        "",
        "",
        "",
        -1,
        null,
        null,
        null,
        "",
        0
    );

    mockMvc.perform(post("/api/items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "testuser", authorities = {"USER"})
  void returnsBadRequestWhenUpdatingWithInvalidPayload() throws Exception {
    Item savedItem = itemRepository.save(Item.create(
        "testuser",
        "Blue Jeans",
        "Description",
        "Blue",
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null
    ));

    UpdateItemRequest request = new UpdateItemRequest(
        "",
        "",
        "",
        "",
        "",
        -1,
        null,
        null,
        null,
        "",
        0
    );

    mockMvc.perform(put("/api/items/{id}", savedItem.id())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "testuser", authorities = {"USER"})
  void returnsNotFoundWhenItemDoesNotExist() throws Exception {
    mockMvc.perform(get("/api/items/{id}", "missing-id"))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(username = "testuser", authorities = {"USER"})
  void returnsNotFoundWhenDeletingMissingItem() throws Exception {
    mockMvc.perform(delete("/api/items/{id}", "missing-id"))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(username = "testuser", authorities = {"USER"})
  void returnsNotFoundWhenUpdatingMissingItem() throws Exception {
    UpdateItemRequest request = new UpdateItemRequest(
        "Blue Jeans",
        "Description",
        "Blue",
        "Levi's",
        "M",
        40,
        true,
        false,
        false,
        "https://example.com/image.jpg",
        5
    );

    mockMvc.perform(put("/api/items/{id}", "missing-id")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(username = "otheruser", authorities = {"USER"})
  void returnsForbiddenWhenAccessingItemOwnedByDifferentUser() throws Exception {
    Item savedItem = itemRepository.save(Item.create(
        "testuser",
        "Blue Jeans",
        "Description",
        "Blue",
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null
    ));

    mockMvc.perform(get("/api/items/{id}", savedItem.id()))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(username = "otheruser", authorities = {"USER"})
  void returnsForbiddenWhenDeletingItemOwnedByDifferentUser() throws Exception {
    Item savedItem = itemRepository.save(Item.create(
        "testuser",
        "Blue Jeans",
        "Description",
        "Blue",
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null
    ));

    mockMvc.perform(delete("/api/items/{id}", savedItem.id()))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(username = "otheruser", authorities = {"USER"})
  void returnsForbiddenWhenUpdatingItemOwnedByDifferentUser() throws Exception {
    Item savedItem = itemRepository.save(Item.create(
        "testuser",
        "Blue Jeans",
        "Description",
        "Blue",
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null
    ));

    UpdateItemRequest request = new UpdateItemRequest(
        "Black Jeans",
        "Updated Description",
        "Black",
        "Levi's",
        "M",
        30,
        true,
        true,
        false,
        "https://example.com/updated.jpg",
        6
    );

    mockMvc.perform(put("/api/items/{id}", savedItem.id())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isForbidden());
  }
}
