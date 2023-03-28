package repository;

import operations.Transaction;
import service.Timestamp;

import java.sql.*;

public class TransactionRepository {
    private Connection getConnection() {
        try {
            Timestamp.timestamp("TransactionRepository,getConnection");
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/project", "root", "root");
        } catch (SQLException throwables) {
            throw new RuntimeException("Nu se poate conecta la baza de date.");
        }
    }

    public void create(Transaction transaction) {
        try (Connection connection = getConnection()) {
            Timestamp.timestamp("TransactionRepository,create");
            String query = "INSERT into transaction(transactionID, IBAN, timestamp, tradeValue, currency) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, transaction.getTransactionID());
            preparedStatement.setString(2, transaction.getIBAN());
            preparedStatement.setString(3, String.valueOf(transaction.getTimestamp()));
            preparedStatement.setDouble(4, transaction.getTradeValue());
            preparedStatement.setString(5, transaction.getCurrency());
            preparedStatement.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new RuntimeException("Tranzactia " + transaction.getTransactionID() + " este deja in DBTransaction.");
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la inserarea tranzactiei in DBTransaction.");
        }
    }
}