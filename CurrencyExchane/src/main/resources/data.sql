INSERT INTO currencies (ID, Code, FullName, Sign) VALUES
                                                      (1, 'AUD', 'Australian dollar', 'A$'),
                                                      (2, 'USD', 'United States dollar', '$'),
                                                      (3, 'JPY', 'Japanese yen', '¥'),
                                                      (4, 'EUR', 'Euro', '€'),
                                                      (5, 'INR', 'Indian rupee', '₹'),
                                                      (6, 'RUB', 'Russian ruble', '₽'),
                                                      (7, 'CNY', 'Chinese yuan', '¥'),
                                                      (8, 'BTC', 'Bitcoin', '₿'),
                                                      (9, 'IQD', 'Iraqi dinar', NULL),  -- Без символа
                                                      (10, 'XAF', 'Central African CFA franc', NULL);  -- Без символа


-- Вставка данных в таблицу ExchangeRates
INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate) VALUES
                                                                       (1, 2, 0.640000),   -- 1 AUD = 0.64 USD
                                                                       (2, 3, 106.250000), -- 1 USD = 106.25 JPY
                                                                       (4, 2, 1.070000),   -- 1 EUR = 1.07 USD
                                                                       (6, 2, 0.012000),   -- 1 RUB = 0.012 USD
                                                                       (8, 2, 68000.000000),-- 1 BTC = 68000 USD
                                                                       (11, 2, 0.002100),  -- 1 KZT = 0.0021 USD
                                                                       (12, 2, 0.027000);  -- 1 UAH = 0.027 USD


-- Вставка данных с участием тенге
INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate) VALUES
                                                                       (11, 2, 0.002100),    -- 1 KZT = 0.0021 USD (уже было)
                                                                       (2, 11, 476.190000),  -- 1 USD = 476.19 KZT
                                                                       (4, 11, 508.470000),  -- 1 EUR = 508.47 KZT
                                                                       (6, 11, 5.490000),    -- 1 RUB = 5.49 KZT
                                                                       (8, 11, 32380952.000000);  -- 1 BTC = 32,380,952 KZT
