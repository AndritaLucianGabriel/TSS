package mainClasses;

import service.CurrencyExchange;
import service.exceptions.BankAccountException;
import service.exceptions.ProviderException;
import service.exceptions.TransactionException;
import service.Timestamp;
import service.FormatDouble;
import service.validations.BankAccountValidation;

import java.util.List;
import java.util.Objects;

public class SavingsAccount extends BankAccount {
    protected double annualInterestRate;

    public SavingsAccount() {
        super();
        annualInterestRate = 0;
    }

    public SavingsAccount(double annualInterestRate) throws BankAccountException {
        super();
        BankAccountValidation.validateAnnualInterestRate(annualInterestRate);
        this.annualInterestRate = annualInterestRate;
    }

    public SavingsAccount(String IBAN, String openingDate, String closingDate, double balance, String currency, double annualInterestRate) throws BankAccountException {
        super(IBAN, openingDate, closingDate, balance, currency);
        BankAccountValidation.validateAnnualInterestRate(annualInterestRate);
        this.annualInterestRate = annualInterestRate;
    }

    public SavingsAccount(String IBAN, String openingDate, String closingDate, double balance, String currency) throws BankAccountException {
        super(IBAN, openingDate, closingDate, balance, currency);
        this.annualInterestRate = 0;
    }

    public SavingsAccount(String IBAN, String openingDate, String closingDate, double balance, String currency, List<Card> cardList, double annualInterestRate) throws BankAccountException {
        super(IBAN, openingDate, closingDate, balance, currency, cardList);
        BankAccountValidation.validateAnnualInterestRate(annualInterestRate);
        this.annualInterestRate = annualInterestRate;
    }

    public SavingsAccount(String IBAN, String openingDate, String closingDate, double balance, String currency, List<Card> cardList) throws BankAccountException {
        super(IBAN, openingDate, closingDate, balance, currency, cardList);
        this.annualInterestRate = 0;
    }

    public double getAnnualInterestRate() {
        return annualInterestRate;
    }

    public void setAnnualInterestRate(double annualInterestRate) throws BankAccountException {
        BankAccountValidation.validateAnnualInterestRate(annualInterestRate);
        this.annualInterestRate = annualInterestRate;
    }

    protected void penalty() {
        Timestamp.timestamp("SavingsAccount,penalty");
        System.out.print("\tPenalizare: \n ~Interest: " + FormatDouble.format(this.annualInterestRate) + " -> ");
        this.annualInterestRate = this.annualInterestRate - this.annualInterestRate / 10;
        System.out.print(FormatDouble.format(this.annualInterestRate) + "\n ~Balance (-1 Euro): " + FormatDouble.format(super.balance) + " " + super.currency + " -> ");
        super.balance = super.balance - CurrencyExchange.convertTransferWithoutText(1, super.currency, "Euro");
        System.out.println(FormatDouble.format(super.balance) + " " + super.currency);
    }

    protected String bankAccountReaderUpdate() {
        Timestamp.timestamp("SavingsAccount,bankAccountReaderUpdate");
        return this.IBAN + "," + this.openingDate + "," + this.closingDate + "," + FormatDouble.format(this.balance) + "," + this.currency + "," + FormatDouble.format(this.annualInterestRate);
    }

    @Override
    protected void withdraw(double value) throws TransactionException {
        Timestamp.timestamp("SavingsAccount,penalty");
        super.withdraw(value);
        this.penalty();
    }

    @Override
    protected void paymentUtilies(String IBAN, double value) throws ProviderException, TransactionException {
        Timestamp.timestamp("SavingsAccount,penalty");
        super.paymentUtilies(IBAN, value);
        this.penalty();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        SavingsAccount savingsAccount = (SavingsAccount) obj;
        return this.annualInterestRate == savingsAccount.annualInterestRate;
    }

    @Override
    public String toString() {
        StringBuilder c = new StringBuilder();
        c.append("[").append(this.BankAccountID).append("]").append(" Contul de economii ").append(this.IBAN).append(" a fost deschis in data de ").append(this.openingDate);
        if (!(Objects.equals(this.closingDate, null) || Objects.equals(this.closingDate, "-")))
            c.append(" si a fost inchis in data de ").append(this.closingDate).append(", avand suma de ").append(FormatDouble.format(this.balance)).append(" ").append(this.currency).append(", avand dobanda anuala de ").append(FormatDouble.format(this.annualInterestRate)).append("%");
        else {
            c.append(", avand suma de ").append(FormatDouble.format(this.balance)).append(" ").append(this.currency).append(", avand comision anual de ").append(FormatDouble.format(this.annualInterestRate)).append("%");
            if (!cardList.isEmpty())
                for (Card x : this.cardList) {
                    c.append("\n ~ ").append(x.toString());
                }
        }
        return c.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.annualInterestRate);
    }
}