package sk.cyrilgavala.wardrobeapi.item.application.query;

public record GetItemsByCategoryQuery(
    String userId,
    String category
) {

}

