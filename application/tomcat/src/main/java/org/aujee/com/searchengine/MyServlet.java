package org.aujee.com.searchengine;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(description = "hi", name = "SearchServlet", urlPatterns = {"/api/get"})
public class MyServlet extends HttpServlet {

    public MyServlet() {
    }

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        PrintWriter writer = resp.getWriter();

        writer.println("<html><title>Wal Sie</title><body>");
        writer.println("<h1>See You later alligator!</h1>");
        writer.println("</body></html>");
    }
}
