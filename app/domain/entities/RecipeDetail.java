package domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.ebean.Finder;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recipes_details")
public class RecipeDetail extends BaseModel {

    private static final Finder<Long, RecipeDetail> finder = new Finder<>(RecipeDetail.class);

    public static List<RecipeDetail> getAll() { return finder.all(); }
    public static RecipeDetail getById(Long id) { return finder.byId(id); }

    @OneToMany(cascade= CascadeType.ALL, mappedBy="parentRecipeDetail")
    private List<Step> steps = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Ingredient> ingredients = new ArrayList<>();

    //private List<String> tags = new ArrayList<>();
    //private List<String> links = new ArrayList<>();

    @JsonIgnore
    @Override
    public Long getId() { return super.getId(); }
    @JsonIgnore
    @Override
    public Timestamp getWhenCreated() { return super.getWhenCreated(); }

    public List<Step> getSteps() { return steps; }
    public List<Ingredient> getIngredients() { return ingredients; }
    //public List<String> getTags() { return tags; }
    //public List<String> getLinks() { return links; }

    public void setSteps(List<Step> steps) {
        for (Step step: steps) {
            step.setParent(this);
        }
        this.steps = steps;
    }
    public void setIngredients(List<Ingredient> ingredients) {
        for (Ingredient ingredient: ingredients) {
            ingredient.addRecipeDetails(this);
        }
        this.ingredients = ingredients;
    }
    //public void setTags(List<String> tags) { this.tags = tags; }
    //public void setLinks(List<String> links) { this.links = links; }
}