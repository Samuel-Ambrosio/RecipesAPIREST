package domain.requests;

import play.data.validation.Constraints;

public class RegisterUserRequest {
    @Constraints.Required
    @Constraints.Email
    private String email;
    @Constraints.Required
    @Constraints.MinLength(6)
    private String password;
    @Constraints.Required
    private String name;
    @Constraints.Required
    private String surname;

    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getName() { return name; }
    public String getSurname() { return surname; }

    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setName(String name) { this.name = name; }
    public void setSurname(String surname) { this.surname = surname; }
}
