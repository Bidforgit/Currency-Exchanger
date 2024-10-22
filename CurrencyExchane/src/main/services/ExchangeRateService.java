package main.services;

import lombok.RequiredArgsConstructor;
import main.DTO.ExchangeRateWithCurrencies;
import main.DatabaseConfig;
import main.exceptions.CurrencyNotFoundException;
import main.exceptions.ExchangeRateAlreadyExistsException;
import main.exceptions.ExchangeRateNotFoundException;
import main.models.Currency;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@RequiredArgsConstructor

public class ExchangeRateService {

    private DatabaseConfig db;
    private final CurrencyService currencyService;

//    private Connection connection;
//
//    public ExchangeRateService(Connection connection) {
//        this.connection = connection;
//    }

    public ExchangeRateService() {
        this.currencyService = new CurrencyService();
    }

    public ExchangeRateWithCurrencies getExchangeRateByCourse(String currencyPair) throws SQLException {

        String baseCurrencyCode = currencyPair.substring(0, 3);  // USD
        String targetCurrencyCode = currencyPair.substring(3, 6);  // KZT

        String sql = "SELECT " +
                "er.id AS exchange_rate_id, " +
                "c1.id AS base_currency_id, c1.code AS base_currency_code, c1.fullName AS base_currency_fullName, c1.sign AS base_currency_sign, " +
                "c2.id AS target_currency_id, c2.code AS target_currency_code, c2.fullName AS target_currency_fullName, c2.sign AS target_currency_sign, " +
                "er.Rate " +
                "FROM ExchangeRates er " +
                "JOIN currencies c1 ON er.baseCurrencyId = c1.id " +
                "JOIN currencies c2 ON er.TargetCurrencyId = c2.id " +
                "WHERE base_currency_code = ? AND target_currency_code = ?";
        ExchangeRateWithCurrencies exchangeRateWithCurrencies = new ExchangeRateWithCurrencies();

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, baseCurrencyCode);
            pstmt.setString(2, targetCurrencyCode);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Base currency (from)
                Currency baseCurrency = new Currency();
                baseCurrency.setId(rs.getInt("base_currency_id"));
                baseCurrency.setCode(rs.getString("base_currency_code"));
                baseCurrency.setFullName(rs.getString("base_currency_fullName"));
                baseCurrency.setSign(rs.getString("base_currency_sign"));

                // Target currency (to)
                Currency targetCurrency = new Currency();
                targetCurrency.setId(rs.getInt("target_currency_id"));
                targetCurrency.setCode(rs.getString("target_currency_code"));
                targetCurrency.setFullName(rs.getString("target_currency_fullName"));
                targetCurrency.setSign(rs.getString("target_currency_sign"));

