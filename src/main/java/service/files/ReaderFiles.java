package service.files;

import mainClasses.*;
import operations.Provider;
import operations.ToProviders;
import operations.Transaction;
import operations.Transfer;
import service.exceptions.*;
import service.Timestamp;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

import static service.files.WriterFiles.RESOURCES_FOLDER_MIX;

public class ReaderFiles {
    public static final String RESOURCES_FOLDER = System.getProperty("user.dir") + "\\src\\main\\java\\service\\files\\resources\\readers";
    public static BufferedReader cardReader;
    public static BufferedReader clientReader;
    public static BufferedReader bankAccountReader;
    public static BufferedReader loanReader;
    public static BufferedReader providerDBReader;
    public static BufferedReader bankReader;



    private static ReaderFiles instance = null;
    private ReaderFiles() {
    }
    public static ReaderFiles getInstance() {
        if (instance == null)
            instance = new ReaderFiles();
        return instance;
    }

    public Bank readerBank() {
        try {
            Timestamp.timestamp("ReaderFiles,readerBank");
            ReaderFiles.getInstance().readerProviderDB();
            BankAccount.setCounterBankAccountID(0);
            Bank.setCounterBankID(0);
            Loan.setCounterLoanID(0);
            String linie;
            String[] dummy;
            Bank local;
            bankReader = new BufferedReader(new FileReader(RESOURCES_FOLDER + "\\Bank.csv"));
            linie = bankReader.readLine();
            dummy = linie.split("[,]+");
            List<Client> clientList = ReaderFiles.getInstance().readerClient();
            Map<Client, List<BankAccount>> clientBankAccountMap = ReaderFiles.getInstance().readerBankAccount(Objects.requireNonNull(clientList));
            Map<Client, List<Loan>> clientLoanMap = ReaderFiles.getInstance().readerLoan(Objects.requireNonNull(clientList));

            Bank.setCounterBankID(Bank.getCounterBankID() - 1);
            local = new Bank(dummy[0], dummy[1], clientBankAccountMap, clientLoanMap);

            bankReader.close();
            return local;
        } catch (IOException e) {
            System.out.println("Eroare la citirea din fisierul BankAccount.csv.");
            return null;
        } catch (BankException e) {
            System.out.println("Eroare la prelucrarea din fisierul Bank.txt.");
            return null;
        }
    }

    public Map<Client, List<BankAccount>> readerBankAccount(List<Client> clientList) {
        try {
            Timestamp.timestamp("ReaderFiles,readerBankAccount");
            String linie;
            String[] dummy;
            Map<Client, List<BankAccount>> clientBankAccountMap = new HashMap<>();
            BankAccount local;
            Client CNP = new Client();
            for (Client x : clientList) {
                bankAccountReader = new BufferedReader(new FileReader(RESOURCES_FOLDER + "\\BankAccount.csv"));
                List<BankAccount> bankAccountList = new ArrayList<>();
                while ((linie = bankAccountReader.readLine()) != null) {
                    dummy = linie.split("[,]+");
                    CNP.setCnp(dummy[0]);
                    if (x.getCnp().equals(CNP.getCnp())) {
                        if (dummy.length == 7) {
                            local = new SavingsAccount();
                            local.setIBAN(dummy[1]);
                            local.setCardList(readerCard(local.getIBAN()));
                            local.setOpeningDate(dummy[2]);
                            local.setClosingDate(dummy[3]);
                            local.setBalance(Double.parseDouble(dummy[4]));
                            local.setCurrency(dummy[5]);
                            local.setAnnualInterestRate(Double.parseDouble(dummy[6]));
                            bankAccountList.add(local);
                        } else {
                            local = new DebitAccount();
                            local.setIBAN(dummy[1]);
                            local.setCardList(readerCard(local.getIBAN()));
                            local.setOpeningDate(dummy[2]);
                            local.setClosingDate(dummy[3]);
                            local.setBalance(Double.parseDouble(dummy[4]));
                            local.setCurrency(dummy[5]);
                            bankAccountList.add(local);
                        }

                    }
                }
                bankAccountReader.close();
                clientBankAccountMap.put(x, bankAccountList);
            }
            return clientBankAccountMap;
        } catch (IOException e) {
            System.out.println("Eroare la citirea din fisierul BankAccount.csv.");
            return null;
        } catch (BankAccountException e) {
            System.out.println("Eroare la prelucrarea din fisierul BankAccount.csv.");
            return null;
        } catch (ClientException e) {
            System.out.println("Eroare la introducerea CNP-ului.");
            return null;
        }
    }

