package controllers;

import domain.entities.Recipe;
import domain.errors.Error;
import domain.mappers.RecipeMapper;
import domain.requests.RecipeRequest;
import domain.requests.ContentType;

import domain.requests.RecipeUpdateRequest;
import play.data.Form;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import scala.Option;

import java.util.List;

public class RecipesController extends BaseController {

    public Result getRecipes(
        final Http.Request request,
        final int page,
        final int pageSize,
        final Option<scala.Boolean> all
    ) {
        final ContentType contentType = getContentType(request);
        final Result result;
        final List<Recipe> recipes = all.getOrElse(() -> false) ? Recipe.getAll() : Recipe.getPaginated(page, pageSize).getList();

        switch (contentType) {
            case JSON:
                result = (recipes.size() == 0 ? noContent() : ok(Json.toJson(recipes.stream().map(RecipeMapper::toJson)))).as(ContentType.JSON.getContentType());
                break;
            case XML:
                result = (recipes.size() == 0 ? noContent() : ok(views.xml.recipes.render(recipes))).as(ContentType.XML.getContentType());
                break;
            default:
                result = badRequest("Unsupported format.").as(ContentType.UNSUPPORTED_FORMAT.getContentType());
                break;
        }

        return result;
    }

    public Result getRecipeById(final Http.Request request, final int id) {
        final ContentType contentType = getContentType(request);
        final Result result;
        final Recipe recipe = Recipe.getById(id);

        switch (contentType) {
            case JSON:
                result = (recipe == null ? notFound() : ok(RecipeMapper.toJson(recipe))).as(ContentType.JSON.getContentType());
                break;
            case XML:
                result = (recipe == null ? notFound() : ok(views.xml.recipe.render(recipe, true, true))).as(ContentType.XML.getContentType());
                break;
            default:
                result = badRequest("Unsupported format.").as(ContentType.UNSUPPORTED_FORMAT.getContentType());
                break;
        }

        return result;
    }

    public Result getRecipesByQuerySearch(final Http.Request request, final String query) {
        final ContentType contentType = getContentType(request);
        final Result result;
        final List<Recipe> recipes = Recipe.getQuery(query);

        switch (contentType) {
            case JSON:
                result = (recipes.size() == 0 ? noContent() : ok(Json.toJson(recipes.stream().map(RecipeMapper::toJson)))).as(ContentType.JSON.getContentType());
                break;
            case XML:
                result = (recipes.size() == 0 ? noContent() : ok(views.xml.recipes.render(recipes))).as(ContentType.XML.getContentType());
                break;
            default:
                result = badRequest("Unsupported format.").as(ContentType.UNSUPPORTED_FORMAT.getContentType());
                break;
        }

        return result;
    }

    public Result createRecipe(final Http.Request request) {
        final ContentType contentType = getContentType(request);
        if (contentType == null)
            return badRequest("Unsupported format.").as(ContentType.UNSUPPORTED_FORMAT.getContentType());

        final Form<RecipeRequest> recipeRequestForm = formFactory.form(RecipeRequest.class).bindFromRequest(request);
        if (recipeRequestForm.hasErrors())
            return Error.toResult(contentType == ContentType.JSON, recipeRequestForm.errors()).as(contentType == ContentType.JSON ? ContentType.JSON.getContentType() : ContentType.XML.getContentType());

        try {
            final Recipe recipe = RecipeMapper.toEntity(recipeRequestForm.get());
            recipe.save();
            final Result result;

            switch (contentType) {
                case JSON:
                    result = created(RecipeMapper.toJson(recipe)).as(ContentType.JSON.getContentType());
                    break;
                case XML:
                    result = created(views.xml.recipe.render(recipe, true, true)).as(ContentType.XML.getContentType());
                    break;
                default:
                    result = internalServerError();
                    break;
            }

            return result;
        } catch (Error error) {
            return Error.toResult(contentType == ContentType.JSON, error).as(contentType == ContentType.JSON ? ContentType.JSON.getContentType() : ContentType.XML.getContentType());
        }
    }

    public Result deleteRecipeById(final Http.Request request, final int id) {
        final Recipe recipe = Recipe.getById(id);
        if (recipe != null) Recipe.deleteById(id);

        return (recipe == null ? notFound() : ok());
    }

    public Result updateRecipeById(final Http.Request request, final int id) {
        final ContentType contentType = getContentType(request);
        if (contentType == null)
            return badRequest("Unsupported format.").as(ContentType.UNSUPPORTED_FORMAT.getContentType());

        final Form<RecipeUpdateRequest> recipeRequestForm = formFactory.form(RecipeUpdateRequest.class).bindFromRequest(request);
        if (recipeRequestForm.hasErrors())
            return Error.toResult(contentType == ContentType.JSON, recipeRequestForm.errors()).as(contentType == ContentType.JSON ? ContentType.JSON.getContentType() : ContentType.XML.getContentType());

        final Recipe recipe = Recipe.getById(id);
        final RecipeUpdateRequest recipeUpdateRequest = recipeRequestForm.get();


        try {
            if (recipe != null) {
                if (recipeUpdateRequest.getTitle() != null) recipe.setTitle(recipeUpdateRequest.getTitle());
                if (recipeUpdateRequest.getSubtitle() != null) recipe.setSubtitle(recipeUpdateRequest.getSubtitle());
                if (recipeUpdateRequest.getSummary() != null) recipe.setSubtitle(recipeUpdateRequest.getSummary());
                if (recipeUpdateRequest.getAuthorId() != null) recipe.setAuthorId(recipeUpdateRequest.getAuthorId());
                if (recipeUpdateRequest.getSteps().size() > 0) recipe.getDetails().setSteps(recipeUpdateRequest.getSteps());
                if (recipeUpdateRequest.getIngredients().size() > 0) recipe.getDetails().setIngredients(recipeUpdateRequest.getIngredients());
                if (recipeUpdateRequest.getImages().size() > 0) recipe.getDetails().setImages(recipeUpdateRequest.getImages());
                if (recipeUpdateRequest.getTags().size() > 0) recipe.getDetails().setTags(recipeUpdateRequest.getTags());
                if (recipeUpdateRequest.getLinks().size() > 0) recipe.getDetails().setLinks(recipeUpdateRequest.getLinks());
                recipe.update();
            }

            final Result result;
            switch (contentType) {
                case JSON:
                    result = created(RecipeMapper.toJson(recipe)).as(ContentType.JSON.getContentType());
                    break;
                case XML:
                    result = ok(views.xml.recipe.render(recipe, true, true)).as(ContentType.XML.getContentType());
                    break;
                default:
                    result = internalServerError();
                    break;
            }

            return (recipe == null ? notFound() : result);
        } catch (Error error) {
            return Error.toResult(contentType == ContentType.JSON, error).as(contentType == ContentType.JSON ? ContentType.JSON.getContentType() : ContentType.XML.getContentType());
        }
    }
}
