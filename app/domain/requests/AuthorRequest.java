package domain.requests;

import play.data.validation.Constraints;

public class AuthorRequest {
    @Constraints.Required
    private String name;
    @Constraints.Required
    private String surname;

    public String getName() { return name; }
    public String getSurname() { return surname; }

    public void setName(String name) { this.name = name; }
    public void setSurname(String surname) { this.surname = surname; }
}
