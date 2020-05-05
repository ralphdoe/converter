package biz.netcentric.parser;

import biz.netcentric.exceptions.SlightlyException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * Integration Test
 *
 * @author Rafael Lopez
 */
public class HTMLParserTest {

    private static final String UTF_8 = "UTF-8";
    private static final String VALID_TEST_FILE = "test.html";
    private static final String WRONG_SCRIPT_TEST_FILE = "wrongScripts.html";
    private static final String VALID_INDEX_FILE = "index.html";
    private static final String TEST_TITLE = "Test";
    private static final String TEST_HEADER = "Test Page";
    private static final String NAME = "Erik";
    private static final String CHILD = "Child: Child ";
    private static final String TITLE_MESSAGE = "The title must the expected.";
    private static final String TITLE_NUMBER_MESSAGE = "The title number must the expected.";
    private static final String CHILD_MESSAGE = "The child must the expected.";
    private static final String CHILD_NUMBER_MESSAGE = "The child number must the expected.";
    private static final int TITLE_NUMBER = 1;
    private static final int DIV_NUMBER = 3;

    private ClassLoader classLoader;

    @Before
    public void setUp() {
        classLoader = getClass().getClassLoader();
    }

    @Test(expected = SlightlyException.class)
    public void wrongScriptTest() {
        File file = new File(classLoader.getResource(WRONG_SCRIPT_TEST_FILE).getFile());
        HTMLParser htmlParser = new HTMLParser();
        htmlParser.parseHTML(null, file);
    }

    @Test
    public void sameResponseTest() {
        File file = new File(classLoader.getResource(VALID_TEST_FILE).getFile());
        HTMLParser htmlParser = new HTMLParser();
        String parsedHTML = htmlParser.parseHTML(null, file);
        Document document = Jsoup.parse(parsedHTML, UTF_8);

        Elements title = document.head().getElementsByTag("title");

        assertEquals(TITLE_NUMBER_MESSAGE, title.size(), TITLE_NUMBER);
        assertEquals(TITLE_MESSAGE, title.text(), TEST_TITLE);

        Elements h1 = document.body().getElementsByTag("h1");
        assertEquals(TITLE_NUMBER_MESSAGE, h1.size(), TITLE_NUMBER);
        assertEquals(TITLE_MESSAGE, h1.text(), TEST_HEADER);
    }

    @Test
    public void processingFileTest() {
        File file = new File(classLoader.getResource(VALID_INDEX_FILE).getFile());
        HTMLParser htmlParser = new HTMLParser();
        String parsedHTML = htmlParser.parseHTML(null, file);
        Document document = Jsoup.parse(parsedHTML, UTF_8);

        Elements title = document.head().getElementsByTag("title");
        assertEquals(TITLE_MESSAGE, title.text(), NAME);

        Elements divs = document.body().getElementsByTag("div");
        assertEquals(CHILD_NUMBER_MESSAGE, DIV_NUMBER, divs.size());

        int i = 0;
        for (final Element div : divs) {
            assertEquals(CHILD_MESSAGE, CHILD + i, div.text());
            i++;
        }
    }
}
