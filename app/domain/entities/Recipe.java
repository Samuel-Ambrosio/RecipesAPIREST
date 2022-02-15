package domain.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import domain.errors.Error;
import domain.errors.ErrorCode;
import io.ebean.Expr;
import io.ebean.Finder;
import io.ebean.PagedList;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "recipes")
public class Recipe extends BaseModel {

    private static final Finder<Long, Recipe> finder = new Finder<>(Recipe.class);

    public static List<Recipe> getAll() { return finder.all(); }
    public static PagedList<Recipe> getPaginated(int page, int pageSize) {
        return finder.query().setFirstRow(page).setMaxRows(pageSize).findPagedList();
    }
    public static Recipe getById(final long id) { return finder.byId(id); }
    public static List<Recipe> getQuery(final String query) {
        return finder.query().where().or(
                Expr.or(Expr.icontains("title", query), Expr.icontains("details.ingredients.name", query)),
                Expr.or(Expr.icontains("author.name", query), Expr.icontains("author.surname", query))
        ).findList();
    }
    public static void deleteById(final long id) { finder.deleteById(id); }

    private String title;
    private String subtitle;
    private String summary;

    @ManyToOne
    @JsonBackReference
    private Author author;

    @OneToOne(cascade = CascadeType.ALL)
    @JsonManagedReference
    private RecipeDetail details;

    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public String getSummary() { return summary; }
    public Author getAuthor() { return author; }
    public RecipeDetail getDetails() { return details; }

    public void setTitle(String title) { this.title = title; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }
    public void setSummary(String summary) { this.summary = summary; }
    public void setAuthorId(Long authorId) throws Error {
        final Author author = Author.getById(authorId);
        if (author != null) {
            author.addRecipe(this);
        } else {
            throw new Error(ErrorCode.AUTHOR_NOT_FOUND);
        }
    }
    public void setAuthor(Author author) { this.author = author; }
    public void setDetails(RecipeDetail details) { this.details = details; }
}
