package sk.cyrilgavala.wardrobeapi.item.presentation.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import sk.cyrilgavala.wardrobeapi.image.application.service.ImageStorageService;
import sk.cyrilgavala.wardrobeapi.item.application.command.CreateItemCommand;
import sk.cyrilgavala.wardrobeapi.item.application.command.DeleteItemCommand;
import sk.cyrilgavala.wardrobeapi.item.application.command.UpdateItemCommand;
import sk.cyrilgavala.wardrobeapi.item.application.command.handler.CreateItemCommandHandler;
import sk.cyrilgavala.wardrobeapi.item.application.command.handler.DeleteItemCommandHandler;
import sk.cyrilgavala.wardrobeapi.item.application.command.handler.UpdateItemCommandHandler;
import sk.cyrilgavala.wardrobeapi.item.application.query.GetAllItemsQuery;
import sk.cyrilgavala.wardrobeapi.item.application.query.GetItemQuery;
import sk.cyrilgavala.wardrobeapi.item.application.query.handler.GetAllItemsQueryHandler;
import sk.cyrilgavala.wardrobeapi.item.application.query.handler.GetItemQueryHandler;
import sk.cyrilgavala.wardrobeapi.item.domain.model.Item;
import sk.cyrilgavala.wardrobeapi.item.presentation.dto.CreateItemRequest;
import sk.cyrilgavala.wardrobeapi.item.presentation.dto.ItemResponse;
import sk.cyrilgavala.wardrobeapi.item.presentation.dto.UpdateItemRequest;
import sk.cyrilgavala.wardrobeapi.item.presentation.mapper.ItemDtoMapper;

