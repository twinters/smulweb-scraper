package be.thomaswinters.scrapers.smulweb;

import be.thomaswinters.scrapers.smulweb.data.SmulwebRecipeCard;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class CachedSmulwebScraper implements ISmulwebScraper{
    private final SmulwebScraper smulwebScraper = new SmulwebScraper();


    private final Cache<SearchWordPageNumber, List<SmulwebRecipeCard>> searchWordPageNumberListCache =
            CacheBuilder.newBuilder().maximumSize(1000).build();
    @Override
    public List<SmulwebRecipeCard> search(String s, int pageNr) throws IOException {
        try {
            return searchWordPageNumberListCache.get(new SearchWordPageNumber(s, pageNr), ()->smulwebScraper.search(s,pageNr) );
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private final Cache<String, List<SmulwebRecipeCard>> searchWordCache =
            CacheBuilder.newBuilder().maximumSize(1000).build();
    @Override
    public List<SmulwebRecipeCard> search(String s) throws IOException {
        try {
            return searchWordCache.get(s, ()->smulwebScraper.search(s) );
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> scrapeSomeTitles() throws IOException {
        return smulwebScraper.scrapeSomeTitles();
    }

    private class SearchWordPageNumber {
        private final String s;
        private final int pageNr;

        public SearchWordPageNumber(String s, int pageNr) {
            this.s = s;
            this.pageNr = pageNr;
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SearchWordPageNumber that = (SearchWordPageNumber) o;
            return pageNr == that.pageNr &&
                    Objects.equals(s, that.s);
        }

        @Override
        public int hashCode() {
            return Objects.hash(s, pageNr);
        }

    }
}
