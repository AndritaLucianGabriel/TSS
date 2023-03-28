package repository;

import operations.Provider;
import service.Timestamp;
import service.exceptions.ProviderException;
import service.validations.ProviderValidation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProviderRepository {
    private Connection getConnection() {
        try {
            Timestamp.timestamp("ProviderRepository,getConnection");
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/project", "root", "root");
        } catch (SQLException throwables) {
            throw new RuntimeException("Nu se poate conecta la baza de date.");
        }
    }

    public void create(Provider provider) {
        try (Connection connection = getConnection()) {
            Timestamp.timestamp("ProviderRepository,create");
            String query = "INSERT into provider(company, IBAN, balance, currency) VALUES (?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, provider.getCompany());
            preparedStatement.setString(2, provider.getIBAN());
            preparedStatement.setDouble(3, provider.getBalance());
            preparedStatement.setString(4, provider.getCurrency());
            preparedStatement.executeUpdate();
            System.out.println("Providerul '" + provider.getCompany() + "' avand contul " + provider.getIBAN() + " a fost introdus in DBProvider cu succes!");
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new RuntimeException("Providerul " + provider.getCompany() + " este deja in DBProvider.");
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la inserarea imprumutului in DBProvider.");
        }
    }

    public List<Object> read() {
        try (Connection connection = this.getConnection()) {
            Timestamp.timestamp("ProviderRepository,read");
            String query = "SELECT * FROM project.provider ORDER BY IBAN ASC;";
            List<Object> providerList = new ArrayList<>();
            Provider local;
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                local = new Provider();
                local.setCompany(resultSet.getString(1));
                local.setIBAN(resultSet.getString(2));
                local.setBalance(resultSet.getDouble(3));
                local.setCurrency(resultSet.getString(4));
                providerList.add(local);
            }
            System.out.println("Citirea tuturor datelor din DBLoan s-a efectuat cu succes!\n");
            return providerList;
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la citirea din DBProvider.");
        } catch (ProviderException e) {
            throw new RuntimeException("Eroare la inserare datelor la citirea din DBProvider.");
        }
    }

    public Provider read(String IBAN) {
        try (Connection connection = this.getConnection()) {
            Timestamp.timestamp("ProviderRepository,read");
            ProviderValidation.validateIBAN(IBAN);
            String query = "SELECT * FROM project.provider WHERE IBAN LIKE ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, IBAN);
            ResultSet resultSet = preparedStatement.executeQuery();
            Provider local;
            if (resultSet.next()) {
                local = new Provider();
                local.setCompany(resultSet.getString(1));
                local.setIBAN(resultSet.getString(2));
                local.setBalance(resultSet.getDouble(3));
                local.setCurrency(resultSet.getString(4));
            } else
                throw new RuntimeException("Providerul cu contul " + IBAN + " nu exista in DB.");
            System.out.println("Citirea providerului '" + local.getCompany() + "' avand contul " + local.getIBAN() + " din DBProvider a avut succes!");
            return local;
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la citirea din DBProvider.");
        } catch (ProviderException e) {
            throw new RuntimeException("Eroare la inserare datelor la citirea din DBProvider.");
        }
    }

    public void update(Provider provider) {
        try (Connection connection = this.getConnection()) {
            Timestamp.timestamp("ProviderRepository,update");
            String query = "UPDATE project.provider SET company = ?, balance = ?, currency = ? WHERE IBAN LIKE ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, provider.getCompany());
            preparedStatement.setDouble(2, provider.getBalance());
            preparedStatement.setString(3, provider.getCurrency());
            preparedStatement.setString(4, provider.getIBAN());
            preparedStatement.executeUpdate();
            System.out.println("Update-ul providerului '" + provider.getCompany() + "' avand contul " + provider.getIBAN() + " din DBProvider a avut succes!");
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la update-ul din DBProvider.");
        }
    }

    public void delete() {
        try (Connection connection = getConnection()) {
            Timestamp.timestamp("ProviderRepository,delete");
            String query = "DELETE from provider;";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throw new RuntimeException("Nu se poate sterge din DBProvider.");
        }
    }

    public void delete(String IBAN) {
        try (Connection connection = getConnection()) {
            Timestamp.timestamp("ProviderRepository,delete");
            ProviderValidation.validateIBAN(IBAN);
            String query = "DELETE from provider WHERE IBAN like ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, IBAN);
            preparedStatement.executeUpdate();
            System.out.println("Stergerea providerului cu IBAN-ul " + IBAN + " din DBProvider a avut succes!");
        } catch (SQLException throwables) {
            throw new RuntimeException("Nu se poate sterge din DBProvider pe baza de IBAN.");
        } catch (ProviderException e) {
            throw new RuntimeException("Eroare la inserare datelor la citirea din DBProvider.");
        }
    }
}
