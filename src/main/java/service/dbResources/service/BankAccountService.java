package service.dbResources.service;

import mainClasses.BankAccount;
import repository.BankAccountRepository;
import service.Timestamp;

import java.util.List;

public class BankAccountService {
    private final BankAccountRepository bankAccountRepository = new BankAccountRepository();

    protected void create(BankAccount bankAccount) {
        Timestamp.timestamp("BankAccountService,create");
        bankAccountRepository.create(bankAccount);
    }

    protected void create(BankAccount bankAccount, String cnp) {
        Timestamp.timestamp("BankAccountService,create");
        bankAccountRepository.create(bankAccount, cnp);
    }

    protected List<Object> read() {
        Timestamp.timestamp("BankAccountService,read");
        return bankAccountRepository.read();
    }

    protected BankAccount read(String IBAN) {
        Timestamp.timestamp("BankAccountService,read");
        return bankAccountRepository.read(IBAN);
    }

    protected void update(BankAccount bankAccount) {
        Timestamp.timestamp("BankAccountService,update");
        bankAccountRepository.update(bankAccount);
    }

    protected void update(String IBAN, String cnp) {
        Timestamp.timestamp("BankAccountService,update");
        bankAccountRepository.update(IBAN, cnp);
    }

    protected void setBalance(double balance, String IBAN) {
        Timestamp.timestamp("BankAccountService,setBalance");
        bankAccountRepository.setBalance(balance, IBAN);
    }

    protected void setCurrency(String currency, String IBAN) {
        Timestamp.timestamp("BankAccountService,setCurrency");
        bankAccountRepository.setCurrency(currency, IBAN);
    }

    protected void delete() {
        Timestamp.timestamp("BankAccountService,delete");
        bankAccountRepository.delete();
    }

    protected void delete(String IBAN) {
        Timestamp.timestamp("BankAccountService,delete");
        bankAccountRepository.delete(IBAN);
    }
}
