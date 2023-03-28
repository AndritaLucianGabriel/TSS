package service.validations;

import service.exceptions.BankException;

public class BankValidation {
    public static void validateName(String name) throws BankException {
        if (!name.matches("^[^-\\s\\d][a-zA-Z0-9\\s-]+$"))
            throw new BankException("Nume banca invalid");
    }
    public static void validateSign(String sign) throws BankException{
        if(!sign.matches("^(<=)||(>=)||(<>)||(><)||(><=)||(>=<)||(>=<=)||[<>=]$"))
            throw new BankException("Semn al filtrarii invalid");
    }
}
