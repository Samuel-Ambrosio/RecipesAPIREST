package controllers;

import domain.entities.Recipe;
import domain.errors.Error;
import domain.mappers.RecipeMapper;
import domain.requests.RecipeRequest;
import domain.requests.ContentType;

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
                result = (recipes.size() == 0 ? noContent() : ok(Json.toJson(recipes))).as(ContentType.JSON.getContentType());
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
                result = (recipe == null ? notFound() : ok(Json.toJson(recipe))).as(ContentType.JSON.getContentType());
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
                    result = created(Json.toJson(recipe)).as(ContentType.JSON.getContentType());
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
}
