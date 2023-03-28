package service.validations;

import service.exceptions.TransactionException;

public class TransactionValidation {

    public static void validateIBAN(String IBAN) throws TransactionException {
        if (!IBAN.matches("^[A-Z]{2}[0-9]{2}[A-Z]{4}[0-9]{16,18}$"))
            throw new TransactionException("IBAN invalid");
    }

    public static void validateValue(double balance) throws TransactionException {
        if (balance < 0)
            throw new TransactionException("Balanta invalida");
    }

    public static void validateCurrency(String currency) throws TransactionException {
        if (!currency.matches("^[a-zA-Z]+$"))
            throw new TransactionException("Currency invalid");
    }
}
