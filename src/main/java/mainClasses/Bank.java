package mainClasses;

import operations.Provider;
import org.jetbrains.annotations.NotNull;
import service.CurrencyExchange;
import operations.ToProviders;
import service.AccountStatement;
import service.dbResources.service.BankService;
import service.exceptions.*;
import service.Timestamp;
import service.files.WriterFiles;
import service.FormatDouble;
import service.validations.BankAccountValidation;
import service.validations.BankValidation;

import java.util.*;

public class Bank implements AccountStatement {
    private static int counterBankID;
    protected int bankID;
    protected String name;
    protected String location;
    protected Map<Client, List<BankAccount>> clientBankAccountMap;
    protected Map<Client, List<Loan>> clientLoanMap;

    public Bank() {
        counterBankID++;
        this.bankID = counterBankID;
        this.name = "";
        this.location = "";
        this.clientBankAccountMap = new HashMap<>();
        this.clientLoanMap = new HashMap<>();
    }

    public Bank(Bank copy) {
        counterBankID++;
        this.bankID = counterBankID;
        this.name = copy.name;
        this.location = copy.location;
        this.clientBankAccountMap = copy.clientBankAccountMap;
        this.clientLoanMap = copy.clientLoanMap;
    }

    public Bank(String name, String location, Map<Client, List<BankAccount>> clientBankAccountMap, Map<Client, List<Loan>> clientLoanMap) throws BankException {
        BankValidation.validateName(name);
        counterBankID++;
        this.bankID = counterBankID;
        this.name = name;
        this.location = location;

        this.clientBankAccountMap = clientBankAccountMap;
        for (Map.Entry<Client, List<BankAccount>> x : clientBankAccountMap.entrySet()) {
            normalizeBankIndex(x.getKey());
        }

        this.clientLoanMap = clientLoanMap;
        for (Map.Entry<Client, List<Loan>> x : this.clientLoanMap.entrySet()) {
            normalizeLoanIndex(x.getKey());
        }
    }

    public Bank(String name, String location, Client clientBankAccount, List<BankAccount> bankAccounts, Client clientLoan, List<Loan> loans) throws BankException {
        BankValidation.validateName(name);
        counterBankID++;
        this.bankID = counterBankID;
        this.name = name;
        this.location = location;

        clientBankAccountMap = new HashMap<>();
        this.clientBankAccountMap.put(clientBankAccount, bankAccounts);
        normalizeBankIndex(clientBankAccount);

        clientLoanMap = new HashMap<>();
        this.clientLoanMap.put(clientLoan, loans);
        normalizeLoanIndex(clientLoan);

    }

    public Bank(String name, String location, Client clientBankAccount, BankAccount bankAccounts, Client clientLoan, Loan loans) throws BankException {
        BankValidation.validateName(name);
        counterBankID++;
        this.bankID = counterBankID;
        this.name = name;
        this.location = location;

        clientBankAccountMap = new HashMap<>();
        List<BankAccount> localBank = new ArrayList<>();
        localBank.add(bankAccounts);
        this.clientBankAccountMap.put(clientBankAccount, localBank);
        normalizeBankIndex(clientBankAccount);

        clientLoanMap = new HashMap<>();
        List<Loan> localLoan = new ArrayList<>();
        localLoan.add(loans);
        this.clientLoanMap.put(clientLoan, localLoan);
        normalizeLoanIndex(clientLoan);
    }

    public static int getCounterBankID() {
        return counterBankID;
    }

    public static void setCounterBankID(int counterBankID) {
        Bank.counterBankID = counterBankID;
    }

    public int getBankID() {
        return bankID;
    }

