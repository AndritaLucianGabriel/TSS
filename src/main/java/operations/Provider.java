package operations;

import service.exceptions.ProviderException;
import service.Timestamp;
import service.FormatDouble;
import service.validations.ProviderValidation;

import java.util.Objects;

public class Provider {
    protected String company;
    protected String IBAN;
    protected double balance;
    protected String currency;

    public Provider() {
        this.company = "";
        this.IBAN = "";
        this.balance = 0;
        this.currency = "";
    }

    public Provider(String company, String IBAN, String currency) throws ProviderException {
        ProviderValidation.validateCompany(company);
        ProviderValidation.validateIBAN(IBAN);
        ProviderValidation.validateCurrency(currency);

        this.company = company;
        this.IBAN = IBAN;
        this.balance = 0;
        this.currency = currency;
    }

    public Provider(String company, String IBAN, double balance, String currency) throws ProviderException {
        ProviderValidation.validateCompany(company);
        ProviderValidation.validateIBAN(IBAN);
        ProviderValidation.validateBalance(balance);
        ProviderValidation.validateCurrency(currency);

        this.company = company;
        this.IBAN = IBAN;
        this.balance = balance;
        this.currency = currency;
    }

    public Provider(Provider provider) {
        this.company = provider.company;
        this.IBAN = provider.IBAN;
        this.balance = provider.balance;
        this.currency = provider.currency;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) throws ProviderException {
        ProviderValidation.validateCompany(company);
        this.company = company;
    }

    public String getIBAN() {
        return IBAN;
    }

    public void setIBAN(String IBAN) throws ProviderException {
        ProviderValidation.validateIBAN(IBAN);
        this.IBAN = IBAN;
    }

    public double getBalance() {
        return FormatDouble.format(balance);
    }

    public void setBalance(double balance) throws ProviderException {
        ProviderValidation.validateBalance(balance);
        this.balance = balance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) throws ProviderException {
        ProviderValidation.validateCurrency(currency);
        this.currency = currency;
    }

    protected String providerReaderUpdate() {
        Timestamp.timestamp("Provider,providerDBReaderUpdate");
        return this.company + "," + this.IBAN + "," + FormatDouble.format(this.balance) + "," + this.currency;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        Provider provider = (Provider) obj;
        if (this.getClass() != obj.getClass())
            return false;
        if (!Objects.equals(this.company, provider.company))
            return false;
        if (!Objects.equals(this.IBAN, provider.IBAN))
            return false;
        return this.balance == provider.balance;
    }

    @Override
    public String toString() {
        StringBuilder c;
        c = new StringBuilder();
        c.append("Compania '").append(this.company).append("' are ").append(FormatDouble.format(this.balance)).append(" ").append(this.currency).append(" in contul ").append(this.IBAN);
        return c.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.company, this.IBAN, this.balance);
    }
}
