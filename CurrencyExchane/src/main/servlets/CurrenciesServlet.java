package main.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import main.services.CurrencyService;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet("/currencies")  // Handle requests for /currency/{code}
public class CurrenciesServlet extends HttpServlet {

    private CurrencyService currencyService;
    private ObjectMapper objectMapper;


    @Override
    public void init() throws ServletException {
        super.init();
        currencyService = new CurrencyService();
        objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String json = objectMapper.writeValueAsString(currencyService.getAllCurrencies());
            response.getWriter().write(json);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Currency code is missing");
        } else {
            try {
                String json = objectMapper.writeValueAsString(currencyService.getAllCurrencies());
                response.getWriter().write(json);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}