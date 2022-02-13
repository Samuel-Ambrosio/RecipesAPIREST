package domain.mappers;

import domain.entities.Author;
import domain.requests.AuthorRequest;

public class AuthorMapper {

    public static Author toEntity(final AuthorRequest authorRequest) {
        final Author author = new Author();
        author.setName(authorRequest.getName());
        author.setSurname(authorRequest.getSurname());
        return author;
    }
}
