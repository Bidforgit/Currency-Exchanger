package main.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import main.services.CurrencyService;
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
//        String pathInfo = request.getPathInfo();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String baseCurrencyCode = request.getParameter("baseCurrency");
        String targetCurrencyCode = request.getParameter("targetCurrency");
        String amountStr = request.getParameter("amount");



//        if (pathInfo == null || pathInfo.equals("/")) {
//            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Currency code is missing");
//        } else {
            try {
                BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(amountStr));

                String json = objectMapper.writeValueAsString(exchangeRateService.calculateExchangeRates(baseCurrencyCode, targetCurrencyCode, amount));
                response.getWriter().write(json);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
//        }
    }
}