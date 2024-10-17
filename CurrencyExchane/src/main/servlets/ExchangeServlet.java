package main.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import main.services.CurrencyService;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/exchange/*")
public class ExchangeServlet extends HttpServlet {

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
        String pathInfo = request.getPathInfo();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String baseCurrency = request.getParameter("baseCurrency");
        String targetCurrency = request.getParameter("targetCurrency");
        String amount = request.getParameter("amount");



        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Currency code is missing");
        } else {
            // Extract the currency code from the URL
            String currencyCode = pathInfo.substring(1);  // Remove leading slash
            try {
                String json = objectMapper.writeValueAsString(currencyService.getCurrencyByCode(currencyCode));
                response.getWriter().write(json);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}