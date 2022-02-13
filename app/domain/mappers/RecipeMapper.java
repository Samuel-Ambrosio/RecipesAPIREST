package domain.mappers;

import domain.entities.Recipe;
import domain.entities.RecipeDetail;
import domain.errors.Error;
import domain.requests.RecipeRequest;


public class RecipeMapper {

    public static Recipe toEntity(final RecipeRequest recipeRequest) throws Error {
        final Recipe recipe = new Recipe();

        recipe.setAuthorId(recipeRequest.getAuthorId());

        recipe.setTitle(recipeRequest.getTitle());
        recipe.setSubtitle(recipeRequest.getSubtitle());
        recipe.setSummary(recipeRequest.getSummary());
        //recipe.setImages(recipeRequest.getImages());

        final RecipeDetail recipeDetail = new RecipeDetail();
        recipeDetail.setSteps(recipeRequest.getSteps());
        recipeDetail.setIngredients(recipeRequest.getIngredients());
        //recipeDetail.setTags(recipeRequest.getTags());
        //recipeDetail.setLinks(recipeRequest.getLinks());

        recipe.setDetails(recipeDetail);

        return recipe;
    }
}
