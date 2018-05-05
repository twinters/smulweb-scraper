package be.thomaswinters.scrapers.smulweb.data;

import java.net.URL;

public class SmulwebRecipeCard {
    private final String title;
    private final URL fullPageUrl;
    
    public SmulwebRecipeCard(String title, URL fullPageUrl) {

        this.title = title;
        this.fullPageUrl = fullPageUrl;
    }

    public String getTitle() {
        return title;
    }

    public URL getFullPageUrl() {
        return fullPageUrl;
    }
}