@Slf4j
@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
@Tag(name = "Items", description = "Wardrobe item management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class ItemController {

  private final CreateItemCommandHandler createItemCommandHandler;
  private final UpdateItemCommandHandler updateItemCommandHandler;
  private final DeleteItemCommandHandler deleteItemCommandHandler;
  private final GetItemQueryHandler getItemQueryHandler;
  private final GetAllItemsQueryHandler getAllItemsQueryHandler;
  private final ItemDtoMapper itemMapper;
  private final ImageStorageService imageStorageService;

  @PostMapping(consumes = "multipart/form-data")
  @Operation(
      summary = "Create a new wardrobe item",
      description =
          "Creates a new wardrobe item for the authenticated user. Supports optional image upload. "
              +
              "Maximum image size: 20MB. Supported formats: JPEG, PNG, WebP."
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Item created successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid request data or image validation failed"),
      @ApiResponse(responseCode = "401", description = "Unauthorized - valid JWT token required")
  })
  public ResponseEntity<ItemResponse> createItem(
      @Valid @ModelAttribute CreateItemRequest request,
      @RequestPart(value = "image", required = false) MultipartFile image) {
    String userId = getCurrentUserId();
    log.info("Received create item request for user: {}", userId);

    // Store image if provided
    String imageId = null;
    if (image != null && !image.isEmpty()) {
      imageId = imageStorageService.storeImage(image);
    }

    CreateItemCommand command = itemMapper.toCreateCommand(request, userId, imageId);
    Item item = createItemCommandHandler.handle(command);
    ItemResponse response = itemMapper.toResponse(item);

    log.info("Item created successfully with id: {}", item.id());
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PutMapping(value = "/{id}", consumes = "multipart/form-data")
  @Operation(
      summary = "Update an existing wardrobe item",
      description = "Updates an existing wardrobe item owned by the authenticated user. " +
          "If a new image is provided, the old image will be automatically deleted and replaced. " +
          "Maximum image size: 20MB. Supported formats: JPEG, PNG, WebP."
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Item updated successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid request data or image validation failed"),
      @ApiResponse(responseCode = "401", description = "Unauthorized - valid JWT token required"),
      @ApiResponse(responseCode = "403", description = "Forbidden - item belongs to another user"),
      @ApiResponse(responseCode = "404", description = "Item not found")
  })
  public ResponseEntity<ItemResponse> updateItem(
      @Parameter(description = "Item ID", required = true)
      @PathVariable String id,
      @Valid @ModelAttribute UpdateItemRequest request,
      @RequestPart(value = "image", required = false) MultipartFile image) {
    String userId = getCurrentUserId();
    log.info("Received update item request for id: {} by user: {}", id, userId);

    // Get existing item to retrieve current imageId
    GetItemQuery getQuery = new GetItemQuery(id, userId);
    Item existingItem = getItemQueryHandler.handle(getQuery);

    String imageId = existingItem.imageId();

    // If new image provided, delete old one and store new one
    if (image != null && !image.isEmpty()) {
      if (imageId != null) {
        imageStorageService.deleteImage(imageId);
      }
      imageId = imageStorageService.storeImage(image);
    }

    UpdateItemCommand command = itemMapper.toUpdateCommand(request, id, userId, imageId);
    Item item = updateItemCommandHandler.handle(command);
    ItemResponse response = itemMapper.toResponse(item);

    log.info("Item updated successfully: {}", id);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  @Operation(
      summary = "Delete a wardrobe item",
      description = "Deletes a wardrobe item owned by the authenticated user. Associated image will also be deleted."
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Item deleted successfully"),
      @ApiResponse(responseCode = "401", description = "Unauthorized - valid JWT token required"),
      @ApiResponse(responseCode = "403", description = "Forbidden - item belongs to another user"),
      @ApiResponse(responseCode = "404", description = "Item not found")
  })
  public ResponseEntity<Void> deleteItem(
      @Parameter(description = "Item ID", required = true)
      @PathVariable String id) {
    String userId = getCurrentUserId();
    log.info("Received delete item request for id: {} by user: {}", id, userId);

    DeleteItemCommand command = new DeleteItemCommand(id, userId);
    deleteItemCommandHandler.handle(command);

    log.info("Item deleted successfully: {}", id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{id}")
  @Operation(
      summary = "Get a wardrobe item",
      description = "Retrieves a specific wardrobe item by ID owned by the authenticated user"
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Item retrieved successfully"),
      @ApiResponse(responseCode = "401", description = "Unauthorized - valid JWT token required"),
      @ApiResponse(responseCode = "403", description = "Forbidden - item belongs to another user"),
      @ApiResponse(responseCode = "404", description = "Item not found")
  })
  public ResponseEntity<ItemResponse> getItem(
      @Parameter(description = "Item ID", required = true)
      @PathVariable String id) {
    String userId = getCurrentUserId();
    log.info("Received get item request for id: {} by user: {}", id, userId);

    GetItemQuery query = new GetItemQuery(id, userId);
    Item item = getItemQueryHandler.handle(query);
    ItemResponse response = itemMapper.toResponse(item);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}/image")
  @Operation(
      summary = "Get item image",
      description = "Retrieves the image binary data for a specific wardrobe item. " +
          "Returns the image with appropriate Content-Type header (image/jpeg, image/png, or image/webp)"
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Image retrieved successfully"),
      @ApiResponse(responseCode = "401", description = "Unauthorized - valid JWT token required"),
      @ApiResponse(responseCode = "403", description = "Forbidden - item belongs to another user"),
      @ApiResponse(responseCode = "404", description = "Item not found or item has no image")
  })
  public ResponseEntity<byte[]> getItemImage(
      @Parameter(description = "Item ID", required = true)
      @PathVariable String id) {
    String userId = getCurrentUserId();
    log.info("Received get image request for item id: {} by user: {}", id, userId);

    // Verify item ownership
    GetItemQuery query = new GetItemQuery(id, userId);
    Item item = getItemQueryHandler.handle(query);

    if (item.imageId() == null) {
      return ResponseEntity.notFound().build();
    }

    byte[] imageData = imageStorageService.getImage(item.imageId());
    String contentType = imageStorageService.getContentType(item.imageId());

    return ResponseEntity.ok()
        .header("Content-Type", contentType)
        .body(imageData);
  }

  @GetMapping
  @Operation(
      summary = "Get all wardrobe items",
      description = "Retrieves all wardrobe items for the authenticated user"
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Items retrieved successfully"),
      @ApiResponse(responseCode = "401", description = "Unauthorized - valid JWT token required")
  })
  public ResponseEntity<List<ItemResponse>> getAllItems() {
    String userId = getCurrentUserId();
    log.info("Received get all items request for user: {}", userId);

    GetAllItemsQuery query = new GetAllItemsQuery(userId);
    List<Item> items = getAllItemsQueryHandler.handle(query);

    List<ItemResponse> response = itemMapper.toResponseList(items);

    log.info("Retrieved {} items for user: {}", response.size(), userId);
    return ResponseEntity.ok(response);
  }

  private String getCurrentUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    assert authentication != null;
    return authentication.getName();
  }
}

