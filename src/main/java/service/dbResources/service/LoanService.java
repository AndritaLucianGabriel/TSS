package service.dbResources.service;

import mainClasses.Loan;
import repository.LoanRepository;
import service.Timestamp;

import java.util.List;

public class LoanService {
    private final LoanRepository loanRepository = new LoanRepository();

    protected void create(Loan loan, String cnp) {
        Timestamp.timestamp("LoanService,create");
        loanRepository.create(loan, cnp);
    }

    protected List<Object> read() {
        Timestamp.timestamp("LoanService,read");
        return loanRepository.read();
    }

    protected Loan read(String cnp, String date) {
        Timestamp.timestamp("LoanService,read");
        return loanRepository.read(cnp, date);
    }

    protected void update(Loan loan, String FK) {
        Timestamp.timestamp("LoanService,update");
        loanRepository.updateByFK(loan, FK);
    }

    protected void delete() {
        Timestamp.timestamp("LoanService,delete");
        loanRepository.delete();
    }

    protected void delete(String cnp, String date) {
        Timestamp.timestamp("LoanService,delete");
        loanRepository.delete(cnp, date);
    }
}
