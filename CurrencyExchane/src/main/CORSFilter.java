package main;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@WebFilter("/*")
public class CORSFilter implements Filter {
    private static final List<String> ALLOWED_ORIGINS = Arrays.asList(
            "http://localhost:3000",
            "https://yourdomain.com"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String origin = ((jakarta.servlet.http.HttpServletRequest) request).getHeader("Origin");
        HttpServletResponse httpResp = (HttpServletResponse) response;

        if (ALLOWED_ORIGINS.contains(origin)) {
            httpResp.setHeader("Access-Control-Allow-Origin", origin);
            httpResp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            httpResp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
            httpResp.setHeader("Access-Control-Allow-Credentials", "true"); // If needed
        }

        // Handle preflight requests
        if ("OPTIONS".equalsIgnoreCase(((jakarta.servlet.http.HttpServletRequest) request).getMethod())) {
            httpResp.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(request, response);
    }
}
