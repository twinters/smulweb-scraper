package be.thomaswinters.scrapers.smulweb.data;

import java.net.URL;

public class SmulwebRecipeCard {
    private final String title;
    private final String ingredients;
    private final URL fullPageUrl;
    
    public SmulwebRecipeCard(String title, String ingredients, URL fullPageUrl) {

        this.title = title;
        this.ingredients = ingredients;
        this.fullPageUrl = fullPageUrl;
    }

    public String getTitle() {
        return title;
    }
    public String getIngredients() {
        return ingredients;
    }

    public URL getFullPageUrl() {
        return fullPageUrl;
    }

    @Override
    public String toString() {
        return "SmulwebRecipeCard{" +
                "title='" + title + '\'' +
                ", ingredients='" + ingredients + '\'' +
                ", fullPageUrl=" + fullPageUrl +
                '}';
    }
}
