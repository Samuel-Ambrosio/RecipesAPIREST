package domain.errors;


import play.mvc.Http;

public enum ErrorCode {
    VALIDATION_FAILED(Http.Status.BAD_REQUEST, "EC_000001", "Validation failed"),
    AUTHOR_NOT_FOUND(Http.Status.NOT_FOUND, "EC_000002", "Author not found."),
    USER_EMAIL_ALREADY_REGISTERED(Http.Status.CONFLICT, "EC_000003", "Email already registered."),
    INVALID_CREDENTIALS(Http.Status.UNAUTHORIZED, "EC_000004", "Invalid credentials.");

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