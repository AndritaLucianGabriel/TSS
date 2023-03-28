package operations;

import service.CurrencyExchange;
import service.dbResources.service.BankService;
import service.exceptions.BankAccountException;
import service.exceptions.ProviderException;
import service.exceptions.TransactionException;
import service.Timestamp;
import service.files.WriterFiles;
import service.FormatDouble;
import service.validations.BankAccountValidation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ToProviders extends Transaction {
    private static ToProviders instance = null;
    protected static List<Provider> providerList = new ArrayList<>();

    private ToProviders() {
    }

    public static ToProviders getInstance() {
        if (instance == null)
            instance = new ToProviders();
        return instance;
    }

    private ToProviders(String IBAN, double value, String currency) throws TransactionException {
        super(IBAN, value, currency);
    }

    public static ToProviders getInstance(String IBAN, double value, String currency) throws TransactionException {
        return new ToProviders(IBAN, value, currency);
    }

    public static List<Provider> getProviderDBList() {
        return providerList;
    }

    public static List<Object> getProvidersAsObject() {
        return new ArrayList<>(ToProviders.getProviderDBList());
    }

    public static void addProvider(Provider provider) {
        Timestamp.timestamp("ToProviders,addProvider");
        instance = ToProviders.getInstance();
        if (providerList.contains(provider))
            System.out.println("Providerul " + provider.getCompany() + " exista deja.");
        else {
            providerList.add(provider);
            System.out.println("Providerul " + provider.getCompany() + " a fost adaugat cu succes!");
        }
    }

    public static void addProvider(List<Provider> providerList) {
        Timestamp.timestamp("ToProviders,addProvider");
        instance = ToProviders.getInstance();
        if (ToProviders.providerList.isEmpty()) {
            ToProviders.providerList.addAll(providerList);
        } else {
            for (Provider x : providerList) {
                if (!ToProviders.providerList.contains(x)) {
                    ToProviders.providerList.add(x);
                }
            }
        }
    }

    public static void removeProvider(Provider provider) {
        Timestamp.timestamp("ToProviders,removeProvider");
        if (!providerList.remove(provider))
            System.out.println("Nu exista providerul " + provider.getCompany());
        else {
            System.out.println("Providerul " + provider.getCompany() + " a fost eliminat cu succes!");
        }
    }

    public static void removeProvider(String IBAN) throws BankAccountException {
        Timestamp.timestamp("ToProviders,removeProvider");
        BankAccountValidation.validateIBAN(IBAN);
        int c = 0;
        Provider local = new Provider();
        for (Provider x : providerList)
            if (x.getIBAN().equals(IBAN)) {
                c++;
                local = x;
            }
        if (c == 0)
            System.out.println("Nu exista providerul cu IBAN-ul " + IBAN);
        else {
            System.out.println("Providerul a fost eliminat cu succes!");
            providerList.remove(local);
        }
    }

    public static List<String> toProvidersReaderUpdate() {
        Timestamp.timestamp("ToProviders,toProviderReaderUpdate");
        List<String> local = new ArrayList<>();
        for (Provider x : providerList) {
            local.add(x.providerReaderUpdate() + "\n");
        }
        return local;
    }

    public String anotherToString() {
        Timestamp.timestamp("ToProviders,anotherToString");
        return transactionID +
                "," + timestamp +
                "," + FormatDouble.format(tradeValue) +
                "," + currency + "\n";
    }

    @Override
    public double paymentUtilities(String IBAN, double value) throws ProviderException {
        Timestamp.timestamp("ToProviders,paymentUtilities");
        double val = 0, c = 0;
        for (Provider x : providerList) {
            if (Objects.equals(x.getIBAN(), IBAN)) {
                c++;
                System.out.println(" in contul " + IBAN + " (" + x.getCompany() + ")" + "\nSold anterior: " + FormatDouble.format(x.getBalance()) + " " + x.getCurrency());
                x.setBalance(x.getBalance() + CurrencyExchange.convertTransfer(value, x.currency, this.currency));
                System.out.println("Sold nou: " + FormatDouble.format(x.getBalance()) + " " + x.currency);
                val = this.value - value;
                this.tradeValue = -value;
                WriterFiles.getInstance().writerAccountStatement(this);
                BankService.getInstance().create(this);
                BankService.getInstance().update(x);
                break;
            }
        }
        if (c == 0)
            System.out.println("Nu exista providerul cu IBAN-ul: " + IBAN);
        return val;
    }

    @Override
    public double withdraw(double value) {
        return 0;
    }

    @Override
    public double deposit(double value) {
        return 0;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        StringBuilder c = new StringBuilder();
        if (providerList.isEmpty())
            c.append("\tNu avem nici un provider.");
        else if (providerList.size() == 1)
            c.append("\tAvem urmatorul provider:\n").append(providerList.get(0).toString());
        else {
            c.append("\tAvem urmatorii provideri:");
            for (Provider x : providerList)
                c.append("\n").append(x.toString());
        }
        return c.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(instance, providerList);
    }
}