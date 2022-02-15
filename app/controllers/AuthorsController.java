package controllers;

import domain.entities.Author;
import domain.entities.Recipe;
import domain.errors.Error;
import domain.mappers.AuthorMapper;
import domain.requests.AuthorRequest;
import domain.requests.ContentType;
import play.data.Form;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import scala.Boolean;
import scala.Option;

import java.util.List;

public class AuthorsController extends BaseController {

    public Result getAuthors(
        final Http.Request request,
        final int page,
        final int pageSize,
        final Option<Boolean> all
    ) {
        final ContentType contentType = getContentType(request);
        final Result result;
        final List<Author> authors =  all.getOrElse(() -> false) ? Author.getAll() : Author.getPaginated(page, pageSize).getList();

        switch (contentType) {
            case JSON:
                result = (authors.size() == 0 ? noContent() : ok(Json.toJson(authors))).as(ContentType.JSON.getContentType());
                break;
            case XML:
                result = (authors.size() == 0 ? noContent() : ok(views.xml.authors.render(authors))).as(ContentType.XML.getContentType());
                break;
            default:
                result = badRequest("Unsupported format.").as(ContentType.UNSUPPORTED_FORMAT.getContentType());
                break;
        }

        return result;
    }

    public Result getAuthorById(final Http.Request request, final int id) {
        final ContentType contentType = getContentType(request);
        final Result result;
        final Author author = Author.getById(id);

        switch (contentType) {
            case JSON:
                result = (author == null ? notFound() : ok(Json.toJson(author))).as(ContentType.JSON.getContentType());
                break;
            case XML:
                result = (author == null ? notFound() : ok(views.xml.author.render(author, true))).as(ContentType.XML.getContentType());
                break;
            default:
                result = badRequest("Unsupported format.").as(ContentType.UNSUPPORTED_FORMAT.getContentType());
                break;
        }

        return result;
    }

    public Result createAuthor(final Http.Request request) {
        final ContentType contentType = getContentType(request);
        if (contentType == null)
            return badRequest("Unsupported format.").as(ContentType.UNSUPPORTED_FORMAT.getContentType());

        final Form<AuthorRequest> authorRequestForm = formFactory.form(AuthorRequest.class).bindFromRequest(request);
        if (authorRequestForm.hasErrors()) {
            return Error.toResult(contentType == ContentType.JSON, authorRequestForm.errors()).as(contentType == ContentType.JSON ? ContentType.JSON.getContentType() : ContentType.XML.getContentType());
        }

        final Author author = AuthorMapper.toEntity(authorRequestForm.get());
        author.save();

        final Result result;
        switch (contentType) {
            case JSON:
                result = created(Json.toJson(author)).as(ContentType.JSON.getContentType());
                break;
            case XML:
                result = created(views.xml.author.render(author, true)).as(ContentType.XML.getContentType());
                break;
            default:
                result = internalServerError();
                break;
        }

        return result;
    }

    public Result deleteAuthorById(final Http.Request request, final int id) {
        final Author author = Author.getById(id);
        if (author != null) Author.deleteById(id);

        return (author == null ? notFound() : ok());
    }
}