    public void setBankID(int bankID) {
        this.bankID = bankID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws BankException {
        BankValidation.validateName(name);
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Map<Client, List<BankAccount>> getClientBankAccountMap() {
        return clientBankAccountMap;
    }

    public void setClientBankAccountMap(Map<Client, List<BankAccount>> clientBankAccountMap) {
        this.clientBankAccountMap = clientBankAccountMap;
    }

    public Map<Client, List<Loan>> getClientLoanMap() {
        return clientLoanMap;
    }

    public void setClientLoanMap(Map<Client, List<Loan>> clientLoanMap) {
        this.clientLoanMap = clientLoanMap;
    }

    public void normalizeBankIndex(Client client) {
        Timestamp.timestamp("Bank,normalizeBankIndex");
        for (Map.Entry<Client, List<BankAccount>> x : this.clientBankAccountMap.entrySet()) {
            if (x.getKey().equals(client)) {
                List<BankAccount> dummy = x.getValue();
                int poz = 1;
                for (BankAccount y : dummy) {
                    y.setBankAccountID(poz);
                    poz++;
                }
            }
        }
    }

    public void normalizeBankIndex() {
        Timestamp.timestamp("Bank,normalizeBankIndex");
        for (Map.Entry<Client, List<BankAccount>> x : this.clientBankAccountMap.entrySet()) {
            List<BankAccount> dummy = x.getValue();
            int poz = 1;
            for (BankAccount y : dummy) {
                y.setBankAccountID(poz);
                poz++;
            }
        }
    }

    public void normalizeLoanIndex(Client client) {
        Timestamp.timestamp("Bank,normalizeLoanIndex");
        for (Map.Entry<Client, List<Loan>> x : this.clientLoanMap.entrySet()) {
            if (x.getKey().equals(client)) {
                List<Loan> dummy = x.getValue();
                int poz = 1;
                for (Loan y : dummy) {
                    y.setLoanID(poz);
                    poz++;
                }
            }
        }
    }

    public void normalizeLoanIndex() {
        Timestamp.timestamp("Bank,normalizeLoanIndex");
        for (Map.Entry<Client, List<Loan>> x : this.clientLoanMap.entrySet()) {
            List<Loan> dummy = x.getValue();
            int poz = 1;
            for (Loan y : dummy) {
                y.setLoanID(poz);
                poz++;
            }
        }
    }

    public void addBankAccountClient(Client client) {
        Timestamp.timestamp("Bank,addBankAccountClient");
        if (this.clientBankAccountMap.containsKey(client)) {
            System.out.println("Clientul " + client.getFirstName() + " " + client.getLastName() + " exista deja!\n");
        } else {
            List<BankAccount> dummy = new ArrayList<>();
            this.clientBankAccountMap.put(client, dummy);
            BankService.getInstance().create(client);
            BankService.getInstance().update("Client", client.getCnp(), String.valueOf(this.getBankID()));
        }
    }

    public void addBankAccount(Client client, BankAccount bankAccount) {
        Timestamp.timestamp("Bank,addBankAccount");
        int avem = 0, c = 0;
        for (Map.Entry<Client, List<BankAccount>> x : this.clientBankAccountMap.entrySet()) {
            for (BankAccount y : x.getValue()) {
                if (y.equals(bankAccount)) {
                    System.out.println("Contul " + bankAccount.getIBAN() + " exista deja pentru clientul " + x.getKey().getFirstName() + " " + x.getKey().getLastName() + "\n");
                    c++;
                }
            }
        }
        for (Map.Entry<Client, List<BankAccount>> x : this.clientBankAccountMap.entrySet()) {
            if (x.getKey().equals(client)) {
                avem++;
                List<BankAccount> dummy = x.getValue();
                if (c == 0) {
                    if (dummy.isEmpty())
                        bankAccount.setBankAccountID(1);
                    dummy.add(bankAccount);
                    normalizeBankIndex(client);
                    BankAccount.setCounterBankAccountID(BankAccount.getCounterBankAccountID() + 1);
                    this.clientBankAccountMap.replace(x.getKey(), dummy);
                    BankService.getInstance().create(bankAccount);
                    BankService.getInstance().update("BankAccount", bankAccount.getIBAN(), client.getCnp());
                }
            }
        }
        if (avem == 0 && c == 0) {
            addBankAccountClient(client);
            List<BankAccount> dummy = new ArrayList<>();
            bankAccount.setBankAccountID(1);
            dummy.add(bankAccount);
            normalizeBankIndex(client);
            BankAccount.setCounterBankAccountID(BankAccount.getCounterBankAccountID() + 1);
            this.clientBankAccountMap.put(client, dummy);
            BankService.getInstance().create(bankAccount);
            BankService.getInstance().update("BankAccount", bankAccount.getIBAN(), client.getCnp());
        }
    }

    public void addLoanCLient(Client client) {
        Timestamp.timestamp("Bank,addLoanCLient");
        if (this.clientLoanMap.containsKey(client)) {
            System.out.println("Clientul " + client.getFirstName() + " " + client.getLastName() + " exista deja!\n");
        } else {
            List<Loan> dummy = new ArrayList<>();
            this.clientLoanMap.put(client, dummy);
            BankService.getInstance().create(client);
            BankService.getInstance().update("Client", client.getCnp(), String.valueOf(this.getBankID()));
        }
    }

    public void addLoan(Client client, Loan loan) {
        Timestamp.timestamp("Bank,addLoan");
        int avem = 0, c = 0;
        for (Map.Entry<Client, List<Loan>> x : this.clientLoanMap.entrySet()) {
            for (Loan y : x.getValue()) {
                if (y.equals(loan)) {
                    System.out.println("Imprumutul exista deja pentru clientul " + x.getKey().getFirstName() + " " + x.getKey().getLastName() + "\n");
                    c++;
                }
            }
        }
        for (Map.Entry<Client, List<Loan>> x : this.clientLoanMap.entrySet()) {
            if (x.getKey().equals(client)) {
                avem++;
                List<Loan> dummy = x.getValue();
                if (c == 0) {
                    if (dummy.isEmpty())
                        loan.setLoanID(1);
                    dummy.add(loan);
                    normalizeLoanIndex(client);
                    Loan.setCounterLoanID(Loan.getCounterLoanID() + 1);
                    this.clientLoanMap.replace(x.getKey(), dummy);
                    BankService.getInstance().createLoan(loan, x.getKey().getCnp());
                }
            }
        }
        if (avem == 0 && c == 0) {
            addLoanCLient(client);
            List<Loan> dummy = new ArrayList<>();
            loan.setLoanID(1);
            dummy.add(loan);
            normalizeLoanIndex(client);
            Loan.setCounterLoanID(Loan.getCounterLoanID() + 1);
            this.clientLoanMap.put(client, dummy);
            BankService.getInstance().createLoan(loan, client.getCnp());
        }
    }

    public void addCard(BankAccount bankAccount, Card card) {
        Timestamp.timestamp("Bank,addCard");
        int c = 0;
        for (Map.Entry<Client, List<BankAccount>> x : this.clientBankAccountMap.entrySet()) {
            if (x.getValue().contains(bankAccount)) {
                c++;
                for (BankAccount y : x.getValue()) {
                    if (y.equals(bankAccount)) {
                        if (!y.cardList.contains(card) && (y.getClosingDate() == null || y.getClosingDate().equals("-"))) {
                            BankService.getInstance().create(card);
                            BankService.getInstance().update("Card", card.getCardNumber(), bankAccount.getIBAN());
                            y.addCard(card);
                        } else if (y.getClosingDate() != null && !Objects.equals(y.getClosingDate(), "-"))
                            System.out.println("Contul " + bankAccount.getIBAN() + " a fost inchis deja, va rugam nu adaugati carduri.");
                    }
                }
            }
        }
        if (c == 0)
            System.out.println("Contul " + bankAccount.getIBAN() + " nu exista.");
    }

    public void removeCard(Card card) {
        Timestamp.timestamp("Bank,removeCard");
        int c = 0;
        for (Map.Entry<Client, List<BankAccount>> x : this.clientBankAccountMap.entrySet()) {
            for (BankAccount y : x.getValue()) {
                if (y.cardList.contains(card)) {
                    c++;
                    y.cardList.remove(card);
                }
            }
        }
        if (c == 0) {
            System.out.println("Cardul " + card.cardNumber + " nu exista");
        }
        BankService.getInstance().delete("card", card.getCardNumber());
    }

    public void removeCard(String cardNumber) {
        Timestamp.timestamp("Bank,removeCard");
        int c = 0;
        for (Map.Entry<Client, List<BankAccount>> x : this.clientBankAccountMap.entrySet()) {
            for (BankAccount y : x.getValue()) {
                for (Card z : y.getCardList()) {
                    if (z.getCardNumber().equals(cardNumber)) {
                        c++;
                        y.cardList.remove(z);
                    }
                }
            }
        }
        if (c == 0) {
            System.out.println("Cardul " + cardNumber + " nu exista");
        }
        BankService.getInstance().delete("card", cardNumber);
    }

    public void removeClientLoan(String cnp) {
        Timestamp.timestamp("Bank,removeClientLoan");
        Client evitConcurrentModificationException = new Client();
        int c = 0;
        int nrLoans = 0;
        for (Map.Entry<Client, List<Loan>> x : this.clientLoanMap.entrySet()) {
            if (x.getKey().getCnp().equals(cnp)) {
                evitConcurrentModificationException = x.getKey();
                c++;
                nrLoans = x.getValue().size();
            }
        }
        if (c != 0) {
            Loan.setCounterLoanID(Loan.getCounterLoanID() - nrLoans);
            this.clientLoanMap.remove(evitConcurrentModificationException);
            BankService.getInstance().deleteCheckBankAccount(cnp);
        }
    }

    public void removeClientLoan(Client client) {
        Timestamp.timestamp("Bank,removeClientLoan");
        int nrLoans = 0;
        if (this.clientLoanMap.containsKey(client)) {
            nrLoans = this.clientLoanMap.values().size() + 1;
            Loan.setCounterLoanID(Loan.getCounterLoanID() - nrLoans);
            this.clientLoanMap.remove(client);
            BankService.getInstance().deleteCheckBankAccount(client.getCnp());
        }
    }

    public void removeLoan(Loan loan) {
        Timestamp.timestamp("Bank,removeLoan");
        int c = 0;
        for (Map.Entry<Client, List<Loan>> x : this.clientLoanMap.entrySet()) {
            if (x.getValue().remove(loan)) {
                c++;
                Loan.setCounterLoanID(Loan.getCounterLoanID() - 1);
                List<Loan> dummy = x.getValue();
                for (Loan y : dummy) {
                    if (y.getLoanID() > loan.getLoanID()) {
                        y.setLoanID(y.getLoanID() - 1);
                        dummy.set(dummy.indexOf(y), y);
                    }
                }
                this.clientLoanMap.replace(x.getKey(), dummy);
                BankService.getInstance().deleteLoan(x.getKey().getCnp(), loan.getDate());
            }
        }
        if (c == 0) {
            System.out.println(loan.toString() + ", nu a fost gasit.\n");
        }
    }

    public void removeClientBankAccount(String cnp) {
        Timestamp.timestamp("Bank,removeClientBankAccount");
        Client evitConcurrentModificationException = new Client();
        int c = 0;
        int nrConturi = 0;
        for (Map.Entry<Client, List<BankAccount>> x : this.clientBankAccountMap.entrySet()) {
            if (x.getKey().getCnp().equals(cnp)) {
                evitConcurrentModificationException = x.getKey();
                c++;
                nrConturi = x.getValue().size();
            }
        }
        if (c != 0) {
            BankAccount.setCounterBankAccountID(BankAccount.getCounterBankAccountID() - nrConturi);
            this.clientBankAccountMap.remove(evitConcurrentModificationException);
            BankService.getInstance().deleteCheckLoan(cnp);
        }
    }

    public void removeClientBankAccount(Client client) {
        Timestamp.timestamp("Bank,removeClientBankAccount");
        int nrConturi = 0;
        if (this.clientBankAccountMap.containsKey(client)) {
            nrConturi = this.clientBankAccountMap.values().size() + 1;
            BankAccount.setCounterBankAccountID(BankAccount.getCounterBankAccountID() - nrConturi);
            this.clientBankAccountMap.remove(client);
            BankService.getInstance().deleteCheckLoan(client.getCnp());
        }
    }

    public void removeAccount(String IBAN) {
        Timestamp.timestamp("Bank,removeClientBankAccount");
        int c = 0;
        for (Map.Entry<Client, List<BankAccount>> x : this.clientBankAccountMap.entrySet()) {
            BankAccount local = null;
            List<BankAccount> dummy = x.getValue();
            for (BankAccount y : dummy) {
                if (y.getIBAN().equals(IBAN)) {
                    local = y;
                }
            }
            if (dummy.contains(local)) {
                c++;
                x.getValue().remove(local);
                BankAccount.setCounterBankAccountID(BankAccount.getCounterBankAccountID() - 1);
                for (BankAccount y : dummy) {
                    assert local != null;
                    if (y.getBankAccountID() > local.getBankAccountID()) {
                        y.setBankAccountID(y.getBankAccountID() - 1);
                        dummy.set(dummy.indexOf(y), y);
                        BankService.getInstance().update(y);
                    }
                }
                this.clientBankAccountMap.replace(x.getKey(), dummy);
                BankService.getInstance().delete("bankAccount", IBAN);
            }
        }
        if (c == 0) {
            System.out.println("Contul " + IBAN + ", nu a fost gasit.\n");
        }
    }

    public void removeAccount(BankAccount bankAccount) {
        Timestamp.timestamp("Bank,removeAccount");
        int c = 0;
        for (Map.Entry<Client, List<BankAccount>> x : this.clientBankAccountMap.entrySet()) {
            if (x.getValue().remove(bankAccount)) {
                c++;
                BankAccount.setCounterBankAccountID(BankAccount.getCounterBankAccountID() - 1);
                List<BankAccount> dummy = x.getValue();
                for (BankAccount y : dummy) {
                    if (y.getBankAccountID() > bankAccount.getBankAccountID()) {
                        y.setBankAccountID(y.getBankAccountID() - 1);
                        dummy.set(dummy.indexOf(y), y);
                    }
                }
                this.clientBankAccountMap.replace(x.getKey(), dummy);
                BankService.getInstance().delete("bankAccount", bankAccount.getIBAN());
            }
        }
        if (c == 0) {
            System.out.println(bankAccount.toString() + ", nu a fost gasit.\n");
        }
    }

    public void interBanking(String receiver, String sender, double value) throws TransactionException {
        Timestamp.timestamp("Bank,interBanking");
        int c = 0, c1 = 0, c2 = 0, c3 = 0, k = 0;
        if (value <= 0) {                                                                               // 1
            System.out.println("De ce incerci asta? Fa-o invers :)");                                   // 2
        } else {                                                                                        // 3
            BankAccount dupeReceiver = null;                                                            // 4
            BankAccount dupeSender = null;                                                              // 5
            for (Map.Entry<Client, List<BankAccount>> x : this.clientBankAccountMap.entrySet()) {       // 6
                for (BankAccount y : x.getValue()) {                                                    // 7
                    if (y.getIBAN().equals(receiver)) {                                                 // 8
                        dupeReceiver = y;                                                               // 9
                        c++;                                                                            // 10
                        if (!y.getClosingDate().equals("-"))                                            // 11
                            c2++;                                                                       // 12
                    }
                    if (y.getIBAN().equals(sender)) {                                                   // 13
                        dupeSender = y;                                                                 // 14
                        c1++;                                                                           // 15
                        if (!y.getClosingDate().equals("-"))                                            // 16
                            c3++;                                                                       // 17
                    }
                }
            }
            if (c != 0 && c1 != 0 && c2 == 0 && c3 == 0) {                                              // 18
                dupeSender.withdraw(value);                                                             // 19
                System.out.println("Transferul din contul " + dupeSender.getIBAN() + " in contul " +
                        dupeReceiver.getIBAN() + " in valoare de " + FormatDouble.format(value) + " " +
                        dupeSender.getCurrency() + " a avut succes!");                                  // 20
                value = CurrencyExchange.convertTransfer(value, dupeReceiver.getCurrency(),
                        dupeSender.getCurrency());                                                      // 21
                dupeReceiver.deposit(value);                                                            // 22
                k++;                                                                                    // 23
            }
            if (c2 != 0 && c3 == 0)                                                                     // 24
                System.out.println("Nu se poate face transferul! Contul " +
                        Objects.requireNonNull(dupeReceiver).getIBAN() + " a fost inchis!");            // 25
            else if (c2 == 0 && c3 != 0)                                                                // 26
                System.out.println("Nu se poate face transferul! Contul " +
                        Objects.requireNonNull(dupeSender).getIBAN() + " a fost inchis!");              // 27
            else if (c2 != 0)                                                                           // 28
                System.out.println("Nu se poate face transferul! Ambele conturi au fost inchise!");     // 29
            else if (c != 0 && c1 == 0)                                                                 // 30
                System.out.println("Nu exista contul in care transferati");                             // 31
            else if (c1 != 0 && c == 0)                                                                 // 32
                System.out.println("Nu exista contul din care transferati");                            // 33
            else if (k == 0)                                                                            // 34
                System.out.println("Nu exista nici un cont");                                           // 35
        }                                                                                               // 36
    }

    public void balanceCheck(BankAccount bankAccount) {
        Timestamp.timestamp("Bank,balanceCheck");
        int c = 0;
        for (Map.Entry<Client, List<BankAccount>> x : this.clientBankAccountMap.entrySet()) {
            if (x.getValue().contains(bankAccount)) {
                c++;
                for (BankAccount y : x.getValue()) {
                    if (y.equals(bankAccount)) {
                        balanceCheck(y.getBalance(), y.getCurrency());
                    }
                }
            }
        }
        if (c == 0)
            System.out.println("Nu exista contul selectat, va rugam verificati informatiile");
    }

    public void filterByDate(String IBAN, String startDate, String sign) {
        try {
            Timestamp.timestamp("Bank,filterByDate");
            BankAccountValidation.validateIBAN(IBAN);
            BankAccountValidation.validateOpeningDate(startDate);
            BankValidation.validateSign(sign);
            WriterFiles.getInstance().writerAccountStatementTemp(IBAN, filterDate(IBAN, startDate, sign));
        } catch (BankAccountException | BankException e) {
            e.printStackTrace();
        }
    }

    public void filterByDate(String IBAN, String startDate, String sign, String stopDate) {
        try {
            Timestamp.timestamp("Bank,filterByDate");
            BankAccountValidation.validateIBAN(IBAN);
            BankAccountValidation.validateOpeningDate(startDate);
            BankValidation.validateSign(sign);
            WriterFiles.getInstance().writerAccountStatementTemp(IBAN, filterDate(IBAN, startDate, sign, stopDate));

        } catch (BankAccountException | BankException e) {
            e.printStackTrace();
        }
    }

    public void filterByValue(String IBAN, double value, String sign) {
        try {
            Timestamp.timestamp("Bank,filterByValue");
            BankAccountValidation.validateIBAN(IBAN);
            BankValidation.validateSign(sign);
            WriterFiles.getInstance().writerAccountStatementTemp(IBAN, filterValue(IBAN, value, sign));
        } catch (BankAccountException | BankException e) {
            e.printStackTrace();
        }
    }

    public void filterByValue(String IBAN, double minValue, String sign, double maxValue) {
        try {
            Timestamp.timestamp("Bank,filterByValue");
            BankAccountValidation.validateIBAN(IBAN);
            BankValidation.validateSign(sign);
            WriterFiles.getInstance().writerAccountStatementTemp(IBAN, filterValue(IBAN, minValue, sign, maxValue));
        } catch (BankAccountException | BankException e) {
            e.printStackTrace();
        }
    }

    public void filterByCurrency(String IBAN, String currency, String sign) {
        try {
            Timestamp.timestamp("Bank,filterByValue");
            BankAccountValidation.validateIBAN(IBAN);
            BankValidation.validateSign(sign);
            WriterFiles.getInstance().writerAccountStatementTemp(IBAN, filterCurrency(IBAN, currency, sign));
        } catch (BankAccountException | BankException e) {
            e.printStackTrace();
        }
    }

    public void paymentUtilies(String Sender, String Receiver, double value) throws ProviderException, TransactionException {
        Timestamp.timestamp("Bank,paymentUtilies");
        int c = 0, c1 = 0;
        for (Map.Entry<Client, List<BankAccount>> x : this.clientBankAccountMap.entrySet()) {
            for (BankAccount y : x.getValue()) {
                if (y.getIBAN().equals(Sender)) {
                    c++;
                    if (!y.getClosingDate().equals("-"))
                        c1++;
                    else {
                        System.out.print("\tClientul " + x.getKey().getFirstName() + " " + x.getKey().getLastName());
                        y.paymentUtilies(Receiver, value);
                    }
                }
            }
        }
        if (c == 0)
            System.out.println("Nu exista contul " + Sender);
        else if (c1 != 0)
            System.out.println("Nu se poate plati providerul! Contul " + Sender + " a fost inchis!");

    }

    public void currencyExchange(BankAccount bankAccount, String wantedCurrency) throws BankAccountException {
        Timestamp.timestamp("Bank,currencyExchange");
        String oldCurrency = bankAccount.getCurrency();
        int c = 0, c1 = 0;
        for (Map.Entry<Client, List<BankAccount>> x : this.clientBankAccountMap.entrySet()) {
            for (BankAccount y : x.getValue()) {
                if (Objects.equals(y.getIBAN(), bankAccount.getIBAN())) {
                    c++;
                    if (!bankAccount.getClosingDate().equals("-"))
                        c1++;
                    else {
                        boolean check = bankAccount.currencyExchange(wantedCurrency, getClientBankAccountMap());
                        if (check) {
                            System.out.println("Conversie realizata! (" + oldCurrency + "->" + wantedCurrency + ")");
                        } else {
                            System.out.println("Conversie esuata!");
                        }
                    }
                }
            }
        }
        if (c == 0)
            System.out.println("Nu exista contul " + bankAccount.getIBAN());
        else if (c1 != 0)
            System.out.println("Nu se poate face conversia! Contul " + bankAccount.getIBAN() + " a fost inchis!");
    }

    public void currencyExchange(String IBAN, String wantedCurrency) {
        Timestamp.timestamp("Bank,currencyExchange");
        int c = 0, c1 = 0;
        for (Map.Entry<Client, List<BankAccount>> x : this.clientBankAccountMap.entrySet()) {
            for (BankAccount y : x.getValue()) {
                if (y.getIBAN().equals(IBAN)) {
                    c++;
                    if (!y.getClosingDate().equals("-"))
                        c1++;
                    else {
                        System.out.println("Conversie realizata! (" + y.getCurrency() + "->" + wantedCurrency + ")");
                        y.currencyExchange(wantedCurrency);
                    }
                }
            }
        }

        if (c == 0)
            System.out.println("Nu exista contul " + IBAN);
        else if (c1 != 0)
            System.out.println("Nu se poate face conversia! Contul " + IBAN + " a fost inchis!");
    }

    public void payLoan(BankAccount bankAccount, Loan loan, double value) throws TransactionException {
        Timestamp.timestamp("Bank,payLoan");
        Client dummy = new Client();
        Loan dupe = new Loan();
        Loan.setCounterLoanID(Loan.getCounterLoanID() - 1);
        int c = 0, c1 = 0, c2 = 0;
        for (Map.Entry<Client, List<Loan>> x : this.clientLoanMap.entrySet())
            if (x.getValue().contains(loan)) {
                dummy = x.getKey();
                c++;
                for (Loan z : x.getValue())
                    if (z.equals(loan))
                        dupe = z;
            }
        for (Map.Entry<Client, List<BankAccount>> y : this.clientBankAccountMap.entrySet())
            if (y.getValue().contains(bankAccount)) {
                c1++;
                if (!(y.getKey().equals(dummy))) {
                    System.out.println("Cele doua produse nu apartin aceluiasi client");
                } else {
                    if (!bankAccount.getClosingDate().equals("-"))
                        c2++;
                    else {
                        bankAccount.withdraw(CurrencyExchange.convertTransfer(value, bankAccount.getCurrency(), loan.getCurrency()));
                        System.out.print("Clientul " + dummy.getFirstName() + " " + dummy.getLastName());
                        dupe.payMonthlyRate(value, dummy.getCnp());
                    }
                }
            }
        if (c == 0 && c1 != 0)
            System.out.println("Imprumutul nu exista");
        else if (c != 0 && c1 == 0)
            System.out.println("Contul " + bankAccount.getIBAN() + " nu exista.");
        else if (c == 0)
            System.out.println("Nici contul, nici imprumutul nu exista");
        else if (c2 != 0)
            System.out.println("Nu se poate plati rata! Contul " + bankAccount.getIBAN() + " a fost inchis!");
    }

    public void payLoan(BankAccount bankAccount, Loan loan) throws TransactionException {
        Timestamp.timestamp("Bank,payLoan");
        Client dummy = new Client();
        Loan dupe = new Loan();
        int c = 0, c1 = 0, c2 = 0;
        for (Map.Entry<Client, List<Loan>> x : this.clientLoanMap.entrySet())
            if (x.getValue().contains(loan)) {
                dummy = x.getKey();
                c++;
                for (Loan z : x.getValue())
                    if (z.equals(loan))
                        dupe = z;
            }
        for (Map.Entry<Client, List<BankAccount>> y : this.clientBankAccountMap.entrySet())
            if (y.getValue().contains(bankAccount)) {
                c1++;
                if (!(y.getKey().equals(dummy))) {
                    System.out.println("Cele doua produse nu apartin aceluiasi client");
                } else {
                    if (!bankAccount.getClosingDate().equals("-"))
                        c2++;
                    else {
                        bankAccount.withdraw(CurrencyExchange.convertTransfer(loan.valueMonthlyRate(), bankAccount.getCurrency(), loan.getCurrency()));
                        System.out.print("Clientul " + dummy.getFirstName() + " " + dummy.getLastName());
                        dupe.payMonthlyRate(dummy.getCnp());
                    }
                }
            }
        if (c == 0 && c1 != 0)
            System.out.println("Imprumutul nu exista");
        else if (c != 0 && c1 == 0)
            System.out.println("Contul " + bankAccount.getIBAN() + " nu exista.");
        else if (c == 0)
            System.out.println("Nici contul, nici imprumutul nu exista");
        else if (c2 != 0)
            System.out.println("Nu se poate plati rata! Contul " + bankAccount.getIBAN() + " a fost inchis!");
    }

    public void addProvider(Provider provider) {
        Timestamp.timestamp("Bank,addProvider");
        BankService.getInstance().create(provider);
        ToProviders.addProvider(provider);
    }

    public void addProvider(List<Provider> providerList) {
        Timestamp.timestamp("Bank,addProvider");
        List<Object> objects = new ArrayList<>(providerList);
        BankService.getInstance().create(objects);
        ToProviders.addProvider(providerList);
    }

    public void removeProvider(Provider provider) {
        Timestamp.timestamp("Bank,removeProvider");
        BankService.getInstance().delete("Provider", provider.getIBAN());
        ToProviders.removeProvider(provider);
    }

    public void removeProvider(String IBAN) throws BankAccountException {
        Timestamp.timestamp("Bank,removeProvider");
        BankService.getInstance().delete("Provider", IBAN);
        ToProviders.removeProvider(IBAN);
    }

    public StringBuilder checkProviders() {
        Timestamp.timestamp("Bank,checkProviders");
        StringBuilder c = new StringBuilder();
        if (ToProviders.getInstance() == null || ToProviders.getProviderDBList().isEmpty())
            c.append("\tInca nu exista nici un provider inregistrat in banca!\n");
        else {
            c.append("\tAvem urmatorii provideri:\n");
            for (Provider x : ToProviders.getProviderDBList())
                c.append(" ~ ").append(x.toString()).append("\n");
        }
        return c;
    }

    public String bankReaderUpdate() {
        Timestamp.timestamp("Bank,bankReaderUpdate");
        return this.name + "," + this.location + "\n";
    }

    public List<String> bankAccountReaderUpdate() {
        Timestamp.timestamp("Bank,bankAccountReaderUpdate");
        List<String> local = new ArrayList<>();
        for (Map.Entry<Client, List<BankAccount>> x : this.clientBankAccountMap.entrySet()) {
            for (BankAccount y : x.getValue()) {
                local.add(x.getKey().getCnp() + "," + y.bankAccountReaderUpdate() + "\n");
            }
        }
        return local;
    }

    public List<String> cardReaderUpdate() {
        Timestamp.timestamp("Bank,cardReaderUpdate");
        List<String> local = new ArrayList<>();
        for (Map.Entry<Client, List<BankAccount>> x : this.clientBankAccountMap.entrySet()) {
            for (BankAccount y : x.getValue()) {
                for (Card z : y.getCardList()) {
                    local.add(y.getIBAN() + "," + z.cardReaderUpdate() + "\n");
                }
            }
        }
        return local;
    }

    public List<String> clientReaderUpdate() {
        Timestamp.timestamp("Bank,clientReaderUpdate");
        List<Client> localClient = new ArrayList<>();
        for (Map.Entry<Client, List<BankAccount>> x : this.clientBankAccountMap.entrySet()) {
            localClient.add(x.getKey());
        }

        for (Map.Entry<Client, List<Loan>> y : this.clientLoanMap.entrySet()) {
            if (!localClient.contains(y.getKey()))
                localClient.add(y.getKey());
        }

        List<String> local = new ArrayList<>();
        for (Client z : localClient) {
            local.add(z.clientReaderUpdate() + "\n");
        }

        return local;
    }

    public List<String> loanReaderUpdate() {
        Timestamp.timestamp("Bank,loanReaderUpdate");
        List<String> local = new ArrayList<>();
        for (Map.Entry<Client, List<Loan>> x : this.clientLoanMap.entrySet()) {
            for (Loan y : x.getValue()) {
                local.add(x.getKey().getCnp() + "," + y.loanReaderUpdate() + "\n");
            }
        }
        return local;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (this.getClass() != obj.getClass())
            return false;
        Bank bank = (Bank) obj;
        if (!Objects.equals(this.bankID, bank.bankID))
            return false;
        if (!Objects.equals(this.name, bank.name))
            return false;
        if (!Objects.equals(this.location, bank.location))
            return false;
        if (!Objects.equals(this.clientBankAccountMap, bank.clientBankAccountMap))
            return false;
        return Objects.equals(this.clientLoanMap, bank.clientLoanMap);
    }

    @Override
    public String toString() {
        StringBuilder c = new StringBuilder();
        c.append("\n\t\t" + "[").append(this.bankID).append("]").append(" Banca ").append(this.name).append(" cu ID-ul ").append(this.bankID).append(" aflata la ").append(this.location);
        if (this.clientBankAccountMap.size() == 0 && this.clientLoanMap.size() == 0)
            c.append(" nu are clienti.\n");
        else {
            List<Client> dummy = new ArrayList<>();
            for (Map.Entry<Client, List<BankAccount>> x : this.clientBankAccountMap.entrySet()) {
                dummy.add(x.getKey());
            }
            for (Map.Entry<Client, List<Loan>> y : this.clientLoanMap.entrySet()) {
                if (!dummy.contains(y.getKey()))
                    dummy.add(y.getKey());
            }
            if (dummy.size() == 1) {
                c.append(" are un singur client.\n");
                int contCont = 0, contImpr = 0;
                if (this.clientBankAccountMap.containsKey(dummy.get(0))) {
                    for (Map.Entry<Client, List<BankAccount>> x : this.clientBankAccountMap.entrySet()) {
                        if (x.getKey().equals(dummy.get(0)) && (!x.getValue().isEmpty())) {
                            c.append(dummy.get(0).toString()).append("\n  ->CONTURI:\n");
                            for (BankAccount y : x.getValue())
                                c.append(y.toString()).append("\n");
                            contCont++;
                        }
                    }
                }
                if (this.clientLoanMap.containsKey(dummy.get(0))) {
                    if (this.clientLoanMap.containsKey(dummy.get(0))) {
                        for (Map.Entry<Client, List<Loan>> x : this.clientLoanMap.entrySet()) {
                            if (x.getKey().equals(dummy.get(0)) && (!x.getValue().isEmpty())) {
                                if (contCont == 0)
                                    c.append(dummy.get(0).toString()).append("\n");
                                c.append("  ->IMPRUMUTURI:\n");
                                for (Loan y : x.getValue())
                                    c.append(y.toString()).append("\n");
                                contImpr++;
                            }
                        }
                    }
                }
                if (contCont == 0)
                    c.append(" ->NU ARE CONTURI\n");
                if (contImpr == 0)
                    c.append(" ->NU ARE IMPRUMUTURI\n");
                c.append("\n");
            } else {
                c.append(" are urmatorii clienti:\n");
                for (Client local : dummy) {
                    int contCont = 0, contImpr = 0;
                    if (this.clientBankAccountMap.containsKey(local)) {
                        for (Map.Entry<Client, List<BankAccount>> x : this.clientBankAccountMap.entrySet()) {
                            if (x.getKey().equals(local) && (!x.getValue().isEmpty())) {
                                c.append(local).append("\n  ->CONTURI:\n");
                                for (BankAccount y : x.getValue())
                                    c.append(y.toString()).append("\n");
                                contCont++;
                            }
                        }
                    }
                    if (this.clientLoanMap.containsKey(local)) {
                        if (this.clientLoanMap.containsKey(local)) {
                            for (Map.Entry<Client, List<Loan>> x : this.clientLoanMap.entrySet()) {
                                if (x.getKey().equals(local) && (!x.getValue().isEmpty())) {
                                    if (contCont == 0)
                                        c.append(local).append("\n");
                                    c.append("  ->IMPRUMUTURI:\n");
                                    for (Loan y : x.getValue())
                                        c.append(y.toString()).append("\n");
                                    contImpr++;
                                }
                            }
                        }
                    }
                    if (contCont == 0 && contImpr != 0)
                        c.append(" ->NU ARE CONTURI\n");
                    else if (contImpr == 0 && contCont != 0)
                        c.append("  ->NU ARE IMPRUMUTURI\n");
                    else if (contImpr == 0)
                        c.append(local.toString()).append(" nu are nici conturi, nici imprumuturi.\n");
                    c.append("\n");
                }
            }
        }
        c.append(this.checkProviders());
        return c.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.bankID, this.name, this.location, this.clientBankAccountMap, this.clientLoanMap);
    }
}