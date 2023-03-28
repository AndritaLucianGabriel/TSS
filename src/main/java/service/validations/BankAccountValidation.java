package service.validations;

import service.exceptions.BankAccountException;

public class BankAccountValidation {
    public static void validateIBAN(String IBAN) throws BankAccountException {
        if (!IBAN.matches("^[A-Z]{2}[0-9]{2}[A-Z]{4}[0-9]{16,18}$"))
            throw new BankAccountException("IBAN invalid");
    }

    public static void validateOpeningDate(String openingDate) throws BankAccountException {
        if (!openingDate.matches("^(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\/|-|\\.)(?:0?[13-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:29(\\/|-|\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$"))
            throw new BankAccountException("Data de deschidere invalida");
    }

    //Multimiri Ofir Luzon @StackOverflow :)
    public static void validateClosingDate(String closingDate) throws BankAccountException {
        if (!((closingDate.equals("-")) || closingDate.matches("^(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\/|-|\\.)(?:0?[13-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:29(\\/|-|\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$")))
            throw new BankAccountException("Data de inchidere invalida");
    }

    public static void validateBalance(double balance) throws BankAccountException {
        if (balance < 0)
            throw new BankAccountException("Balanta invalida");
    }

    public static void validateCurrency(String currency) throws BankAccountException {
        if (!currency.matches("^[a-zA-Z]+$"))
            throw new BankAccountException("Currency invalid");
    }

    public static void validateAnnualInterestRate(double annualInterestRate) throws BankAccountException {
        if (annualInterestRate < 0)
            throw new BankAccountException("Interest rate invalid");
    }

}