package service.dbResources.service;

import mainClasses.*;
import operations.Provider;
import operations.Transaction;
import repository.*;
import service.Timestamp;

import java.util.List;

public class BankService {
    private final BankAccountService bankAccountService = new BankAccountService();
    private final BankRepository bankRepository = new BankRepository();
    private final CardService cardService = new CardService();
    private final ClientService clientService = new ClientService();
    private final LoanService loanService = new LoanService();
    private final ProviderService providerService = new ProviderService();
    private final TransactionService transactionService = new TransactionService();


    private static BankService instance = null;

    private BankService() {
    }

    public static BankService getInstance() {
        if (instance == null)
            instance = new BankService();
        return instance;
    }

    public void create(Object object) {
        Timestamp.timestamp("BankService,create");
        if (object instanceof Bank)
            bankRepository.create((Bank) object);
        else if (object instanceof BankAccount)
            bankAccountService.create((BankAccount) object);
        else if (object instanceof Card)
            cardService.create((Card) object);
        else if (object instanceof Client)
            clientService.create((Client) object);
        else if (object instanceof Provider)
            providerService.create((Provider) object);
        else if (object instanceof Transaction)
            transactionService.create((Transaction) object);
        else throw new RuntimeException("Nu se pot introduce obiecte de tipul " + object.getClass() + " in DB.");
    }

    public void create(List<Object> object) {
        Timestamp.timestamp("BankService,create");
        if (object.get(0) instanceof Bank)
            for (Object bank : object)
                bankRepository.create((Bank) bank);
        else if (object.get(0) instanceof BankAccount)
            for (Object bankAccount : object)
                bankAccountService.create((BankAccount) bankAccount);
        else if (object.get(0) instanceof Card)
            for (Object card : object)
                cardService.create((Card) card);
        else if (object.get(0) instanceof Client)
            for (Object client : object)
                clientService.create((Client) client);
        else if (object.get(0) instanceof Provider)
            for (Object provider : object)
                providerService.create((Provider) provider);
        else throw new RuntimeException("Nu se pot introduce obiecte de tipul " + object.getClass() + " in DB.");
    }

    public void createLoan(Loan loan, String cnp) {
        Timestamp.timestamp("BankService,create");
        loanService.create(loan, cnp);
    }

    public void createLoan(List<Loan> loan, String cnp) {
        for (Loan x : loan)
            loanService.create(x, cnp);
    }

    public List<Object> read(String className) {
        Timestamp.timestamp("BankService,read");
        if (className.equalsIgnoreCase("Bank"))
            return bankRepository.read();
        else if (className.equalsIgnoreCase("BankAccount"))
            return bankAccountService.read();
        else if (className.equalsIgnoreCase("Card"))
            return cardService.read();
        else if (className.equalsIgnoreCase("Client"))
            return clientService.read();
        else if (className.equalsIgnoreCase("Loan"))
            return loanService.read();
        else if (className.equalsIgnoreCase("Provider"))
            return providerService.read();
        else
            throw new RuntimeException("Nu exista obiecte de tipul " + className + " in DB.");
    }

    public Object read(String className, String PK) {
        Timestamp.timestamp("BankService,read");
        if (className.equalsIgnoreCase("BankAccount"))
            return bankAccountService.read(PK);
        else if (className.equalsIgnoreCase("Card"))
            return cardService.read(PK);
        else if (className.equalsIgnoreCase("Client"))
            return clientService.read(PK);
        else if (className.equalsIgnoreCase("Provider"))
            return providerService.read(PK);
        else
            throw new RuntimeException("Nu exista obiecte de tipul " + className + " in DB.");
    }

    public Object read(String className, int PK) {
        Timestamp.timestamp("BankService,read");
        if (className.equalsIgnoreCase("Bank"))
            return bankRepository.read(PK);
        else
            throw new RuntimeException("Nu exista obiecte de tipul " + className + " in DB.");
    }

    public Object readLoan(String cnp, String date) {
        Timestamp.timestamp("BankService,readLoan");
        return loanService.read(cnp, date);
    }

    public void update(String className, String PK, String FK) {
        Timestamp.timestamp("BankService,update");
        if (className.equalsIgnoreCase("BankAccount"))
            bankAccountService.update(PK, FK);
        else if (className.equalsIgnoreCase("Card"))
            cardService.update(PK, FK);
        else if (className.equalsIgnoreCase("Client"))
            clientService.update(PK, FK);
        else throw new RuntimeException("Nu se face poate face transferul");
    }

    public void update(Object object) {
        Timestamp.timestamp("BankService,update");
        if (object instanceof Bank)
            bankRepository.update((Bank) object);
        else if (object instanceof BankAccount)
            bankAccountService.update((BankAccount) object);
        else if (object instanceof Card)
            cardService.update((Card) object);
        else if (object instanceof Client)
            clientService.update((Client) object);
        else if (object instanceof Provider)
            providerService.update((Provider) object);
        else throw new RuntimeException("Nu se face update la " + object.getClass() + " in DB.");
    }

    public void setBalance(double balance, String IBAN) {
        Timestamp.timestamp("BankService,setBalance");
        bankAccountService.setBalance(balance, IBAN);
    }

    public void setCurrency(String currency, String IBAN) {
        Timestamp.timestamp("BankService,setCurrency");
        bankAccountService.setCurrency(currency, IBAN);
    }

    public void updateLoan(Loan loan, String FK) {
        Timestamp.timestamp("BankService,updateLoan");
        loanService.update(loan, FK);
    }

    public void delete() {
        Timestamp.timestamp("BankService,delete");
        bankRepository.delete();
    }

    public void delete(String className, String PK) {
        Timestamp.timestamp("BankService,delete");
        if (className.equalsIgnoreCase("BankAccount"))
            bankAccountService.delete(PK);
        else if (className.equalsIgnoreCase("Bank"))
            bankRepository.delete(PK);
        else if (className.equalsIgnoreCase("Card"))
            cardService.delete(PK);
        else if (className.equalsIgnoreCase("Client"))
            clientService.delete(PK);
        else if (className.equalsIgnoreCase("Provider"))
            providerService.delete(PK);
        else
            throw new RuntimeException("Nu exista obiecte de tipul " + className + " in DB.");
    }

    public void delete(String className, int PK) {
        Timestamp.timestamp("BankService,delete");
        if (className.equalsIgnoreCase("Bank"))
            bankRepository.delete(PK);
        else
            throw new RuntimeException("Nu exista obiecte de tipul " + className + " in DB.");
    }

    public void deleteCheckBankAccount(String cnp) {
        Timestamp.timestamp("BankService,deleteCheckBankAccount");
        clientService.deleteCheckBankAccount(cnp);
    }

    public void deleteCheckLoan(String cnp) {
        Timestamp.timestamp("BankService,deleteCheckLoan");
        clientService.deleteCheckLoan(cnp);
    }


    public void deleteLoan(String cnp, String date) {
        Timestamp.timestamp("BankService,deleteLoan");
        loanService.delete(cnp, date);
    }

    public void deleteAllData() {
        Timestamp.timestamp("BankService,deleteAllData");
        this.delete();
        new ProviderService().delete();
    }
}
