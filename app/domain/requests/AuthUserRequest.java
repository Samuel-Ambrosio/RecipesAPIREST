package domain.requests;

import play.data.validation.Constraints;

public class AuthUserRequest {
    @Constraints.Required
    @Constraints.Email
    private String email;
    @Constraints.Required
    @Constraints.MinLength(6)
    private String password;

    public String getEmail() { return email; }
    public String getPassword() { return password; }

    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
}
