package domain.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import domain.entities.Recipe;
import domain.entities.RecipeDetail;
import domain.errors.Error;
import domain.requests.RecipeRequest;
import play.libs.Json;


public class RecipeMapper {

    public static Recipe toEntity(final RecipeRequest recipeRequest) throws Error {
        final Recipe recipe = new Recipe();

        recipe.setAuthorId(recipeRequest.getAuthorId());

        recipe.setTitle(recipeRequest.getTitle());
        recipe.setSubtitle(recipeRequest.getSubtitle());
        recipe.setSummary(recipeRequest.getSummary());

        final RecipeDetail recipeDetail = new RecipeDetail();
        recipeDetail.setSteps(recipeRequest.getSteps());
        recipeDetail.setIngredients(recipeRequest.getIngredients());
        recipeDetail.setImages(recipeRequest.getImages());
        recipeDetail.setTags(recipeRequest.getTags());
        recipeDetail.setLinks(recipeRequest.getLinks());

        recipe.setDetails(recipeDetail);

        return recipe;
    }

    public static JsonNode toJson(final Recipe recipe) {
        JsonNode json = Json.toJson(recipe);
        ObjectNode authorJson = Json.newObject();

        authorJson.put("id", recipe.getAuthor().getId());
        authorJson.put("name", recipe.getAuthor().getName());
        authorJson.put("surname", recipe.getAuthor().getSurname());
        ((ObjectNode)json).set("author", authorJson);

        return json;
    }
}
