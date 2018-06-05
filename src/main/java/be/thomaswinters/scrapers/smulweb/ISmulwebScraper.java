package be.thomaswinters.scrapers.smulweb;

import be.thomaswinters.scrapers.smulweb.data.SmulwebRecipeCard;

import java.io.IOException;
import java.util.List;

public interface ISmulwebScraper {
    List<SmulwebRecipeCard> search(String s, int pageNr) throws IOException;

    List<SmulwebRecipeCard> search(String s) throws IOException;

    List<String> scrapeSomeTitles() throws IOException;
}
