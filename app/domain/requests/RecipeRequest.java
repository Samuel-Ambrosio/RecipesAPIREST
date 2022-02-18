package domain.requests;

import domain.entities.Ingredient;
import domain.entities.Step;
import play.data.validation.Constraints;

import javax.validation.Constraint;
import java.util.ArrayList;
import java.util.List;

public class RecipeRequest {
    @Constraints.Required
    private String title;
    @Constraints.Required
    private String subtitle;
    @Constraints.Required
    private String summary;
    @Constraints.Required()
    private List<Step> steps = new ArrayList<>();
    @Constraints.Required
    private List<Ingredient> ingredients = new ArrayList<>();
    @Constraints.Required
    private List<String> images = new ArrayList<>();
    private List<String> links = new ArrayList<>();
    private List<String> tags = new ArrayList<>();

    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public String getSummary() { return summary; }
    public List<Step> getSteps() { return steps; }
    public List<Ingredient> getIngredients() { return ingredients; }
    public List<String> getImages() { return images; }
    public List<String> getLinks() { return links; }
    public List<String> getTags() { return tags; }

    public void setTitle(String title) { this.title = title; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }
    public void setSummary(String summary) { this.summary = summary; }
    public void setSteps(List<Step> steps) { this.steps = steps; }
    public void setIngredients(List<Ingredient> ingredients) { this.ingredients = ingredients; }
    public void setImages(List<String> images) { this.images = images; }
    public void setLinks(List<String> links) { this.links = links; }
    public void setTags(List<String> tags) { this.tags = tags; }
}
