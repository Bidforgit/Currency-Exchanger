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

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {

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
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (request.getMethod().equalsIgnoreCase("PATCH")) {
            // Handle PATCH request
            doPatch(request, response);
        } else {
            // Delegate to super class for other HTTP methods (GET, POST, etc.)
            super.service(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Currency code is missing");
        } else {
            String currencyPair = pathInfo.substring(1);
            try {
                String json = objectMapper.writeValueAsString(exchangeRateService.getExchangeRateByCourse(currencyPair));
                response.getWriter().write(json);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String pathInfo = req.getPathInfo();
        String rateStr = req.getParameter("rate");

        if (rateStr == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Missing rate");
            return;
        }
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Currency code is missing");
        } else {
            try {
                pathInfo = pathInfo.substring(1);  // Remove leading slash

                BigDecimal rate = new BigDecimal(rateStr);


                String json = objectMapper.writeValueAsString(exchangeRateService.updateCurrencyRate(pathInfo, rate));
                resp.getWriter().write(json);

//            if (success) {
//                resp.setStatus(HttpServletResponse.SC_OK);
//                resp.getWriter().write("Currency pair updated successfully");
//            } else {
//                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
//                resp.getWriter().write("Currency pair not found");
//            }
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("Invalid rate format");
            } catch (SQLException e) {
                e.printStackTrace();
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("Failed to update currency pair");
            }

        }
    }
}