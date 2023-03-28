package service.validations;

import service.exceptions.ProviderException;

public class ProviderValidation {
    public static void validateCompany(String name) throws ProviderException {
        if (!name.matches("^[^-\\s\\d][a-zA-Z0-9\\s-.]+$"))
            throw new ProviderException("Nume companie invalid");
    }

    public static void validateIBAN(String IBAN) throws ProviderException {
        if (!IBAN.matches("^[A-Z]{2}[0-9]{2}[A-Z]{4}[0-9]{16,18}$"))
            throw new ProviderException("IBAN invalid");
    }

    public static void validateBalance(double balance) throws ProviderException {
        if (balance < 0)
            throw new ProviderException("Balanta invalida");
    }

    public static void validateCurrency(String currency) throws ProviderException {
        if (!currency.matches("^[a-zA-Z]+$"))
            throw new ProviderException("Currency invalid");
    }
}
