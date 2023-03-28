package repository;

import mainClasses.*;
import service.Timestamp;
import service.exceptions.BankAccountException;
import service.exceptions.CardException;
import service.validations.BankAccountValidation;
import service.validations.CardValidation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CardRepository {

    private Connection getConnection() {
        try {
            Timestamp.timestamp("CardRepository,getConnection");
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/project", "root", "root");
        } catch (SQLException throwables) {
            throw new RuntimeException("Nu se poate conecta la baza de date.");
        }
    }

    public void create(Card card) {
        try (Connection connection = getConnection()) {
            Timestamp.timestamp("CardRepository,create");
            String query = "INSERT into card(cardNumber, IBAN, PIN, issueDate) VALUES (?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, card.getCardNumber());
            preparedStatement.setNull(2, Types.VARCHAR);
            preparedStatement.setInt(3, card.getPIN());
            preparedStatement.setString(4, card.getIssueDate());
            preparedStatement.executeUpdate();
            System.out.println("Cardul " + card.getCardNumber() + " a fost introdus in DBCard cu succes!\n");
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new RuntimeException("Cardul " + card.getCardNumber() + " exista deja in DBCard.");
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la inserarea cardului in DBCard.");
        }
    }

    public void create(Card card, String IBAN) {
        try (Connection connection = getConnection()) {
            Timestamp.timestamp("CardRepository,create");
            BankAccountValidation.validateIBAN(IBAN);
            String query = "INSERT into card(cardNumber, IBAN, PIN, issueDate) VALUES (?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, card.getCardNumber());
            preparedStatement.setString(2, IBAN);
            preparedStatement.setInt(3, card.getPIN());
            preparedStatement.setString(4, card.getIssueDate());
            preparedStatement.executeUpdate();
            System.out.println("~ Cardul " + card.getCardNumber() + " a fost introdus in DBCard cu succes!");
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new RuntimeException("Cardul " + card.getCardNumber() + " exista deja in DBCard.");
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la inserarea cardului in DBCard.");
        } catch (BankAccountException e) {
            throw new RuntimeException("Eroare la setarea FK-ului pentru cardul " + card.getCardNumber() + " din DBCard.");
        }
    }

    public List<Object> read() {
        try (Connection connection = this.getConnection()) {
            Timestamp.timestamp("CardRepository,read");
            String query = "SELECT * FROM project.card ORDER BY cardNumber ASC;";
            List<Object> cardList = new ArrayList<>();
            Card local;
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                local = new Card();
                local.setCardNumber(resultSet.getString(1));
                local.setPIN(resultSet.getInt(3));
                local.setIssueDate(resultSet.getString(4));
                cardList.add(local);
            }
            System.out.println("Citirea tuturor datelor din DBCard s-a efectuat cu succes!\n");
            return cardList;
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la citirea din DBCard.");
        } catch (CardException e) {
            throw new RuntimeException("Eroare la inserare datelor la citirea din DBCard.");
        }
    }

    public Card read(String cardNumber) {
        try (Connection connection = this.getConnection()) {
            Timestamp.timestamp("CardRepository,read");
            CardValidation.validateCardNumber(cardNumber);
            String query = "SELECT * FROM project.card WHERE cardNumber LIKE ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, cardNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            Card local;
            if (resultSet.next()) {
                local = new Card();
                local.setCardNumber(resultSet.getString(1));
                local.setPIN(resultSet.getInt(3));
                local.setIssueDate(resultSet.getString(4));
            } else
                throw new RuntimeException("Cardul cu nr " + cardNumber + " nu exista in DB.");
            System.out.println("Citirea cardului " + cardNumber + " din DBCard a avut succes!");
            return local;
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la citirea din DBCard.");
        } catch (CardException e) {
            throw new RuntimeException("Eroare la inserare datelor la citirea din DBCard.");
        }
    }

    protected List<Card> readByFK(String IBAN) {
        try (Connection connection = this.getConnection()) {
            Timestamp.timestamp("CardRepository,readByFK");
            BankAccountValidation.validateIBAN(IBAN);
            String query = "SELECT * from card WHERE IBAN LIKE ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, IBAN);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Card> cardList = new ArrayList<>();
            Card local;
            while (resultSet.next()) {
                local = new Card();
                local.setCardNumber(resultSet.getString(1));
                local.setPIN(resultSet.getInt(3));
                local.setIssueDate(resultSet.getString(4));
                cardList.add(local);
                System.out.println(" ~ Citirea cardului " + local.getCardNumber() + " din DBCard a avut succes!");
            }
            return cardList;
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la citirea din DBCard.");
        } catch (CardException e) {
            throw new RuntimeException("Eroare la inserare datelor la citirea din DBCard.");
        } catch (BankAccountException e) {
            throw new RuntimeException("Eroare validarea IBAN-ului la citirea din DBCard.");
        }
    }

    public void update(Card card) {
        try (Connection connection = this.getConnection()) {
            Timestamp.timestamp("CardRepository,update");
            String query = "UPDATE project.card SET PIN = ?, issueDate = ? WHERE cardNumber LIKE ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, card.getPIN());
            preparedStatement.setString(2, card.getIssueDate());
            preparedStatement.setString(3, card.getCardNumber());
            preparedStatement.executeUpdate();
            System.out.println("Update-ul cardului " + card.getCardNumber() + " din DBCard a avut succes!");
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la update-ul din DBCard.");
        }
    }

    public void update(String cardNumber, String IBAN) {
        try (Connection connection = this.getConnection()) {
            Timestamp.timestamp("CardRepository,update");
            BankAccountValidation.validateIBAN(IBAN);
            String query = "UPDATE project.card SET IBAN = ? WHERE cardNumber LIKE ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, IBAN);
            preparedStatement.setString(2, cardNumber);
            preparedStatement.executeUpdate();
            System.out.println("Transferul cardului " + cardNumber + " la contul " + IBAN + "a avut succes!");
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la transferul cardului din DBCard.");
        } catch (BankAccountException e) {
            throw new RuntimeException("Eroare validarea IBAN-ului la citirea din DBCard.");
        }
    }

    public void delete() {
        try (Connection connection = getConnection()) {
            Timestamp.timestamp("CardRepository,delete");
            String query = "DELETE from card;";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throw new RuntimeException("Nu se poate sterge din DBCard.");
        }
    }

    public void delete(String cardNumber) {
        try (Connection connection = getConnection()) {
            Timestamp.timestamp("CardRepository,delete");
            CardValidation.validateCardNumber(cardNumber);
            String query = "DELETE from card WHERE cardNumber LIKE ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, cardNumber);
            preparedStatement.executeUpdate();
            System.out.println("Stergerea cardului " + cardNumber + " din DBCard a avut succes!");
        } catch (SQLException throwables) {
            throw new RuntimeException("Nu se poate sterge din DBCard pe baza de cardNumber.");
        } catch (CardException e) {
            throw new RuntimeException("Eroare la inserare datelor la citirea din DBCard.");
        }
    }
}