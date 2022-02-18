package domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.ebean.Expr;
import io.ebean.Finder;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;


@Entity
@Table(name = "users")
public class User extends BaseModel {
    private static final Finder<Long, User> finder = new Finder<>(User.class);

    public static User getByEmail(final String email) { return finder.query().where(Expr.eq("email", email)).findOne(); }
    public static User getById(final long id) { return finder.byId(id); }
    public static void deleteById(final long id) { finder.deleteById(id); }

    private String email;
    private String password;

    @OneToOne(cascade = CascadeType.ALL)
    @JsonManagedReference
    private Author author;

    @JsonIgnore
    @Override
    public Long getId() { return super.getId(); }

    public String getEmail() { return email; }
    @JsonIgnore
    public String getPassword() { return password; }
    public Author getAuthor() { return author; }

    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setAuthor(Author author) { this.author = author; }
}
