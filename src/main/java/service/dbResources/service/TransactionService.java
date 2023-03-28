package service.dbResources.service;

import operations.Transaction;
import repository.TransactionRepository;
import service.Timestamp;

public class TransactionService {
    private final TransactionRepository transactionRepository = new TransactionRepository();

    protected void create(Transaction transaction) {
        Timestamp.timestamp("TransactionService,create");
        transactionRepository.create(transaction);
    }
}
