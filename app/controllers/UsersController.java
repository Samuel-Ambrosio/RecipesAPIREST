package controllers;

import domain.utils.Attrs;
import domain.utils.AuthenticationAction;
import domain.entities.Token;
import domain.entities.User;
import domain.errors.Error;
import domain.mappers.UserMapper;
import domain.requests.ContentType;
import domain.requests.AuthUserRequest;
import domain.requests.RegisterUserRequest;
import domain.utils.PasswordHandler;
import play.data.Form;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.With;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import static domain.entities.Token.*;
import static domain.errors.ErrorCode.INVALID_CREDENTIALS;
import static domain.errors.ErrorCode.USER_EMAIL_ALREADY_REGISTERED;

public class UsersController extends BaseController {

    public Result register(final Http.Request request) {
        final ContentType contentType = getContentType(request);
        if (contentType == null) return badRequest("Unsupported format.");

        final Form<RegisterUserRequest> registerUserRequest = formFactory.form(RegisterUserRequest.class).bindFromRequest(request);
        if (registerUserRequest.hasErrors()) {
            return errorToResultWithContentType(contentType, registerUserRequest.errors());
        }

        final boolean isEmailAlreadyRegistered = User.getByEmail(registerUserRequest.get().getEmail()) != null;
        if (isEmailAlreadyRegistered) {
            return errorToResultWithContentType(contentType, new Error(USER_EMAIL_ALREADY_REGISTERED));
        }

        final User user = UserMapper.toEntity(registerUserRequest.get());
        user.save();

        final Result result;
        switch (contentType) {
            case JSON:
                result = created(Json.toJson(user)).as(ContentType.JSON.getContentType());
                break;
            case XML:
                result = created(views.xml.user.render(user)).as(ContentType.XML.getContentType());
                break;
            default:
                result = internalServerError();
                break;
        }

        return result;
    }

    public Result authenticate(final Http.Request request) {
        final ContentType contentType = getContentType(request);
        if (contentType == null) return badRequest("Unsupported format.");
        final Form<AuthUserRequest> authUserRequest = formFactory.form(AuthUserRequest.class).bindFromRequest(request);
        if (authUserRequest.hasErrors()) return errorToResultWithContentType(contentType,  authUserRequest.errors());

        final String email = authUserRequest.get().getEmail();
        final String password = authUserRequest.get().getPassword();

        final User user = User.getByEmail(email);
        final boolean areCredentialsValid = user != null && PasswordHandler.checkPassword(password, user.getPassword());
        if (!areCredentialsValid) return errorToResultWithContentType(contentType, new Error(INVALID_CREDENTIALS));

        Token.deleteByUser(user);

        final Token token = new Token();
        token.setToken(UUID.randomUUID().toString());
        token.setExpirationDate(Timestamp.from(Instant.now().plusSeconds(TOKEN_DURATION)));
        token.setUser(user);
        token.save();

        final Result result;
        switch (contentType) {
            case JSON:
                result = ok(Json.newObject().put("token", token.getToken())).as(ContentType.JSON.getContentType());
                break;
            case XML:
                result = ok(views.xml.token.render(token.getToken())).as(ContentType.XML.getContentType());
                break;
            default:
                result = internalServerError();
                break;
        }

        return result;
    }

    @With(AuthenticationAction.class)
    public Result getProfile(final Http.Request request) {
        final ContentType contentType = getContentType(request);
        if (contentType == null) return badRequest("Unsupported format.");
        final User user = request.attrs().get(Attrs.USER);
        if (user == null) return unauthorized();

        final Result result;
        switch (contentType) {
            case JSON:
                result = ok(Json.toJson(user)).as(ContentType.JSON.getContentType());
                break;
            case XML:
                result = ok(views.xml.user.render(user)).as(ContentType.XML.getContentType());
                break;
            default:
                result = internalServerError();
                break;
        }

        return result;
    }

    @With(AuthenticationAction.class)
    public Result delete(final Http.Request request) {
        final User user = request.attrs().get(Attrs.USER);
        if (user == null) return unauthorized();

        Token.deleteByUser(user);
        User.deleteById(user.getId());

        return ok();
    }
}
