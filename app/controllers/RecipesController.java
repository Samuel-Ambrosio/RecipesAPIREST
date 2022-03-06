package controllers;

import domain.utils.Attrs;
import domain.utils.AuthenticationAction;
import domain.entities.Recipe;
import domain.entities.User;
import domain.errors.Error;
import domain.mappers.RecipeMapper;
import domain.requests.RecipeRequest;
import domain.requests.ContentType;

import domain.requests.RecipeUpdateRequest;
import play.data.Form;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.With;
import scala.Option;

import java.util.List;
import java.util.Objects;

public class RecipesController extends BaseController {

    public Result getRecipes(
        final Http.Request request,
        final int page,
        final int pageSize,
        final Option<scala.Boolean> all
    ) {
        final ContentType contentType = getContentType(request);
        if (contentType == null) return badRequest("Unsupported format.");
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
                result = internalServerError();
                break;
        }

        return result;
    }

    public Result getRecipeById(final Http.Request request, final int id) {
        final ContentType contentType = getContentType(request);
        if (contentType == null) return badRequest("Unsupported format.");
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
                result = internalServerError();
                break;
        }

        return result;
    }

    public Result getRecipesByQuerySearch(final Http.Request request, final String query) {
        final ContentType contentType = getContentType(request);
        if (contentType == null) return badRequest("Unsupported format.");
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
                result = internalServerError();
                break;
        }

        return result;
    }

    @With(AuthenticationAction.class)
    public Result createRecipe(final Http.Request request) {
        final ContentType contentType = getContentType(request);
        if (contentType == null) return badRequest("Unsupported format.");
        final User user = request.attrs().get(Attrs.USER);
        if (user == null) return unauthorized();
        final Form<RecipeRequest> recipeRequestForm = formFactory.form(RecipeRequest.class).bindFromRequest(request);
        if (recipeRequestForm.hasErrors()) return errorToResultWithContentType(contentType, recipeRequestForm.errors());

        try {
            final Recipe recipe = RecipeMapper.toEntity(recipeRequestForm.get(), user.getAuthor().getId());
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
            return errorToResultWithContentType(contentType, error);
        }
    }

    @With(AuthenticationAction.class)
    public Result deleteRecipeById(final Http.Request request, final int id) {
        final User user = request.attrs().get(Attrs.USER);
        if (user == null) return unauthorized();

        final Recipe recipe = Recipe.getById(id);
        final boolean isRecipeFound = recipe != null;
        if (!isRecipeFound) return notFound();

        final boolean isUserAuthorOfRecipe = Objects.equals(recipe.getAuthor().getId(), user.getAuthor().getId());
        if (isUserAuthorOfRecipe) Recipe.deleteById(id);

        return !isUserAuthorOfRecipe ? unauthorized() : ok();
    }

    @With(AuthenticationAction.class)
    public Result updateRecipeById(final Http.Request request, final int id) {
        final ContentType contentType = getContentType(request);
        if (contentType == null) return badRequest("Unsupported format.");
        final User user = request.attrs().get(Attrs.USER);
        if (user == null) return unauthorized();

        final Form<RecipeUpdateRequest> recipeRequestForm = formFactory.form(RecipeUpdateRequest.class).bindFromRequest(request);
        if (recipeRequestForm.hasErrors()) return errorToResultWithContentType(contentType, recipeRequestForm.errors());

        final Recipe recipe = Recipe.getById(id);
        if (recipe == null) return notFound();
        final RecipeUpdateRequest recipeUpdateRequest = recipeRequestForm.get();

        final boolean isUserAuthorOfRecipe = Objects.equals(recipe.getAuthor().getId(), user.getAuthor().getId());
        if (!isUserAuthorOfRecipe) return unauthorized();

        if (recipeUpdateRequest.getTitle() != null) recipe.setTitle(recipeUpdateRequest.getTitle());
        if (recipeUpdateRequest.getSubtitle() != null) recipe.setSubtitle(recipeUpdateRequest.getSubtitle());
        if (recipeUpdateRequest.getSummary() != null) recipe.setSubtitle(recipeUpdateRequest.getSummary());
        if (recipeUpdateRequest.getSteps().size() > 0) recipe.getDetails().setSteps(recipeUpdateRequest.getSteps());
        if (recipeUpdateRequest.getIngredients().size() > 0) recipe.getDetails().setIngredients(recipeUpdateRequest.getIngredients());
        if (recipeUpdateRequest.getImages().size() > 0) recipe.getDetails().setImages(recipeUpdateRequest.getImages());
        if (recipeUpdateRequest.getTags().size() > 0) recipe.getDetails().setTags(recipeUpdateRequest.getTags());
        if (recipeUpdateRequest.getLinks().size() > 0) recipe.getDetails().setLinks(recipeUpdateRequest.getLinks());
        recipe.update();

        final Result result;
        switch (contentType) {
            case JSON:
                result = ok(RecipeMapper.toJson(recipe)).as(ContentType.JSON.getContentType());
                break;
            case XML:
                result = ok(views.xml.recipe.render(recipe, true, true)).as(ContentType.XML.getContentType());
                break;
            default:
                result = internalServerError();
                break;
        }

        return result;
    }
}
