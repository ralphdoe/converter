package biz.netcentric.parser;

import biz.netcentric.exceptions.SlightlyException;
import biz.netcentric.script.ScriptExecutor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * HTMLParser Class - The class with the html processor
 * @author Rafael Lopez
 */
public final class HTMLParser {

    private static final String UTF_8 = "UTF-8";
    private static final String REQUEST_VARIABLE_NAME = "request";
    private static final String SCRIPT_TAG = "script";
    private static final String DATA_IF_ATTRIBUTE = "data-if";
    private static final String DATA_FOR_ATTRIBUTE = "data-for";
    private static final String TYPE = "type";
    private static final String SCRIPT = "server/javascript";
    private static final String EXPRESSION_PATTERN = "\\$\\{(.*?)}";
    private static final String EMPTY_LIST = "EMPTY_LIST";


    private static final Logger LOGGER = Logger.getLogger(HTMLParser.class);
    private ScriptExecutor scriptExecutor;

    /**
     * The main method of the parser, it reads the html, analyze it and return the parsed response
     * @param request client request
     * @param file    the html source
     * @return String htmlResponse
     * @throws IOException
     */
    public String parseHTML(final HttpServletRequest request, final File file) {
        scriptExecutor = new ScriptExecutor();

        scriptExecutor.sendAttribute(REQUEST_VARIABLE_NAME, request);
        StringBuilder htmlResponse = new StringBuilder();

        try {
            Document document = Jsoup.parse(file, UTF_8);
            executeScripts(document);
            htmlResponse.append(parseHTMLHead(document));
            htmlResponse.append(parseHTMLBody(document));
        } catch (final ScriptException | IOException e) {
            LOGGER.error(e.getMessage());
            throw new SlightlyException(e.getMessage(), e);
        }
        return htmlResponse.toString();
    }

    /**
     * Execute the script tags in the document
     * @param document
     * @throws ScriptException
     */
    private void executeScripts(final Document document) throws ScriptException {
        Elements scripts = document.getElementsByTag(SCRIPT_TAG);
        Optional<Element> script = scripts.stream().
                filter(element -> CollectionUtils.isNotEmpty(element.getElementsByAttributeValue(TYPE, SCRIPT))).
                findFirst();
        if (script.isPresent()) {
            scriptExecutor.executeJavaScript(script.get().html());
        }
    }

    /**
     * Analyze the HTML Head (It doesn't run the scripts)
     * @param document
     * @return String
     * @throws ScriptException
     */
    private String parseHTMLHead(final Document document) throws ScriptException {
        StringBuilder headResponse = new StringBuilder();
        headResponse.append("<!DOCTYPE html>");
        headResponse.append("<html>");
        headResponse.append("<head>");
        Elements headElements = document.head().select("*").get(0).children();
        for (Element headElement : headElements) {
            if (!SCRIPT_TAG.equals(headElement.tagName())) {
                headResponse.append(evaluateExpression(headElement.toString()));
            }
        }
        headResponse.append("</head>");
        return headResponse.toString();
    }

    /**
     * Analyze the HTML Body
     * @param document
     * @return String
     * @throws ScriptException
     */
    private String parseHTMLBody(final Document document) throws ScriptException {
        StringBuilder bodyResponse = new StringBuilder();
        bodyResponse.append("<body>");
        Elements bodyElements = document.body().select("*").get(0).children();
        for (Element bodyElement : bodyElements) {
            if (CollectionUtils.isNotEmpty(bodyElement.getElementsByAttribute("data-if"))) {
                if (validateDataIf(bodyElement)) {
                    bodyResponse.append(evaluateExpression(bodyElement.toString()));
                }
            } else {
                final String dataForResponse = resolveDataFor(bodyElement);
                if (StringUtils.isEmpty(dataForResponse)) {
                    bodyResponse.append(evaluateExpression(bodyElement.toString()));
                } else if (!EMPTY_LIST.equals(dataForResponse)) {
                    bodyResponse.append(dataForResponse);
                }
            }
        }
        bodyElements.append("</body>");
        bodyElements.append("</html>");
        return bodyResponse.toString();
    }

    /**
     * It validates if the tag has a data-for-* attribute, and process it
     * @param element
     * @return String
     * @throws ScriptException
     */
    private String resolveDataFor(final Element element) throws ScriptException {
        StringBuilder dataForResponse = new StringBuilder();
        Attributes attributes = element.attributes();
        for (Attribute attribute : attributes) {
            String key = attribute.getKey();
            if (key.startsWith(DATA_FOR_ATTRIBUTE)) {
                attributes.remove(attribute.getKey());
                String attributeBinding = key.substring(key.lastIndexOf('-') + 1, key.length());

                List<Object> list = (List<Object>) scriptExecutor.executeJavaScript(attribute.getValue());
                if (CollectionUtils.isEmpty(list)) {
                    return EMPTY_LIST;
                }
                for (Object object : list) {
                    scriptExecutor.sendAttribute(attributeBinding, object);
                    dataForResponse.append(evaluateExpression(element.toString()));
                }
            }
        }
        return dataForResponse.toString();
    }

    /**
     * Validates if a tag has a data-if attribute and returns the validation
     * @param element
     * @return Boolean
     * @throws ScriptException
     */
    private boolean validateDataIf(final Element element) throws ScriptException {
        Stream<Attribute> attributes = element.attributes().asList().stream();
        Optional<Attribute> first = attributes.filter(attribute -> {
            element.attributes().remove(attribute.getKey());
            return attribute.getKey().equals(DATA_IF_ATTRIBUTE);
        }).findFirst();
        return first.isPresent() ? (Boolean) scriptExecutor.executeJavaScript(first.get().getValue()) : false;
    }

    /**
     * Evaluate the expression ${} and execute everything inside
     * @param htmlCode
     * @return String
     * @throws ScriptException
     */
    private String evaluateExpression(final String htmlCode) throws ScriptException {
        // Create a Pattern object
        Pattern pattern = Pattern.compile(EXPRESSION_PATTERN);
        StringBuilder expressionResponse = new StringBuilder();
        expressionResponse.append(htmlCode);
        Matcher matcher = pattern.matcher(htmlCode);
        while (matcher.find()) {
            String script = expressionResponse.toString().substring(matcher.start() + 2, matcher.end() - 1);
            String newValue = escapeHtmlCode(scriptExecutor.executeJavaScript(script).toString());
            expressionResponse.replace(matcher.start(), matcher.end(), newValue);
            matcher = pattern.matcher(expressionResponse.toString());
        }
        return (expressionResponse.toString());
    }

    /**
     * Escapes the html code
     * @param text
     * @return String escaped text
     */
    private String escapeHtmlCode(final String text) {
        return StringEscapeUtils.escapeHtml4(text);
    }
}