                // ExchangeRateWithCurrencies contains both currencies
                exchangeRateWithCurrencies.setBaseCurrency(baseCurrency);
                exchangeRateWithCurrencies.setTargetCurrency(targetCurrency);
                exchangeRateWithCurrencies.setRate(rs.getBigDecimal("Rate"));

            }
        }
        return exchangeRateWithCurrencies;
    }

    public List<ExchangeRateWithCurrencies> getExchangeRateWithCurrencies() throws SQLException {
        String sql = "SELECT " +
                "er.id AS exchange_rate_id, " +
                "c1.id AS base_currency_id, c1.code AS base_currency_code, c1.fullName AS base_currency_fullName, c1.sign AS base_currency_sign, " +
                "c2.id AS target_currency_id, c2.code AS target_currency_code, c2.fullName AS target_currency_fullName, c2.sign AS target_currency_sign, " +
                "er.Rate " +
                "FROM ExchangeRates er " +
                "JOIN currencies c1 ON er.baseCurrencyId = c1.id " +
                "JOIN currencies c2 ON er.TargetCurrencyId = c2.id ";

        List<ExchangeRateWithCurrencies> exchangeRateWithCurrenciesList = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                // Base currency (from)
                Currency baseCurrency = new Currency();
                baseCurrency.setId(rs.getInt("base_currency_id"));
                baseCurrency.setCode(rs.getString("base_currency_code"));
                baseCurrency.setFullName(rs.getString("base_currency_fullName"));
                baseCurrency.setSign(rs.getString("base_currency_sign"));

                // Target currency (to)
                Currency targetCurrency = new Currency();
                targetCurrency.setId(rs.getInt("target_currency_id"));
                targetCurrency.setCode(rs.getString("target_currency_code"));
                targetCurrency.setFullName(rs.getString("target_currency_fullName"));
                targetCurrency.setSign(rs.getString("target_currency_sign"));

                // ExchangeRateWithCurrencies contains both currencies
                ExchangeRateWithCurrencies exchangeRateWithCurrencies = new ExchangeRateWithCurrencies();
                exchangeRateWithCurrencies.setBaseCurrency(baseCurrency);
                exchangeRateWithCurrencies.setTargetCurrency(targetCurrency);
                exchangeRateWithCurrencies.setRate(rs.getBigDecimal("Rate"));

                exchangeRateWithCurrenciesList.add(exchangeRateWithCurrencies);
            }
            return exchangeRateWithCurrenciesList;
        }
    }

    public ExchangeRateWithCurrencies insertExchangeRate(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) throws SQLException {
        try (Connection connection = DatabaseConfig.getDataSource().getConnection()) {
            connection.setAutoCommit(false);

            Currency baseCurrency = currencyService.getCurrencyByCode(baseCurrencyCode);
            Currency targetCurrency = currencyService.getCurrencyByCode(targetCurrencyCode);

            if (baseCurrency == null) {
                throw new CurrencyNotFoundException("Base currency not found: " + baseCurrencyCode);
            }

            if (targetCurrency == null) {
                throw new CurrencyNotFoundException("Target currency not found: " + targetCurrencyCode);
            }

            // Check if the exchange rate already exists
            if (exchangeRateExists(baseCurrency.getId(), targetCurrency.getId())) {
                throw new ExchangeRateAlreadyExistsException("Exchange rate for this currency pair already exists");
            }
            ExchangeRateWithCurrencies exchangeRateWithCurrencies = new ExchangeRateWithCurrencies();
            String insertSql = "INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, rate) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(insertSql)) {
                pstmt.setLong(1, baseCurrency.getId());
                pstmt.setLong(2, targetCurrency.getId());
                pstmt.setBigDecimal(3, rate);

                int affectedRows = pstmt.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException("Inserting exchange rate failed, no rows affected.");
                }

                // Retrieve the generated key (e.g., the new ID)
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        exchangeRateWithCurrencies.setId(generatedKeys.getLong(1));
                    } else {
                        throw new SQLException("Inserting exchange rate failed, no ID obtained.");
                    }
                }
            }

            connection.commit();


            exchangeRateWithCurrencies.setRate(rate);
            exchangeRateWithCurrencies.setBaseCurrency(baseCurrency);
            exchangeRateWithCurrencies.setTargetCurrency(targetCurrency);
            return exchangeRateWithCurrencies;
        }
    }

//    private Currency getCurrencyByCode(String currencyCode) throws SQLException {
//        String query = "SELECT * FROM Currencies WHERE code = ?";
//        try (Connection conn = DatabaseConfig.connect();
//             PreparedStatement pstmt = conn.prepareStatement(query)) {
//            pstmt.setString(1, currencyCode);
//            ResultSet rs = pstmt.executeQuery();
//            if (rs.next()) {
//                Currency currency = new Currency();
//                currency.setFullName(rs.getString("FullName"));
//                currency.setCode(rs.getString("Code"));
//                currency.setId(rs.getInt("id"));
//                currency.setSign(rs.getString("Sign"));
//                return currency;
//            }
//        }
//        return null;
//    }

    private Long getExchangeRateId(int baseCurrencyId, int targetCurrencyId) throws SQLException {
        String query = "SELECT id FROM ExchangeRates WHERE baseCurrencyId = ? AND targetCurrencyId = ?";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, baseCurrencyId);
            pstmt.setInt(2, targetCurrencyId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getLong("id");
            } else {
                throw new IllegalArgumentException("Exchange rate not found for update");
            }
        }
    }

    private boolean exchangeRateExists(int baseCurrencyId, int targetCurrencyId) throws SQLException {
        String query = "SELECT 1 FROM ExchangeRates WHERE BaseCurrencyId = ? AND TargetCurrencyId = ?";
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, baseCurrencyId);
            pstmt.setInt(2, targetCurrencyId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        }
    }

    public ExchangeRateWithCurrencies calculateExchangeRates(String baseCurrencyCode, String targetCurrencyCode, BigDecimal amount) throws SQLException {
        String sql = "SELECT " +
                "er.id AS exchange_rate_id, " +
                "c1.id AS base_currency_id, c1.code AS base_currency_code, c1.fullName AS base_currency_fullName, c1.sign AS base_currency_sign, " +
                "c2.id AS target_currency_id, c2.code AS target_currency_code, c2.fullName AS target_currency_fullName, c2.sign AS target_currency_sign, " +
                "er.Rate, 'forward' AS direction " +
                "FROM ExchangeRates er " +
                "JOIN currencies c1 ON er.baseCurrencyId = c1.id " +
                "JOIN currencies c2 ON er.TargetCurrencyId = c2.id " +
                "WHERE c1.code = ? AND c2.code = ? " + // base -> target

                "UNION " +

                "SELECT " +
                "er.id AS exchange_rate_id, " +
                "c2.id AS base_currency_id, c2.code AS base_currency_code, c2.fullName AS base_currency_fullName, c2.sign AS base_currency_sign, " +
                "c1.id AS target_currency_id, c1.code AS target_currency_code, c1.fullName AS target_currency_fullName, c1.sign AS target_currency_sign, " +
                "er.Rate, 'reverse' AS direction " +
                "FROM ExchangeRates er " +
                "JOIN currencies c1 ON er.baseCurrencyId = c1.id " +
                "JOIN currencies c2 ON er.TargetCurrencyId = c2.id " +
                "WHERE c1.code = ? AND c2.code = ?";  // target -> base

        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, baseCurrencyCode);
            pstmt.setString(2, targetCurrencyCode);
            pstmt.setString(3, targetCurrencyCode); // For reverse part of the UNION
            pstmt.setString(4, baseCurrencyCode);   // For reverse part of the UNION

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Base currency (from)
                Currency baseCurr = new Currency();
                baseCurr.setId(rs.getInt("base_currency_id"));
                baseCurr.setCode(rs.getString("base_currency_code"));
                baseCurr.setFullName(rs.getString("base_currency_fullName"));
                baseCurr.setSign(rs.getString("base_currency_sign"));

                // Target currency (to)
                Currency targetCurr = new Currency();
                targetCurr.setId(rs.getInt("target_currency_id"));
                targetCurr.setCode(rs.getString("target_currency_code"));
                targetCurr.setFullName(rs.getString("target_currency_fullName"));
                targetCurr.setSign(rs.getString("target_currency_sign"));


                ExchangeRateWithCurrencies exchangeRateWithCurrencies = new ExchangeRateWithCurrencies();

                String direction = rs.getString("direction");
                if ("reverse".equals(direction)) {
                    exchangeRateWithCurrencies.setRate(BigDecimal.valueOf(1).divide(rs.getBigDecimal("Rate")));
                } else {
                    exchangeRateWithCurrencies.setRate(rs.getBigDecimal("Rate"));
                }
