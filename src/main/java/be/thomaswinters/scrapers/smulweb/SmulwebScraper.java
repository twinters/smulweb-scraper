package be.thomaswinters.scrapers.smulweb;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class SmulwebScraper {

	private static final String BASE_SEARCH_URL = "http://www.smulweb.nl/recepten?page=";
	private static final int MAX_SEARCH_PAGE = 5000;
	private static final int MAX_MILLISECONDS_TIMEOUT = 60 * 1000;
	private static Random random = new Random();

	private String generateRandomSearchPage() {
		return BASE_SEARCH_URL + random.nextInt(MAX_SEARCH_PAGE);
	}

	public List<String> scrapeSomeTitles() throws IOException {
		String page = generateRandomSearchPage();
		Connection conn = Jsoup.connect(page).userAgent("Mozilla").timeout(MAX_MILLISECONDS_TIMEOUT)
				.followRedirects(true);

		Connection.Response resp = conn.execute();

		if (resp.statusCode() == 200) {
			Document doc = conn.get();

			Elements titleElements = doc.select("h2");
			List<String> titles = titleElements.stream().flatMap(e -> e.children().stream().map(f -> f.text()))
					.collect(Collectors.toList());
			return titles;
		}
		throw new RuntimeException("Error connecting Smulweb: " + resp);
	}

}
