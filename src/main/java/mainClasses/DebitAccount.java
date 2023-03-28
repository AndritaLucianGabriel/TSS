package mainClasses;

import service.exceptions.BankAccountException;
import service.Timestamp;
import service.FormatDouble;

import java.util.List;
import java.util.Objects;

public class DebitAccount extends BankAccount {
    public DebitAccount() {
        super();
    }

    public DebitAccount(String IBAN, String openingDate, String closingDate, double balance, String currency) throws BankAccountException {
        super(IBAN, openingDate, closingDate, balance, currency);
    }

    public DebitAccount(String IBAN, String openingDate, String closingDate, double balance, String currency, List<Card> cardList) throws BankAccountException {
        super(IBAN, openingDate, closingDate, balance, currency, cardList);
    }

    protected String bankAccountReaderUpdate() {
        Timestamp.timestamp("DebitAccount,bankAccountReaderUpdate");
        return this.IBAN + "," + this.openingDate + "," + this.closingDate + "," + FormatDouble.format(this.balance) + "," + this.currency;
    }

    @Override
    public void setAnnualInterestRate(double annualInterestRate) {
    }

    @Override
    public String toString() {
        StringBuilder c = new StringBuilder();
        c.append("[").append(this.BankAccountID).append("]").append(" Contul de debit ").append(this.IBAN).append(" a fost deschis in data de ").append(this.openingDate);
        if (!(Objects.equals(this.closingDate, null) || Objects.equals(this.closingDate, "-")))
            c.append(" si a fost inchis in data de ").append(this.closingDate).append(", avand suma de ").append(FormatDouble.format(this.balance)).append(" ").append(this.currency);
        else {
            c.append(", avand suma de ").append(FormatDouble.format(this.balance)).append(" ").append(this.currency);
            if (!cardList.isEmpty())
                for (Card x : this.cardList) {
                    c.append("\n ~ ").append(x.toString());
                }
        }
        return c.toString();
    }
}
