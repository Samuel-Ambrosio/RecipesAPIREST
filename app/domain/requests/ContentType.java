package domain.requests;

public enum ContentType {
    JSON("application/json"),
    XML("application/xml"),
    UNSUPPORTED_FORMAT("text");

    final String contentType;
    ContentType(final String contentType) {
        this.contentType = contentType;
    }

    public String getContentType() { return contentType; }
}
