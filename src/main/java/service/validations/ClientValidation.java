package service.validations;

import service.exceptions.ClientException;

public class ClientValidation {

    public static void validateFirstName(String firstName) throws ClientException {
        if (!firstName.matches("^[a-zA-Z]+$"))
            throw new ClientException("First name invalid");
    }

    public static void validateLastName(String lastName) throws ClientException {
        if (!lastName.matches("^[a-zA-Z- ]+$"))
            throw new ClientException("Last name invalid");
    }

    //o sa am doar clienti majori
    public static void validateAge(int age) throws ClientException {
        if (age <= 18 || age >= 200)
            throw new ClientException("Varsta invalida");
    }

    public static void validateCnp(String cnp) throws ClientException {
        if (!cnp.matches("^[1-9]\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])(0[1-9]|[1-4]\\d|5[0-2]|99)(00[1-9]|0[1-9]\\d|[1-9]\\d\\d)\\d$"))
            throw new ClientException("CNP invalid");
    }
}