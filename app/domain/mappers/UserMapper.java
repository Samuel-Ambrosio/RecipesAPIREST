package domain.mappers;

import domain.entities.Author;
import domain.entities.User;
import domain.requests.RegisterUserRequest;
import domain.utils.PasswordHandler;

public class UserMapper {
    public static User toEntity(final RegisterUserRequest registerUserRequest) {
        final User user = new User();
        user.setEmail(registerUserRequest.getEmail());
        user.setPassword(PasswordHandler.createPassword(registerUserRequest.getPassword()));

        final Author author = new Author();
        author.setName(registerUserRequest.getName());
        author.setSurname(registerUserRequest.getSurname());
        user.setAuthor(author);

        return user;
    }
}
