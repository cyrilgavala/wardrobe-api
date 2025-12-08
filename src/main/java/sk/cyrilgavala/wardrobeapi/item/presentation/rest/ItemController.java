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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sk.cyrilgavala.wardrobeapi.item.application.command.CreateItemCommand;
import sk.cyrilgavala.wardrobeapi.item.application.command.DeleteItemCommand;
import sk.cyrilgavala.wardrobeapi.item.application.command.UpdateItemCommand;
import sk.cyrilgavala.wardrobeapi.item.application.command.handler.CreateItemCommandHandler;
import sk.cyrilgavala.wardrobeapi.item.application.command.handler.DeleteItemCommandHandler;
import sk.cyrilgavala.wardrobeapi.item.application.command.handler.UpdateItemCommandHandler;
import sk.cyrilgavala.wardrobeapi.item.application.query.GetAllItemsQuery;
import sk.cyrilgavala.wardrobeapi.item.application.query.GetItemQuery;
import sk.cyrilgavala.wardrobeapi.item.application.query.GetItemsByCategoryQuery;
import sk.cyrilgavala.wardrobeapi.item.application.query.handler.GetAllItemsQueryHandler;
import sk.cyrilgavala.wardrobeapi.item.application.query.handler.GetItemQueryHandler;
import sk.cyrilgavala.wardrobeapi.item.application.query.handler.GetItemsByCategoryQueryHandler;
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
@SecurityRequirement(name = "Bearer Authentication")
public class ItemController {

  private final CreateItemCommandHandler createItemCommandHandler;
  private final UpdateItemCommandHandler updateItemCommandHandler;
  private final DeleteItemCommandHandler deleteItemCommandHandler;
  private final GetItemQueryHandler getItemQueryHandler;
  private final GetAllItemsQueryHandler getAllItemsQueryHandler;
  private final GetItemsByCategoryQueryHandler getItemsByCategoryQueryHandler;
  private final ItemDtoMapper itemMapper;

  @PostMapping
  @Operation(
      summary = "Create a new wardrobe item",
      description = "Creates a new wardrobe item for the authenticated user"
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Item created successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid request data"),
      @ApiResponse(responseCode = "401", description = "Unauthorized")
  })
  public ResponseEntity<ItemResponse> createItem(@Valid @RequestBody CreateItemRequest request) {
    String userId = getCurrentUserId();
    log.info("Received create item request for user: {}", userId);

    CreateItemCommand command = itemMapper.toCreateCommand(request, userId);
    Item item = createItemCommandHandler.handle(command);
    ItemResponse response = itemMapper.toResponse(item);

    log.info("Item created successfully with id: {}", item.id());
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PutMapping("/{id}")
  @Operation(
      summary = "Update an existing wardrobe item",
      description = "Updates an existing wardrobe item owned by the authenticated user"
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Item updated successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid request data"),
      @ApiResponse(responseCode = "401", description = "Unauthorized"),
      @ApiResponse(responseCode = "403", description = "Access denied to this item"),
      @ApiResponse(responseCode = "404", description = "Item not found")
  })
  public ResponseEntity<ItemResponse> updateItem(
      @Parameter(description = "Item ID", required = true)
      @PathVariable String id,
      @Valid @RequestBody UpdateItemRequest request) {
    String userId = getCurrentUserId();
    log.info("Received update item request for id: {} by user: {}", id, userId);

    UpdateItemCommand command = itemMapper.toUpdateCommand(request, id, userId);
    Item item = updateItemCommandHandler.handle(command);
    ItemResponse response = itemMapper.toResponse(item);

    log.info("Item updated successfully: {}", id);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  @Operation(
      summary = "Delete a wardrobe item",
      description = "Deletes a wardrobe item owned by the authenticated user"
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Item deleted successfully"),
      @ApiResponse(responseCode = "401", description = "Unauthorized"),
      @ApiResponse(responseCode = "403", description = "Access denied to this item"),
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
      description = "Retrieves a specific wardrobe item owned by the authenticated user"
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Item retrieved successfully"),
      @ApiResponse(responseCode = "401", description = "Unauthorized"),
      @ApiResponse(responseCode = "403", description = "Access denied to this item"),
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

  @GetMapping
  @Operation(
      summary = "Get all wardrobe items",
      description = "Retrieves all wardrobe items for the authenticated user, optionally filtered by category"
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Items retrieved successfully"),
      @ApiResponse(responseCode = "401", description = "Unauthorized")
  })
  public ResponseEntity<List<ItemResponse>> getAllItems(
      @Parameter(description = "Filter by category")
      @RequestParam(required = false) String category) {
    String userId = getCurrentUserId();
    log.info("Received get all items request for user: {} with category filter: {}", userId,
        category);

    List<Item> items;
    if (category != null) {
      GetItemsByCategoryQuery query = new GetItemsByCategoryQuery(userId, category);
      items = getItemsByCategoryQueryHandler.handle(query);
    } else {
      GetAllItemsQuery query = new GetAllItemsQuery(userId);
      items = getAllItemsQueryHandler.handle(query);
    }

    List<ItemResponse> response = itemMapper.toResponseList(items);

    log.info("Retrieved {} items for user: {}", response.size(), userId);
    return ResponseEntity.ok(response);
  }

  private String getCurrentUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication.getName();
  }
}

