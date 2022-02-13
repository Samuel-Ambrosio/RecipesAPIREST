package domain.errors;


import play.mvc.Http;

public enum ErrorCode {
    VALIDATION_FAILED(Http.Status.BAD_REQUEST, "EC_000001", "Validation failed"),
    AUTHOR_NOT_FOUND(Http.Status.INTERNAL_SERVER_ERROR, "EC_000002", "Author not found.");

    private final int httpStatus;
    private final String code;
    private final String message;

    ErrorCode(final int httpStatus, final String code, final String description) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = description;
    }

    public int getHttpStatus() { return httpStatus; }
    public String getCode() { return code; }
    public String getMessage() { return message; }
}