package main.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import main.services.CurrencyService;
import main.utils.ErrorResponseUtil;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/currencies")
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

        try {
            String json = objectMapper.writeValueAsString(currencyService.getAllCurrencies());
            response.getWriter().write(json);
        } catch (SQLException e) {
            ErrorResponseUtil.sendErrorResponse(response, "Валют не существует.", HttpServletResponse.SC_BAD_REQUEST);
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String name = request.getParameter("name");
        String code = request.getParameter("code");
        String sign = request.getParameter("sign");

        try {
            String json = objectMapper.writeValueAsString(currencyService.insertCurrency(code, name, sign));
            response.getWriter().write(json);

        } catch ( SQLException e) {
            ErrorResponseUtil.sendErrorResponse(response, "Валюта с кодом '" + code + "' уже существует.", HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}