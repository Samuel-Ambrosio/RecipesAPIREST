package domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.ebean.Expr;
import io.ebean.Finder;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "tokens")
public class Token extends BaseModel {

    public static final long TOKEN_DURATION = 86400L;

    private static final Finder<Long, Token> finder = new Finder<>(Token.class);

    public static User getUserIfTokenIsValid(final String token) {
        if (token == null) return null;

        final Token tokenQuery = finder.query().where(Expr.eq("token", token)).findOne();
        if (tokenQuery != null) {
            final boolean isNotExpired = tokenQuery.getExpirationDate().after(Timestamp.from(Instant.now()));
            return isNotExpired ? tokenQuery.getUser() : null;
        }

        return null;
    }
    public static void deleteByUser(final User user) {
        finder.query().where(Expr.eq("user.id", user.getId())).delete();
    }

    private String token;
    private Timestamp expirationDate;

    @OneToOne
    private User user;

    public String getToken() { return token; }
    public Timestamp getExpirationDate() { return expirationDate; }
    @JsonIgnore
    public User getUser() { return user; }

    public void setToken(String token) {  this.token = token; }
    public void setExpirationDate(Timestamp expirationDate) { this.expirationDate = expirationDate; }
    public void setUser(User user) { this.user = user; }
}
