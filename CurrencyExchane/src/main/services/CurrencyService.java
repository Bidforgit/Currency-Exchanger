package main.services;

import main.DatabaseConfig;
import main.models.Currency;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CurrencyService {

    private DatabaseConfig db;

    public Currency insertCurrency(String code, String fullName, String sign) throws SQLException {
        String sql = "INSERT INTO currencies(code, fullName, sign) VALUES(?,?,?) RETURNING * ";

        try (Connection conn = DatabaseConfig.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, code);
            pstmt.setString(2, fullName);
            pstmt.setString(3, sign);

            ResultSet rs = pstmt.executeQuery();
            Currency currency = new Currency();
            currency.setId(rs.getInt("id"));
            currency.setCode(rs.getString("code"));
            currency.setFullName(rs.getString("fullName"));
            currency.setSign(rs.getString("sign"));
            return currency;

        }
    }

    public Currency getCurrencyByCode(String currencyCode) throws SQLException {
        String sql = "SELECT id, code, fullName, sign FROM currencies WHERE code = ?";

        try (Connection conn = DatabaseConfig.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, currencyCode);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Currency currency = new Currency();
                    currency.setId(rs.getInt("id"));
                    currency.setCode(rs.getString("code"));
                    currency.setFullName(rs.getString("fullName"));
                    currency.setSign(rs.getString("sign"));
                    return currency;
                } else {
                    // Currency not found
                    return null; // Or throw an exception if preferred
                }
            }
        }
    }

    public List<Currency> getAllCurrencies() throws SQLException {
        List<Currency> currencies = new ArrayList<>();
        String sql = "SELECT id, code, fullName, sign FROM currencies";

        try (Connection conn = DatabaseConfig.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            ResultSet rs = pstmt.executeQuery();

                while(rs.next()){
                    Currency currency = new Currency();
                    currency.setId(rs.getInt("id"));
                    currency.setCode(rs.getString("code"));
                    currency.setFullName(rs.getString("fullName"));
                    currency.setSign(rs.getString("sign"));
                    currencies.add(currency);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        return currencies;
    }

}
