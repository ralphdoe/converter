package biz.netcentric.service;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * HttpContext Class to encapsule the httpServlet objects
 * @author Rafael Lopez
 */
public class HttpContext {
    private final HttpServletRequest httpServletRequest;
    private final HttpServletResponse httpServletResponse;
    private final ServletContext servletContext;

    public HttpContext(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse,
                       ServletContext servletContext) {
        this.httpServletRequest = httpServletRequest;
        this.httpServletResponse = httpServletResponse;
        this.servletContext = servletContext;
    }

    public HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }

    public HttpServletResponse getHttpServletResponse() {
        return httpServletResponse;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }
}
