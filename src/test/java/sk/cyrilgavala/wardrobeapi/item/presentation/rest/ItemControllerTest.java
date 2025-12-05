package sk.cyrilgavala.wardrobeapi.item.presentation.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import sk.cyrilgavala.wardrobeapi.item.application.service.ItemService;
import sk.cyrilgavala.wardrobeapi.item.domain.exception.ItemAccessDeniedException;
import sk.cyrilgavala.wardrobeapi.item.domain.exception.ItemNotFoundException;
import sk.cyrilgavala.wardrobeapi.item.domain.model.Item;
import sk.cyrilgavala.wardrobeapi.item.domain.model.ItemCategory;
import sk.cyrilgavala.wardrobeapi.item.presentation.dto.CreateItemRequest;
import sk.cyrilgavala.wardrobeapi.item.presentation.dto.UpdateItemRequest;
import sk.cyrilgavala.wardrobeapi.item.presentation.mapper.ItemMapper;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

  private MockMvc mockMvc;

  @Mock
  private ItemService itemService;

  @InjectMocks
  private ItemMapper itemMapper = new ItemMapper();

  @InjectMocks
  private ItemController itemController;

  private ObjectMapper objectMapper;
  private final String userId = "user123";
  private final String itemId = "item123";

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    objectMapper.findAndRegisterModules();

    itemController = new ItemController(itemService, itemMapper);

    mockMvc = MockMvcBuilders.standaloneSetup(itemController)
        .setControllerAdvice(new ItemExceptionHandler())
        .build();

    // Setup security context
    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(userId, null, List.of());
    SecurityContext securityContext = new SecurityContextImpl();
    securityContext.setAuthentication(authentication);
    SecurityContextHolder.setContext(securityContext);
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  private Item createTestItem() {
    return Item.builder()
        .id(itemId)
        .userId(userId)
        .name("Blue Jeans")
        .description("Comfortable denim jeans")
        .category(ItemCategory.BOTTOMS)
        .color("Blue")
        .brand("Levi's")
        .size("32")
        .washingTemperature(40)
        .canBeIroned(true)
        .canBeTumbleDried(false)
        .canBeDryCleaned(false)
        .canBeBleached(false)
        .imageUrl("https://example.com/jeans.jpg")
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .build();
  }

  // ===== Create Item Tests =====

  @Test
  void createItem_shouldReturnCreated_withValidRequest() throws Exception {
    // Given
    CreateItemRequest request = new CreateItemRequest(
        "Blue Jeans",
        "Comfortable denim jeans",
        ItemCategory.BOTTOMS,
        "Blue",
        "Levi's",
        "32",
        40,
        true,
        false,
        false,
        false,
        "https://example.com/jeans.jpg"
    );

    Item createdItem = createTestItem();
    when(itemService.createItem(any())).thenReturn(createdItem);

    // When & Then
    mockMvc.perform(post("/api/items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(itemId))
        .andExpect(jsonPath("$.userId").value(userId))
        .andExpect(jsonPath("$.name").value("Blue Jeans"))
        .andExpect(jsonPath("$.description").value("Comfortable denim jeans"))
        .andExpect(jsonPath("$.category").value("BOTTOMS"))
        .andExpect(jsonPath("$.color").value("Blue"))
        .andExpect(jsonPath("$.brand").value("Levi's"))
        .andExpect(jsonPath("$.size").value("32"))
        .andExpect(jsonPath("$.washingTemperature").value(40))
        .andExpect(jsonPath("$.canBeIroned").value(true))
        .andExpect(jsonPath("$.canBeTumbleDried").value(false))
        .andExpect(jsonPath("$.canBeDryCleaned").value(false))
        .andExpect(jsonPath("$.canBeBleached").value(false))
        .andExpect(jsonPath("$.imageUrl").value("https://example.com/jeans.jpg"));

    verify(itemService).createItem(any());
  }

  @Test
  void createItem_shouldReturnBadRequest_whenNameIsMissing() throws Exception {
    // Given
    CreateItemRequest request = new CreateItemRequest(
        "",  // empty name
        "Description",
        ItemCategory.BOTTOMS,
        "Blue",
        "Levi's",
        "32",
        40,
        true,
        false,
        false,
        false,
        null
    );

    // When & Then
    mockMvc.perform(post("/api/items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createItem_shouldReturnBadRequest_whenCategoryIsMissing() throws Exception {
    // Given
    String requestJson = """
        {
          "name": "Blue Jeans",
          "description": "Comfortable denim jeans",
          "color": "Blue"
        }
        """;

    // When & Then
    mockMvc.perform(post("/api/items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createItem_shouldReturnBadRequest_whenWashingTemperatureTooHigh() throws Exception {
    // Given
    CreateItemRequest request = new CreateItemRequest(
        "Blue Jeans",
        "Description",
        ItemCategory.BOTTOMS,
        "Blue",
        "Levi's",
        "32",
        150,  // exceeds max of 95
        true,
        false,
        false,
        false,
        null
    );

    // When & Then
    mockMvc.perform(post("/api/items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createItem_shouldReturnBadRequest_whenWashingTemperatureIsNegative() throws Exception {
    // Given
    CreateItemRequest request = new CreateItemRequest(
        "Blue Jeans",
        "Description",
        ItemCategory.BOTTOMS,
        "Blue",
        "Levi's",
        "32",
        -10,  // negative temperature
        true,
        false,
        false,
        false,
        null
    );

    // When & Then
    mockMvc.perform(post("/api/items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createItem_shouldReturnBadRequest_whenCategoryIsInvalid() throws Exception {
    // Given
    String requestJson = """
        {
          "name": "Blue Jeans",
          "description": "Comfortable denim jeans",
          "category": "INVALID_CATEGORY",
          "color": "Blue",
          "brand": "Levi's",
          "size": "32",
          "washingTemperature": 40,
          "canBeIroned": true,
          "canBeTumbleDried": false,
          "canBeDryCleaned": false,
          "canBeBleached": false
        }
        """;

    // When & Then
    mockMvc.perform(post("/api/items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isBadRequest());
  }

  // ===== Update Item Tests =====

  @Test
  void updateItem_shouldReturnOk_withValidRequest() throws Exception {
    // Given
    UpdateItemRequest request = new UpdateItemRequest(
        "Updated Jeans",
        "Updated description",
        ItemCategory.BOTTOMS,
        "Dark Blue",
        "Levi's",
        "34",
        40,
        true,
        false,
        false,
        false,
        "https://example.com/updated.jpg"
    );

    Item updatedItem = Item.builder()
        .id(itemId)
        .userId(userId)
        .name("Updated Jeans")
        .description("Updated description")
        .category(ItemCategory.BOTTOMS)
        .color("Dark Blue")
        .brand("Levi's")
        .size("34")
        .washingTemperature(40)
        .canBeIroned(true)
        .canBeTumbleDried(false)
        .canBeDryCleaned(false)
        .canBeBleached(false)
        .imageUrl("https://example.com/updated.jpg")
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .build();

    when(itemService.updateItem(any())).thenReturn(updatedItem);

    // When & Then
    mockMvc.perform(put("/api/items/{id}", itemId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(itemId))
        .andExpect(jsonPath("$.name").value("Updated Jeans"))
        .andExpect(jsonPath("$.description").value("Updated description"))
        .andExpect(jsonPath("$.color").value("Dark Blue"))
        .andExpect(jsonPath("$.size").value("34"));

    verify(itemService).updateItem(any());
  }

  @Test
  void updateItem_shouldReturnNotFound_whenItemDoesNotExist() throws Exception {
    // Given
    UpdateItemRequest request = new UpdateItemRequest(
        "Updated Jeans",
        "Updated description",
        ItemCategory.BOTTOMS,
        "Dark Blue",
        "Levi's",
        "34",
        40,
        true,
        false,
        false,
        false,
        null
    );

    when(itemService.updateItem(any())).thenThrow(ItemNotFoundException.withId(itemId));

    // When & Then
    mockMvc.perform(put("/api/items/{id}", itemId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Item not found with id: " + itemId));
  }

  @Test
  void updateItem_shouldReturnForbidden_whenUserDoesNotOwnItem() throws Exception {
    // Given
    UpdateItemRequest request = new UpdateItemRequest(
        "Updated Jeans",
        "Updated description",
        ItemCategory.BOTTOMS,
        "Dark Blue",
        "Levi's",
        "34",
        40,
        true,
        false,
        false,
        false,
        null
    );

    when(itemService.updateItem(any())).thenThrow(ItemAccessDeniedException.withId(itemId));

    // When & Then
    mockMvc.perform(put("/api/items/{id}", itemId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.message").value("Access denied to item with id: " + itemId));
  }

  @Test
  void updateItem_shouldReturnBadRequest_whenNameIsMissing() throws Exception {
    // Given
    UpdateItemRequest request = new UpdateItemRequest(
        "",  // empty name
        "Description",
        ItemCategory.BOTTOMS,
        "Blue",
        "Levi's",
        "32",
        40,
        true,
        false,
        false,
        false,
        null
    );

    // When & Then
    mockMvc.perform(put("/api/items/{id}", itemId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  // ===== Delete Item Tests =====

  @Test
  void deleteItem_shouldReturnNoContent_whenSuccessful() throws Exception {
    // When & Then
    mockMvc.perform(delete("/api/items/{id}", itemId))
        .andExpect(status().isNoContent());

    verify(itemService).deleteItem(eq(itemId), eq(userId));
  }

  @Test
  void deleteItem_shouldReturnNotFound_whenItemDoesNotExist() throws Exception {
    // Given
    doThrow(ItemNotFoundException.withId(itemId))
        .when(itemService).deleteItem(eq(itemId), eq(userId));

    // When & Then
    mockMvc.perform(delete("/api/items/{id}", itemId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Item not found with id: " + itemId));
  }

  @Test
  void deleteItem_shouldReturnForbidden_whenUserDoesNotOwnItem() throws Exception {
    // Given
    doThrow(ItemAccessDeniedException.withId(itemId))
        .when(itemService).deleteItem(eq(itemId), eq(userId));

    // When & Then
    mockMvc.perform(delete("/api/items/{id}", itemId))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.message").value("Access denied to item with id: " + itemId));
  }

  // ===== Get Item Tests =====

  @Test
  void getItem_shouldReturnOk_whenItemExists() throws Exception {
    // Given
    Item item = createTestItem();
    when(itemService.getItem(eq(itemId), eq(userId))).thenReturn(item);

    // When & Then
    mockMvc.perform(get("/api/items/{id}", itemId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(itemId))
        .andExpect(jsonPath("$.userId").value(userId))
        .andExpect(jsonPath("$.name").value("Blue Jeans"));

    verify(itemService).getItem(eq(itemId), eq(userId));
  }

  @Test
  void getItem_shouldReturnNotFound_whenItemDoesNotExist() throws Exception {
    // Given
    when(itemService.getItem(eq(itemId), eq(userId)))
        .thenThrow(ItemNotFoundException.withId(itemId));

    // When & Then
    mockMvc.perform(get("/api/items/{id}", itemId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Item not found with id: " + itemId));
  }

  @Test
  void getItem_shouldReturnForbidden_whenUserDoesNotOwnItem() throws Exception {
    // Given
    when(itemService.getItem(eq(itemId), eq(userId)))
        .thenThrow(ItemAccessDeniedException.withId(itemId));

    // When & Then
    mockMvc.perform(get("/api/items/{id}", itemId))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.message").value("Access denied to item with id: " + itemId));
  }

  // ===== Get All Items Tests =====

  @Test
  void getAllItems_shouldReturnOk_withListOfItems() throws Exception {
    // Given
    Item item1 = createTestItem();
    Item item2 = Item.builder()
        .id("item456")
        .userId(userId)
        .name("White T-Shirt")
        .category(ItemCategory.TOPS)
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .build();

    when(itemService.getAllItemsForUser(userId)).thenReturn(List.of(item1, item2));

    // When & Then
    mockMvc.perform(get("/api/items"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].id").value(itemId))
        .andExpect(jsonPath("$[0].name").value("Blue Jeans"))
        .andExpect(jsonPath("$[1].id").value("item456"))
        .andExpect(jsonPath("$[1].name").value("White T-Shirt"));

    verify(itemService).getAllItemsForUser(userId);
  }

  @Test
  void getAllItems_shouldReturnEmptyArray_whenNoItems() throws Exception {
    // Given
    when(itemService.getAllItemsForUser(userId)).thenReturn(List.of());

    // When & Then
    mockMvc.perform(get("/api/items"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(0));

    verify(itemService).getAllItemsForUser(userId);
  }

  @Test
  void getAllItems_shouldFilterByCategory_whenCategoryProvided() throws Exception {
    // Given
    ItemCategory category = ItemCategory.TOPS;
    Item item1 = Item.builder()
        .id("item1")
        .userId(userId)
        .name("T-Shirt")
        .category(category)
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .build();

    when(itemService.getItemsByCategory(userId, category)).thenReturn(List.of(item1));

    // When & Then
    mockMvc.perform(get("/api/items")
            .param("category", "TOPS"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].name").value("T-Shirt"))
        .andExpect(jsonPath("$[0].category").value("TOPS"));

    verify(itemService).getItemsByCategory(userId, category);
  }

  @Test
  void getAllItems_shouldReturnEmptyArray_whenNoCategoryItems() throws Exception {
    // Given
    ItemCategory category = ItemCategory.FORMAL;
    when(itemService.getItemsByCategory(userId, category)).thenReturn(List.of());

    // When & Then
    mockMvc.perform(get("/api/items")
            .param("category", "FORMAL"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(0));

    verify(itemService).getItemsByCategory(userId, category);
  }
}

