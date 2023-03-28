package operations;

import service.dbResources.service.BankService;
import service.exceptions.TransactionException;
import service.Timestamp;
import service.files.WriterFiles;

public class Transfer extends Transaction {

    public Transfer() {
        super();
    }

    public Transfer(String IBAN, double value, String currency) throws TransactionException {
        super(IBAN, value, currency);
    }

    public double withdraw(double value) {
        Timestamp.timestamp("Transfer,withdraw");
        this.tradeValue = -value;
        WriterFiles.getInstance().writerAccountStatement(this);
        BankService.getInstance().create(this);
        BankService.getInstance().setBalance(this.value - value, this.getIBAN());
        return this.value -= value;
    }

    public double deposit(double value) {
        Timestamp.timestamp("Transfer,withdraw");
        this.tradeValue = value;
        WriterFiles.getInstance().writerAccountStatement(this);
        BankService.getInstance().create(this);
        BankService.getInstance().setBalance(this.value + value, this.getIBAN());
        return this.value += value;
    }

    @Override
    public double paymentUtilities(String IBAN, double value) {
        return 0;
    }
}