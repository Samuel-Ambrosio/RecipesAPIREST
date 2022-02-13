package controllers;

import domain.requests.ContentType;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;

import javax.inject.Inject;

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
}