    public List<Card> readerCard(String IBAN) {
        try {
            Timestamp.timestamp("ReaderFiles,readerCard");
            cardReader = new BufferedReader(new FileReader(RESOURCES_FOLDER + "\\Card.csv"));
            String linie;
            String[] dummy;
            List<Card> cardList = new ArrayList<>();
            Card local;
            while ((linie = cardReader.readLine()) != null) {
                local = new Card();
                dummy = linie.split("[,]+");
                if (dummy[0].equals(IBAN)) {
                    local.setCardNumber(dummy[1]);
                    local.setPIN(Integer.parseInt(dummy[2]));
                    local.setIssueDate(dummy[3]);
                    cardList.add(local);
                }
            }
            cardReader.close();
            return cardList;
        } catch (IOException e) {
            System.out.println("Eroare la citirea din fisierul Card.csv.");
            return null;
        } catch (CardException e) {
            System.out.println("Eroare la prelucrarea de informatii din fisierul Card.csv.");
            return null;
        }
    }

    public List<Client> readerClient() {
        try {
            Timestamp.timestamp("ReaderFiles,readerClient");
            clientReader = new BufferedReader(new FileReader(RESOURCES_FOLDER + "\\Client.csv"));
            String linie;
            String[] dummy;
            List<Client> clientList = new ArrayList<>();
            while ((linie = clientReader.readLine()) != null) {
                Client local = new Client();
                dummy = linie.split("[,]+");
                local.setFirstName(dummy[0]);
                local.setLastName(dummy[1]);
                local.setAge(Integer.parseInt(dummy[2]));
                local.setCnp(dummy[3]);
                clientList.add(local);
            }
            clientReader.close();
            return clientList;
        } catch (IOException e) {
            System.out.println("Eroare la citirea din fisierul Client.csv.");
            return null;
        } catch (ClientException e) {
            System.out.println("Eroare la prelucrarea de informatii din fisierul Client.csv.");
            return null;
        }
    }

    public Map<Client, List<Loan>> readerLoan(List<Client> clientList) {
        try {
            Timestamp.timestamp("ReaderFiles,readerLoan");
            String linie;
            String[] dummy;
            Map<Client, List<Loan>> clientLoanMap = new HashMap<>();
            Loan local;
            Client CNP = new Client();
            for (Client x : clientList) {
                loanReader = new BufferedReader(new FileReader(RESOURCES_FOLDER + "\\Loan.csv"));
                List<Loan> loanList = new ArrayList<>();
                while ((linie = loanReader.readLine()) != null) {
                    dummy = linie.split("[,]+");
                    CNP.setCnp(dummy[0]);
                    if (x.getCnp().equals(CNP.getCnp())) {
                        local = new Loan();
                        local.setValue(Double.parseDouble(dummy[1]));
                        local.setCurrency(dummy[2]);
                        local.setDetail(dummy[3]);
                        local.setDate(dummy[4]);
                        local.setDurationMonths(Integer.parseInt(dummy[5]));
                        loanList.add(local);
                    }
                }
                loanReader.close();
                clientLoanMap.put(x, loanList);
            }
            return clientLoanMap;
        } catch (IOException e) {
            System.out.println("Eroare la citirea din fisierul Loan.csv.");
            return null;
        } catch (LoanException e) {
            System.out.println("Eroare la prelucrarea din fisierul Loan.csv.");
            return null;
        } catch (ClientException e) {
            System.out.println("Eroare la introducerea CNP-ului.");
            return null;
        }
    }

