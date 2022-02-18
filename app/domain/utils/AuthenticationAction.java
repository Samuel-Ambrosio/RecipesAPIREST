package domain.utils;

import domain.entities.Token;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

import java.util.concurrent.CompletionStage;

public class AuthenticationAction extends Action.Simple {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String AUTHORIZATION_TYPE = "Bearer";

    @Override
    public CompletionStage<Result> call(Http.Request req) {
        final String authorizationHeaderValue = req.getHeaders().get(AUTHORIZATION_HEADER).orElse(null);
        final String token = (authorizationHeaderValue != null && authorizationHeaderValue.startsWith(AUTHORIZATION_TYPE))
                ? authorizationHeaderValue.substring(7) : null;

        return delegate.call(req.addAttr(Attrs.USER, Token.getUserIfTokenIsValid(token)));
    }
}