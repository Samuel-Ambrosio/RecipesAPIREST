package domain.errors;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.data.validation.ValidationError;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.twirl.api.Content;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static domain.errors.ErrorCode.VALIDATION_FAILED;
import static play.mvc.Results.badRequest;
import static play.mvc.Results.internalServerError;

public class Error extends Exception {

    private final ErrorCode code;

    public Error(final ErrorCode code) {
        super(code.getMessage());
        this.code = code;
    }

    public Error(final ErrorCode code, final ValidationError validationError) {
        super(validationError.key() + ": " + validationError.message());
        this.code = code;
    }

    public String getCode() { return code.getCode(); }

    private JsonNode parseError() {
        final ObjectNode json = Json.newObject();
        json.put("errorCode", code.getCode());
        json.put("errorMessage", getMessage());
        return json;
    }

    private static JsonNode toJson(final Error ... errors) {
        return Json.newObject().set("errors", Json.newArray().addAll(Arrays.stream(errors).map(Error::parseError).collect(Collectors.toList())));
    }

    private static Content toXML(final Error ... errors) {
        return views.xml.errors.render(Arrays.stream(errors).collect(Collectors.toList()));
    }

    public static Result toResult(final Boolean isJson, final List<ValidationError> validationErrors) {
        return Error.toResult(isJson, validationErrors.stream().map(validationError -> (new Error(VALIDATION_FAILED, validationError))).toArray(Error[]::new));
    }

    public static Result toResult(final Boolean isJson, final Error... errors) {
        final JsonNode json = Error.toJson(errors);
        final Content xml = Error.toXML(errors);
        Result result = isJson ? internalServerError(json) : internalServerError(xml);

        switch (errors[0].code.getHttpStatus()) {
            case Http.Status.BAD_REQUEST: result = isJson ? badRequest(json) : badRequest(xml); break;
            case Http.Status.INTERNAL_SERVER_ERROR: break;
        }

        return result;
    }
}
