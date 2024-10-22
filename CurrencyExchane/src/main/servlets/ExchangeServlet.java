package main.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import main.services.ExchangeRateService;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

@WebServlet("/exchange/*")
public class ExchangeServlet extends HttpServlet {

    private ExchangeRateService exchangeRateService;
    private ObjectMapper objectMapper;


    @Override
    public void init() throws ServletException {
        super.init();
        exchangeRateService = new ExchangeRateService();
        objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String baseCurrencyCode = request.getParameter("from");
        String targetCurrencyCode = request.getParameter("to");
        String amountStr = request.getParameter("amount");

        try {
            BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(amountStr));

            String json = objectMapper.writeValueAsString(exchangeRateService.calculateExchangeRates(baseCurrencyCode, targetCurrencyCode, amount));
            response.getWriter().write(json);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}