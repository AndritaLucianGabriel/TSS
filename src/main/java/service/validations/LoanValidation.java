package service.validations;

import service.exceptions.LoanException;

public class LoanValidation {
    public static void validateValue(double value) throws LoanException {
        if (value < 0)
            throw new LoanException("Valoare invalida");
    }

    public static void validateCurrency(String currency) throws LoanException {
        if (!currency.matches("^[a-zA-Z]+$"))
            throw new LoanException("Currency invalid");
    }

    public static void validateDate(String date) throws LoanException {
        if (!date.matches("^(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\/|-|\\.)(?:0?[13-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:29(\\/|-|\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$"))
            throw new LoanException("Data invalida");
    }

    public static void validateDurationMonths(int durationMonths) throws LoanException {
        if (durationMonths < 0)
            throw new LoanException("Durata invalida");
    }
}
