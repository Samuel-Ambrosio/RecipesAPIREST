package domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "steps")
public class Step extends BaseModel {

    @ManyToOne
    private RecipeDetail parentRecipeDetail;

    private String title;
    private String description;

    @JsonIgnore
    @Override
    public Long getId() { return super.getId(); }
    @JsonIgnore
    @Override
    public Timestamp getWhenCreated() { return super.getWhenCreated(); }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    @JsonIgnore
    public RecipeDetail getParent() { return parentRecipeDetail; }

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setParent(RecipeDetail parentRecipeDetail) { this.parentRecipeDetail = parentRecipeDetail; }
}