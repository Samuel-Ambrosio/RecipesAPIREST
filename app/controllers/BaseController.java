package controllers;

import domain.errors.Error;
import domain.requests.ContentType;
import play.data.FormFactory;
import play.data.validation.ValidationError;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;

import java.util.List;

import static domain.errors.ErrorCode.USER_EMAIL_ALREADY_REGISTERED;

public class BaseController extends Controller {
    @Inject
    public FormFactory formFactory;

    protected ContentType getContentType(final Http.Request request) {
        ContentType type = null;
        if (request.accepts(ContentType.JSON.getContentType())) {
            type = ContentType.JSON;
        } else if (request.accepts(ContentType.XML.getContentType())) {
            type = ContentType.XML;
        }
        return type;
    }

    protected Result errorToResultWithContentType(
        final ContentType contentType,
        final List<ValidationError> validationErrors
    ) {
        return Error.toResult(contentType == ContentType.JSON, validationErrors)
                .as(contentType == ContentType.JSON ? ContentType.JSON.getContentType() : ContentType.XML.getContentType());
    }

    protected Result errorToResultWithContentType(
        final ContentType contentType,
        final Error error
    ) {
        return Error.toResult(contentType == ContentType.JSON, error)
                .as(contentType == ContentType.JSON ? ContentType.JSON.getContentType() : ContentType.XML.getContentType());
    }
}
