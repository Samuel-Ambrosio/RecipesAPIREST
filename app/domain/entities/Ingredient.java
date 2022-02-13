package domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ingredients")
public class Ingredient extends BaseModel {

    @ManyToMany(mappedBy = "ingredients")
    private Set<RecipeDetail> recipeDetails = new HashSet<>();

    private String name;
    private Double quantity;
    private String unit;

    @JsonIgnore
    @Override
    public Long getId() { return super.getId(); }
    @JsonIgnore
    @Override
    public Timestamp getWhenCreated() { return super.getWhenCreated(); }

    public String getName() { return name; }
    public Double getQuantity() { return quantity; }
    public String getUnit() { return unit; }
    @JsonIgnore
    public Set<RecipeDetail> getRecipeDetails() { return recipeDetails; }

    public void setName(String name) { this.name = name; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }
    public void setUnit(String unit) { this.unit = unit; }
    public void setRecipeDetails(Set<RecipeDetail> recipeDetails) { this.recipeDetails = recipeDetails; }
    public void addRecipeDetails(RecipeDetail recipeDetail) { recipeDetails.add(recipeDetail); }
}