package biz.netcentric.service.impl;

import biz.netcentric.exceptions.SlightlyException;
import biz.netcentric.parser.HTMLParser;
import biz.netcentric.service.HttpContext;
import biz.netcentric.service.ParserService;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * The Service Implementation
 * @author Rafael López
 */
public class ParseServiceImpl implements ParserService {
    private static final Logger LOGGER = Logger.getLogger(ParseServiceImpl.class);
    private static final String HTML_FORMAT = "html";

    @Override
    public void parse(final HttpContext httpContext) {
        try {
            if (httpContext.getHttpServletRequest().getRequestURI().endsWith(HTML_FORMAT)) {
                File file = getFileFromRequest(httpContext);
                HTMLParser htmlParser = new HTMLParser();
                String htmlResponse = htmlParser.parseHTML(httpContext.getHttpServletRequest(), file);
                PrintWriter printWriter = httpContext.getHttpServletResponse().getWriter();
                printWriter.print(htmlResponse);
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            throw new SlightlyException(e.getMessage(), e);
        }
    }

    /**
     * Extract the file from the request
     *
     * @param httpContext
     * @return File
     * @throws MalformedURLException
     */
    private File getFileFromRequest(final HttpContext httpContext) throws MalformedURLException {
        String uri = httpContext.getHttpServletRequest().getRequestURI();
        URL url = httpContext.getServletContext().getResource(uri);
        return new File(url.getFile());
    }

}
