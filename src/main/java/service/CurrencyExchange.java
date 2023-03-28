package service;

import javafx.util.Pair;
import service.dbResources.service.BankService;

import java.util.Objects;

public interface CurrencyExchange {
    String[] currency = {"Lei", "Dolari", "Euro"};
                                //Rates @ 25-03-2021 14:41
                                //Lei->X           //Dolari->X        //Euro->X
    Double[][] exchangeRates = {{1.0, 0.24, 0.20}, {4.14, 1.0, 0.85}, {4.89, 1.18, 1.0}};

    static Integer[] searchByCurrency(String baseCurrency, String wantedCurrency) {
        Timestamp.timestamp("CurrencyExchange,searchByCurrency");
        Integer[] local = {-1, -1, -1};
        for (int i = 0; i < currency.length; i++) {
            if (Objects.equals(currency[i], baseCurrency))
                local[0] = i;
            if (Objects.equals(currency[i], wantedCurrency))
                local[1] = i;
        }
        return local;
    }

    static Pair<Double, String> exchangeBankAccount(double value, String baseCurrency, String wantedCurrency) {
        Timestamp.timestamp("CurrencyExchange,exchangeBankAccount");
        Integer[] local = searchByCurrency(baseCurrency, wantedCurrency);
        if(local[0] != -1 && local[1] != -1) {
            System.out.println("\tExchangeRate: " + FormatDouble.format(exchangeRates[local[0]][local[1]]));
            return new Pair<>(value * exchangeRates[local[0]][local[1]], wantedCurrency);
        }
        return new Pair<>(-1.0, "");
    }

    static double convertTransfer(double value, String baseCurrency, String wantedCurrency) {
        Timestamp.timestamp("CurrencyExchange,convertTransfer");
        Integer[] local = searchByCurrency(baseCurrency, wantedCurrency);
        System.out.println("\tExchangeRate: " + FormatDouble.format(exchangeRates[local[1]][local[0]]));
        return value * exchangeRates[local[1]][local[0]];
    }

    static double convertTransferWithoutText(double value, String baseCurrency, String wantedCurrency) {
        Timestamp.timestamp("CurrencyExchange,convertTransferWithoutText");
        Integer[] local = searchByCurrency(baseCurrency, wantedCurrency);
        return value * exchangeRates[local[1]][local[0]];
    }

}