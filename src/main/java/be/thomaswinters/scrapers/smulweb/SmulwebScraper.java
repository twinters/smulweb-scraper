package be.thomaswinters.scrapers.smulweb;

import be.thomaswinters.scrapers.smulweb.data.SmulwebRecipeCard;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

public class SmulwebScraper {

    private static URL CONTEXT_URL;

    static {
        try {
            CONTEXT_URL = new URL("http://www.smulweb.nl/");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private static final String BASE_SEARCH_URL = "http://www.smulweb.nl/recepten/";
    private static final String PAGE_SPECIFIER = "?page=";
    private static final int MAX_SEARCH_PAGE = 5000;
    private static final int MAX_MILLISECONDS_TIMEOUT = 60 * 1000;
    private static Random random = new Random();

    //region Search result scraper
    private String createSearchUrl(String searchWord, int pageNr) {
        return BASE_SEARCH_URL + searchWord + PAGE_SPECIFIER + pageNr;
    }

    private List<SmulwebRecipeCard> scrapeSearchResult(String url) throws IOException {
        Connection conn = Jsoup.connect(url).userAgent("Mozilla").timeout(MAX_MILLISECONDS_TIMEOUT)
                .followRedirects(true);

        Connection.Response resp = conn.execute();

        if (resp.statusCode() == 200) {
            Document doc = conn.get();

            Elements titleElements = doc.select("h2");
            return titleElements.stream()
                    .flatMap(e -> e.children()
                            .stream()
                            .map(this::scrapeSearchResult)
                            .filter(Optional::isPresent)
                            .map(Optional::get))
                    .collect(Collectors.toList());
        }
        throw new RuntimeException("Error connecting Smulweb: " + resp);
    }

    private Optional<SmulwebRecipeCard> scrapeSearchResult(Element element) {
        if (element.tagName().equals("a")) {
            String title = element.text();
            String urlString = element.attr("href");
            URL url = null;
            try {
                url = new URL(CONTEXT_URL, urlString);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return Optional.of(new SmulwebRecipeCard(title, url));
        }
        return Optional.empty();

    }


    public List<SmulwebRecipeCard> search(String s, int pageNr) throws IOException {
        return scrapeSearchResult(createSearchUrl(s, pageNr));
    }

    public List<SmulwebRecipeCard> search(String s) throws IOException {
        return search(s, 0);
    }
    //endregion

    //region Scraping random titles
    private String generateRandomSearchPage() {
        return createSearchUrl("", random.nextInt(MAX_SEARCH_PAGE));
    }

    public List<String> scrapeSomeTitles() throws IOException {
        String page = generateRandomSearchPage();
        return scrapeSearchResult(page)
                .stream()
                .map(SmulwebRecipeCard::getTitle)
                .collect(Collectors.toList());
    }
    //endregion

}
