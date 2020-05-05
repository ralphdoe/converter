package biz.netcentric.servlet;

import biz.netcentric.service.HttpContext;
import biz.netcentric.service.ParserService;
import biz.netcentric.service.impl.ParseServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles the request and calls the ParserService
 * @author Rafael Lopez
 */
public class HTMLParserServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        ParserService parseService = new ParseServiceImpl();
        parseService.parse(new HttpContext(request, response, getServletContext()));
    }
}
