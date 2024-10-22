package main.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import main.exceptions.CurrencyAlreadyExistsException;
import main.exceptions.CurrencyNotFoundException;
import main.services.CurrencyService;
import main.utils.ErrorResponseUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet("/currency/*")  // Handle requests for /currency/{code}
public class CurrencyServlet extends HttpServlet {

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

        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Currency code is missing");
        } else {

            String currencyCode = pathInfo.substring(1);  // Remove leading slash
            try {
                String json = objectMapper.writeValueAsString(currencyService.getCurrencyByCode(currencyCode));
                response.getWriter().write(json);
            } catch (CurrencyNotFoundException e) {
                ErrorResponseUtil.sendErrorResponse(response, "Валют не существует.", HttpServletResponse.SC_BAD_REQUEST);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}