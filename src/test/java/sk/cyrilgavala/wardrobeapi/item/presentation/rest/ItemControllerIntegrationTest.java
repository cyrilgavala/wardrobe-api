package sk.cyrilgavala.wardrobeapi.item.presentation.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import sk.cyrilgavala.wardrobeapi.item.domain.model.Item;
import sk.cyrilgavala.wardrobeapi.item.infrastructure.persistence.MongoItemRepository;
import sk.cyrilgavala.wardrobeapi.shared.config.TestcontainersConfiguration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
class ItemControllerIntegrationTest {

  @Autowired
  private WebApplicationContext context;

  private MockMvc mockMvc;


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
    mockMvc.perform(multipart("/api/items")
            .param("name", "Blue Jeans")
            .param("description", "Comfortable denim jeans")
            .param("color", "Blue")
            .param("brand", "Levi's")
            .param("size", "M")
            .param("washingTemperature", "40")
            .param("canBeIroned", "true")
            .param("canBeDried", "false")
            .param("canBeBleached", "false")
            .param("boxNumber", "5"))
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
    mockMvc.perform(multipart("/api/items")
            .param("name", "")
            .param("description", "")
            .param("color", "")
            .param("brand", "")
            .param("size", "")
            .param("washingTemperature", "-1")
            .param("boxNumber", "0"))
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

    mockMvc.perform(multipart("/api/items/{id}", savedItem.id())
            .with(request -> {
              request.setMethod("PUT");
              return request;
            })
            .param("name", "")
            .param("description", "")
            .param("color", "")
            .param("brand", "")
            .param("size", "")
            .param("washingTemperature", "-1")
            .param("boxNumber", "0"))
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
    mockMvc.perform(multipart("/api/items/{id}", "missing-id")
            .with(request -> {
              request.setMethod("PUT");
              return request;
            })
            .param("name", "Blue Jeans")
            .param("description", "Description")
            .param("color", "Blue")
            .param("brand", "Levi's")
            .param("size", "M")
            .param("washingTemperature", "40")
            .param("canBeIroned", "true")
            .param("canBeDried", "false")
            .param("canBeBleached", "false")
            .param("boxNumber", "5"))
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

