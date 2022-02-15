package domain.entities;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.ebean.Finder;
import io.ebean.PagedList;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "authors")
public class Author extends BaseModel {

    private static final Finder<Long, Author> finder = new Finder<>(Author.class);

    public static List<Author> getAll() { return finder.all(); }
    public static PagedList<Author> getPaginated(int page, int pageSize) {
        return finder.query().setFirstRow(page).setMaxRows(pageSize).findPagedList();
    }
    public static Author getById(final long id) { return finder.byId(id); }
    public static void deleteById(final long id) { finder.deleteById(id); }

    private String name;
    private String surname;

    @OneToMany(cascade=CascadeType.ALL, mappedBy="author")
    @JsonManagedReference
    private List<Recipe> recipes = new ArrayList<>();

    public String getName() { return name; }
    public String getSurname() { return surname; }
    public List<Recipe> getRecipes() { return recipes; }

    public void setName(String name) { this.name = name; }
    public void setSurname(String surname) { this.surname = surname; }
    public void setRecipes(List<Recipe> recipes) {
        for (Recipe recipe: recipes) {
            recipe.setAuthor(this);
        }
        this.recipes = recipes;
    }
    public void addRecipe(Recipe recipe) {
        recipes.add(recipe);
        recipe.setAuthor(this);
    }
}