    public void readerProviderDB() {
        try {
            Timestamp.timestamp("ReaderFiles,readerProviderDB");
            providerDBReader = new BufferedReader(new FileReader(RESOURCES_FOLDER + "\\Provider.csv"));
            String linie;
            String[] dummy;
            Provider local;
            List<Provider> providerList = new ArrayList<>();
            while ((linie = providerDBReader.readLine()) != null) {
                local = new Provider();
                dummy = linie.split("[,]+");
                local.setCompany(dummy[0]);
                local.setIBAN(dummy[1]);
                local.setBalance(Double.parseDouble(dummy[2]));
                local.setCurrency(dummy[3]);
                providerList.add(local);
            }
            ToProviders.addProvider(providerList);
        } catch (IOException e) {
            System.out.println("Eroare la citirea din fisierul Provider.csv.");
        } catch (ProviderException e) {
            System.out.println("Eroare la prelucararea informatiilor din Provider.csv");
        }
    }

    public List<Transaction> readerAccountStatement(String IBAN) {
        try {
            Timestamp.timestamp("ReaderFiles,readerAccountStatement");
            BufferedReader accountStatementTempReader = new BufferedReader(new FileReader(RESOURCES_FOLDER_MIX + "\\" + IBAN + ".csv"));
            String linie;
            String[] dummy;
            List<Transaction> transactionList = new ArrayList<>();
            while ((linie = accountStatementTempReader.readLine()) != null) {
                dummy = linie.split("[, ]+");
                Transaction local = new Transfer();
                local.setTransactionID(dummy[0]);
                local.setTimestamp(LocalDate.parse(dummy[1]));
                local.setValue(Double.parseDouble(dummy[2]));
                local.setCurrency(dummy[3]);
                transactionList.add(local);
            }
            transactionList.sort(Transaction::compareTo);
            accountStatementTempReader.close();
            return transactionList;
        } catch (IOException | TransactionException e) {
            System.out.println("Eroare la citirea din fisierul extras " + IBAN + ".txt");
            return null;
        }
    }

    public void updateReaders(Bank bank) {
        try {
            Timestamp.timestamp("ReaderFiles,updateReaders");
            BufferedWriter bankWriter = new BufferedWriter(new FileWriter(RESOURCES_FOLDER + "\\Bank.csv"));
            BufferedWriter bankAccountWriter = new BufferedWriter(new FileWriter(RESOURCES_FOLDER + "\\BankAccount.csv"));
            BufferedWriter cardWriter = new BufferedWriter(new FileWriter(RESOURCES_FOLDER + "\\Card.csv"));
            BufferedWriter clientWriter = new BufferedWriter(new FileWriter(RESOURCES_FOLDER + "\\Client.csv"));
            BufferedWriter loanWriter = new BufferedWriter(new FileWriter(RESOURCES_FOLDER + "\\Loan.csv"));
            BufferedWriter providerDBWriter = new BufferedWriter(new FileWriter(RESOURCES_FOLDER + "\\Provider.csv"));
            bankWriter.write(bank.bankReaderUpdate());
            for (String x : bank.bankAccountReaderUpdate())
                bankAccountWriter.write(x);
            for (String x : bank.cardReaderUpdate())
                cardWriter.write(x);
            for (String x : bank.clientReaderUpdate())
                clientWriter.write(x);
            for (String x : ToProviders.toProvidersReaderUpdate())
                providerDBWriter.write(x);
            for (String x : bank.loanReaderUpdate())
                loanWriter.write(x);
            bankWriter.close();
            bankAccountWriter.close();
            cardWriter.close();
            clientWriter.close();
            loanWriter.close();
            providerDBWriter.close();

        } catch (IOException e) {
            System.out.println("Eroare la update in cadrul fisierelor de citire!");
        }
    }
}