    mockMvc.perform(multipart("/api/items/{id}", savedItem.id())
            .with(request -> {
              request.setMethod("PUT");
              return request;
            })
            .param("name", "Black Jeans")
            .param("description", "Updated Description")
            .param("color", "Black")
            .param("brand", "Levi's")
            .param("size", "M")
            .param("washingTemperature", "30")
            .param("canBeIroned", "true")
            .param("canBeDried", "true")
            .param("canBeBleached", "false")
            .param("boxNumber", "6"))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(username = "testuser", authorities = {"USER"})
  void returnsNotFoundWhenGettingImageForItemWithoutImage() throws Exception {
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

    mockMvc.perform(get("/api/items/{id}/image", savedItem.id()))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(username = "testuser", authorities = {"USER"})
  void returnsNotFoundWhenGettingImageForNonExistentItem() throws Exception {
    mockMvc.perform(get("/api/items/{id}/image", "missing-id"))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(username = "otheruser", authorities = {"USER"})
  void returnsForbiddenWhenGettingImageForItemOwnedByDifferentUser() throws Exception {
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
        "image-123",
        null
    ));

    mockMvc.perform(get("/api/items/{id}/image", savedItem.id()))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(username = "testuser", authorities = {"USER"})
  void returnsBadRequestWhenUploadingInvalidImageType() throws Exception {
    MockMultipartFile invalidImage = new MockMultipartFile(
        "image",
        "test.txt",
        "text/plain",
        "This is not an image".getBytes()
    );

    mockMvc.perform(multipart("/api/items")
            .file(invalidImage)
            .param("name", "Blue Jeans")
            .param("description", "Comfortable denim jeans")
            .param("color", "Blue")
            .param("brand", "Levi's")
            .param("size", "M"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value(
            org.hamcrest.Matchers.containsString("Invalid image type")));
  }

  @Test
  @WithMockUser(username = "testuser", authorities = {"USER"})
  void returnsBadRequestWhenUploadingImageThatIsTooLarge() throws Exception {
    byte[] largeContent = new byte[21 * 1024 * 1024];
    MockMultipartFile largeImage = new MockMultipartFile(
        "image",
        "large.jpg",
        "image/jpeg",
        largeContent
    );

    mockMvc.perform(multipart("/api/items")
            .file(largeImage)
            .param("name", "Blue Jeans")
            .param("description", "Comfortable denim jeans")
            .param("color", "Blue"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value(
            org.hamcrest.Matchers.containsString("exceeds maximum allowed size")));
  }

  @Test
  @WithMockUser(username = "testuser", authorities = {"USER"})
  void createsItemSuccessfullyWithValidImage() throws Exception {
    MockMultipartFile validImage = new MockMultipartFile(
        "image",
        "test.jpg",
        "image/jpeg",
        "fake-image-content".getBytes()
    );

    mockMvc.perform(multipart("/api/items")
            .file(validImage)
            .param("name", "Blue Jeans")
            .param("description", "With image")
            .param("color", "Blue")
            .param("brand", "Levi's")
            .param("size", "M"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Blue Jeans"))
        .andExpect(jsonPath("$.imageId").exists());

    assertThat(itemRepository.count()).isEqualTo(1);
    Item savedItem = itemRepository.findAll().get(0);
    assertThat(savedItem.imageId()).isNotNull();
  }

  @Test
  @WithMockUser(username = "testuser", authorities = {"USER"})
  void updatesItemAndReplacesImageSuccessfully() throws Exception {
    MockMultipartFile initialImage = new MockMultipartFile(
        "image",
        "initial.jpg",
        "image/jpeg",
        "initial-image".getBytes()
    );

    String itemId = mockMvc.perform(multipart("/api/items")
            .file(initialImage)
            .param("name", "Original")
            .param("color", "Blue"))
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse()
        .getContentAsString();

    Item item = itemRepository.findAll().get(0);
    String originalImageId = item.imageId();
    assertThat(originalImageId).isNotNull();

    MockMultipartFile updatedImage = new MockMultipartFile(
        "image",
        "updated.png",
        "image/png",
        "updated-image".getBytes()
    );

    mockMvc.perform(multipart("/api/items/{id}", item.id())
            .file(updatedImage)
            .with(request -> {
              request.setMethod("PUT");
              return request;
            })
            .param("name", "Updated")
            .param("color", "Red"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Updated"));

    Item updatedItem = itemRepository.findById(item.id()).get();
    assertThat(updatedItem.imageId()).isNotNull();
    assertThat(updatedItem.imageId()).isNotEqualTo(originalImageId);
  }

  @Test
  @WithMockUser(username = "testuser", authorities = {"USER"})
  void returnsImageWithCorrectContentType() throws Exception {
    MockMultipartFile pngImage = new MockMultipartFile(
        "image",
        "test.png",
        "image/png",
        new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47}
    );

    String responseBody = mockMvc.perform(multipart("/api/items")
            .file(pngImage)
            .param("name", "Item with PNG")
            .param("color", "Blue"))
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse()
        .getContentAsString();

    Item item = itemRepository.findAll().get(0);

    mockMvc.perform(get("/api/items/{id}/image", item.id()))
        .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(username = "testuser", authorities = {"USER"})
  void acceptsWebpImageFormat() throws Exception {
    MockMultipartFile webpImage = new MockMultipartFile(
        "image",
        "test.webp",
        "image/webp",
        "RIFF-webp-content".getBytes()
    );

    mockMvc.perform(multipart("/api/items")
            .file(webpImage)
            .param("name", "Item with WebP")
            .param("color", "Green"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.imageId").exists());
  }

  @Test
  @WithMockUser(username = "testuser", authorities = {"USER"})
  void returnsBadRequestWhenUpdatingWithInvalidImageType() throws Exception {
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

    MockMultipartFile invalidImage = new MockMultipartFile(
        "image",
        "document.pdf",
        "application/pdf",
        "PDF content".getBytes()
    );

    mockMvc.perform(multipart("/api/items/{id}", savedItem.id())
            .file(invalidImage)
            .with(request -> {
              request.setMethod("PUT");
              return request;
            })
            .param("name", "Updated")
            .param("color", "Red"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value(
            org.hamcrest.Matchers.containsString("Invalid image type")));
  }
}