//                    exchangeRateWithCurrencies.setRate(
//                            BigDecimal.valueOf(1)
//                                    .divide(rs.getBigDecimal("Rate"), 2, RoundingMode.HALF_UP) // 6 is the scale, adjust as needed
//                    );

                exchangeRateWithCurrencies.setBaseCurrency(baseCurr);
                exchangeRateWithCurrencies.setTargetCurrency(targetCurr);
                exchangeRateWithCurrencies.setConvertedAmount(exchangeRateWithCurrencies.getRate().multiply(amount));


                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        exchangeRateWithCurrencies.setId(generatedKeys.getLong(1));
                    }
                }

                return exchangeRateWithCurrencies;
            }
        }

        return null;

    }

    public ExchangeRateWithCurrencies updateCurrencyRate(String pathInfo, BigDecimal rate) throws SQLException {

        String baseCurrencyCode = pathInfo.substring(0, 3);  // USD
        String targetCurrencyCode = pathInfo.substring(3, 6);  // KZT

        try (Connection connection = DatabaseConfig.getDataSource().getConnection()) {
            connection.setAutoCommit(false);

            Currency baseCurrency = currencyService.getCurrencyByCode(baseCurrencyCode);
            Currency targetCurrency = currencyService.getCurrencyByCode(targetCurrencyCode);

            if (baseCurrency == null) {
                throw new SQLException("Base currency not found: " + baseCurrencyCode);
            }

            if (targetCurrency == null) {
                throw new SQLException("Target currency not found: " + targetCurrencyCode);
            }

            Long exchangeRateId = getExchangeRateId(baseCurrency.getId(), targetCurrency.getId());

            // Perform the UPDATE since the exchange rate exists
            String updateSql = "UPDATE ExchangeRates SET rate = ? WHERE id = ?";
            try (PreparedStatement updatePstmt = connection.prepareStatement(updateSql)) {
                updatePstmt.setBigDecimal(1, rate);
                updatePstmt.setLong(2, exchangeRateId);  // Use the ID to update the correct row

                int affectedRows = updatePstmt.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException("Updating exchange rate failed, no rows affected.");
                }

                connection.commit();

                // Set fields and return the ExchangeRateWithCurrencies object
                ExchangeRateWithCurrencies exchangeRateWithCurrencies = new ExchangeRateWithCurrencies();
                exchangeRateWithCurrencies.setId(exchangeRateId);  // Now we have the ID of the updated row
                exchangeRateWithCurrencies.setRate(rate);
                exchangeRateWithCurrencies.setBaseCurrency(baseCurrency);
                exchangeRateWithCurrencies.setTargetCurrency(targetCurrency);

                return exchangeRateWithCurrencies;

            } catch (SQLException e) {
                connection.rollback();
                throw e;  // Re-throw the exception
            }
        }
    }
}
