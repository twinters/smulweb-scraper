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
import java.util.*;
import java.util.stream.Collectors;

public class SmulwebScraper implements ISmulwebScraper {

    private static URL CONTEXT_URL;
    private final List<String> prohibitedSmulwebWords = Collections.singletonList("special");

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

            Elements articles = doc.select("article");
            return articles.stream()
                    .map(this::scrapeSearchResult)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
//            return titleElements.stream()
//                    .flatMap(e -> e.children()
//                            .stream()
//                            .map(this::scrapeSearchResult)
//                            .filter(Optional::isPresent)
//                            .map(Optional::get))
//                    .collect(Collectors.toList());
        }
        throw new RuntimeException("Error connecting Smulweb: " + resp);
    }

    private Optional<SmulwebRecipeCard> scrapeSearchResult(Element element) {
//        if (element.tagName().equals("a")) {
        Element headerLink = element
                .getElementsByTag("h2").first()
                .getElementsByTag("a").first();
        String title = headerLink.text();

        
        String ingredients = "";
        Elements ingredientsElements = element
                .getElementsByClass("ingredienten").first()
                .getElementsByTag("p");
        if (ingredientsElements.size() > 0) {
            ingredients = ingredientsElements.first()
                    .text();
        }
        String urlString = headerLink.attr("href");
        URL url = null;
        try {
            url = new URL(CONTEXT_URL, urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return Optional.of(new SmulwebRecipeCard(title, ingredients, url));
//        }
//        return Optional.empty();

    }


    @Override
    public List<SmulwebRecipeCard> search(String searchWord, int pageNr) throws IOException {
        if (prohibitedSmulwebWords.stream().anyMatch(e -> searchWord.toLowerCase().contains(e))) {
            return new ArrayList<>();
        }
        return scrapeSearchResult(createSearchUrl(searchWord, pageNr));
    }

    @Override
    public List<SmulwebRecipeCard> search(String s) throws IOException {
        return search(s, 0);
    }
    //endregion

    //region Scraping random titles
    private String generateRandomSearchPage() {
        return createSearchUrl("", random.nextInt(MAX_SEARCH_PAGE));
    }

    @Override
    public List<String> scrapeSomeTitles() throws IOException {
        String page = generateRandomSearchPage();
        return scrapeSearchResult(page)
                .stream()
                .map(SmulwebRecipeCard::getTitle)
                .collect(Collectors.toList());
    }
    //endregion

    public static void main(String[] args) throws IOException {
        System.out.println(new SmulwebScraper().search("pizza"));
    }

}
