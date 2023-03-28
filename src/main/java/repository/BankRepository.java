package repository;

import mainClasses.*;
import service.Timestamp;
import service.exceptions.BankException;
import service.validations.BankValidation;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BankRepository {
    private final CardRepository cardRepository = new CardRepository();
    private final ClientRepository clientRepository = new ClientRepository();
    private final BankAccountRepository bankAccountRepository = new BankAccountRepository();
    private final LoanRepository loanRepository = new LoanRepository();

    private Connection getConnection() {
        try {
            Timestamp.timestamp("BankRepository,getConnection");
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/project", "root", "root");
        } catch (SQLException throwables) {
            throw new RuntimeException("Nu se poate conecta la baza de date.");
        }
    }

    public void create(Bank bank) {
        try (Connection connection = this.getConnection()) {
            Timestamp.timestamp("BankRepository,create");
            String query = "INSERT into bank(id, name, location) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, bank.getBankID());
            preparedStatement.setString(2, bank.getName());
            preparedStatement.setString(3, bank.getLocation());
            preparedStatement.executeUpdate();
            List<Client> dummy = new ArrayList<>();
            for (Map.Entry<Client, List<BankAccount>> x : bank.getClientBankAccountMap().entrySet()) {
                dummy.add(x.getKey());
            }
            for (Map.Entry<Client, List<Loan>> y : bank.getClientLoanMap().entrySet()) {
                if (!dummy.contains(y.getKey()))
                    dummy.add(y.getKey());
            }
            for (Client x : dummy)
                clientRepository.create(x, bank.getBankID());
            System.out.println();
            for (Map.Entry<Client, List<BankAccount>> x : bank.getClientBankAccountMap().entrySet()) {
                for (BankAccount y : x.getValue())
                    bankAccountRepository.create(y, x.getKey().getCnp());
            }
            for (Map.Entry<Client, List<Loan>> x : bank.getClientLoanMap().entrySet()) {
                for (Loan y : x.getValue())
                    loanRepository.create(y, x.getKey().getCnp());
            }
            System.out.println("\n [!!] Banca '" + bank.getName() + "' aflata la adresa '" + bank.getLocation() + "' a fost introdusa in DBBank cu succes in totalitate!\n");
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new RuntimeException("Banca " + bank.getName() + " este deja in DBBank.");
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la inserarea imprumutului in DBBank.");
        }
    }

    public List<Object> read() {
        try (Connection connection = this.getConnection()) {
            Timestamp.timestamp("BankRepository,read");
            String query = "SELECT * FROM project.bank;";
            List<Object> bankList = new ArrayList<>();
            Bank local;
            Map<Client, List<BankAccount>> clientBankAccountMap;
            Map<Client, List<Loan>> clientLoanMap;
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                local = new Bank();
                clientBankAccountMap = new HashMap<>();
                clientLoanMap = new HashMap<>();
                local.setBankID(resultSet.getInt(1));
                local.setName(resultSet.getString(2));
                local.setLocation(resultSet.getString(3));
                for (Client x : clientRepository.readByFK(resultSet.getInt(1))) {
                    clientBankAccountMap.put(x, bankAccountRepository.readByFK(x.getCnp()));
                    clientLoanMap.put(x, loanRepository.readByFK(x.getCnp()));
                }
                local.setClientBankAccountMap(clientBankAccountMap);
                local.setClientLoanMap(clientLoanMap);
                bankList.add(local);
            }
            System.out.println("\n [!!] Citirea tuturor datelor din DBBank s-a efectuat cu succes!\n");
            return bankList;
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la citirea din DBBank.");
        } catch (BankException e) {
            throw new RuntimeException("Eroare la inserare datelor la citirea din DBBank.");
        }
    }

    public Bank read(int id) {
        try (Connection connection = this.getConnection()) {
            Timestamp.timestamp("BankRepository,read");
            String query = "SELECT * FROM project.bank WHERE id = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            Bank local;
            if (resultSet.next()) {
                local = new Bank();
                local.setBankID(resultSet.getInt(1));
                local.setName(resultSet.getString(2));
                local.setLocation(resultSet.getString(3));
            } else
                throw new RuntimeException("Banca cu id-ul " + id + " nu exista in DB.");
            System.out.println("Citirea bancii " + local.getName() + " din DBBank a avut succes!");
            return local;
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la citirea din DBBank.");
        } catch (BankException e) {
            throw new RuntimeException("Eroare la inserare datelor la citirea din DBBank.");
        }
    }

    public void update(Bank bank) {
        try (Connection connection = this.getConnection()) {
            Timestamp.timestamp("BankRepository,update");
            String query = "UPDATE project.bank SET name = ?, location = ? WHERE id = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            List<Client> dummy = new ArrayList<>();
            for (Map.Entry<Client, List<BankAccount>> x : bank.getClientBankAccountMap().entrySet()) {
                dummy.add(x.getKey());
                for (BankAccount y : x.getValue()) {
                    for (Card z : y.getCardList())
                        cardRepository.update(z);
                    bankAccountRepository.update(y);
                }
            }
            for (Map.Entry<Client, List<Loan>> y : bank.getClientLoanMap().entrySet()) {
                if (!dummy.contains(y.getKey()))
                    dummy.add(y.getKey());
                for (Loan t : y.getValue())
                    loanRepository.updateByFK(t, y.getKey().getCnp());
            }
            for (Client x : dummy)
                clientRepository.update(x);
            preparedStatement.setString(1, bank.getName());
            preparedStatement.setString(2, bank.getLocation());
            preparedStatement.setInt(3, bank.getBankID());
            preparedStatement.executeUpdate();
            System.out.println("Update-ul bancii " + bank.getName() + " din DBBank a avut succes!");
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la update-ul din DBBank.");
        }
    }

    public void delete() {
        try (Connection connection = getConnection()) {
            Timestamp.timestamp("BankRepository,delete");
            String query = "DELETE from bank;";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throw new RuntimeException("Nu se poate sterge din DBBank.");
        }
    }

    public void delete(int id) {
        try (Connection connection = getConnection()) {
            Timestamp.timestamp("BankRepository,delete");
            String query = "DELETE from bank WHERE id = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            System.out.println("Stergerea bancii cu id-ul " + id + " din DBBank a avut succes!");
        } catch (SQLException throwables) {
            throw new RuntimeException("Nu se poate sterge din DBBank pe baza de id.");
        }
    }

    public void delete(String name) {
        try (Connection connection = getConnection()) {
            Timestamp.timestamp("BankRepository,deleteBank");
            BankValidation.validateName(name);
            String query = "DELETE from bank WHERE name like ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.executeUpdate();
            System.out.println("Stergerea bancii " + name + " din DBBank a avut succes!");
        } catch (SQLException throwables) {
            throw new RuntimeException("Nu se poate sterge din DBBank pe baza de nume.");
        } catch (BankException e) {
            throw new RuntimeException("Eroare la inserare datelor la citirea din DBBank.");
        }
    }

}
