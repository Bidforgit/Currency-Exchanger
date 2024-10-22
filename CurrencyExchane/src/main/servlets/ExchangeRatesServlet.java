package main.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import main.exceptions.CurrencyNotFoundException;
import main.services.ExchangeRateService;
import main.utils.ErrorResponseUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {

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

        try {
            String json = objectMapper.writeValueAsString(exchangeRateService.getExchangeRateWithCurrencies());
            response.getWriter().write(json);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String baseCurrency = request.getParameter("baseCurrencyCode");
        String targetCurrency = request.getParameter("targetCurrencyCode");
        BigDecimal rate = BigDecimal.valueOf(Long.parseLong(request.getParameter("rate")));

        try {
            String json = objectMapper.writeValueAsString(exchangeRateService.insertExchangeRate(baseCurrency, targetCurrency, rate));
            response.getWriter().write(json);

        } catch (SQLException e) {
            ErrorResponseUtil.sendErrorResponse(response, "Ошибка базы данных: " + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (CurrencyNotFoundException e) {
            ErrorResponseUtil.sendErrorResponse(response, "Валюта не найдена: " + e.getCurrencyCode(), HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            ErrorResponseUtil.sendErrorResponse(response, "Произошла ошибка: " + e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    }
}