package repository;

import mainClasses.Loan;
import service.Timestamp;
import service.exceptions.ClientException;
import service.exceptions.LoanException;
import service.validations.ClientValidation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoanRepository {
    private Connection getConnection() {
        try {
            Timestamp.timestamp("LoanRepository,getConnection");
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/project", "root", "root");
        } catch (SQLException throwables) {
            throw new RuntimeException("Nu se poate conecta la baza de date.");
        }
    }

    public void create(Loan loan, String cnp) {
        try (Connection connection = getConnection()) {
            Timestamp.timestamp("LoanRepository,create");
            String query = "INSERT into loan(id, cnp, value, currency, detail, date, durationMonths) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, loan.getLoanID());
            preparedStatement.setString(2, cnp);
            preparedStatement.setDouble(3, loan.getValue());
            preparedStatement.setString(4, loan.getCurrency());
            preparedStatement.setString(5, loan.getDetail());
            preparedStatement.setString(6, loan.getDate());
            preparedStatement.setInt(7, loan.getDurationMonths());
            preparedStatement.executeUpdate();
            System.out.println("Imprumutul cu id-ul " + loan.getLoanID() + " in valoare de " + loan.getValue() + " " + loan.getCurrency() + " a fost introdus in DBLoan cu succes!");
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new RuntimeException("Imprumutul " + loan.toString() + " este deja in DBLoan.");
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la inserarea imprumutului in DBLoan.");
        }
    }

    public List<Object> read() {
        try (Connection connection = this.getConnection()) {
            Timestamp.timestamp("LoanRepository,read");
            String query = "SELECT * FROM project.loan ORDER BY id ASC;";
            List<Object> loanList = new ArrayList<>();
            Loan local;
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                local = new Loan();
                local.setLoanID(resultSet.getInt(1));
                local.setValue(resultSet.getDouble(3));
                local.setCurrency(resultSet.getString(4));
                local.setDetail(resultSet.getString(5));
                local.setDate(resultSet.getString(6));
                local.setDurationMonths(resultSet.getInt(7));
                loanList.add(local);
            }
            System.out.println("Citirea tuturor datelor din DBLoan s-a efectuat cu succes!\n");
            return loanList;
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la citirea din DBLoan.");
        } catch (LoanException e) {
            throw new RuntimeException("Eroare la inserare datelor la citirea din DBLoan.");
        }
    }

    public Loan read(String cnp, String date) {
        try (Connection connection = this.getConnection()) {
            Timestamp.timestamp("LoanRepository,read");
            String query = "SELECT * FROM project.loan WHERE cnp LIKE ? and date LIKE ? ORDER BY id ASC;";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, cnp);
            preparedStatement.setString(2, date);
            ResultSet resultSet = preparedStatement.executeQuery();
            Loan local;
            if (resultSet.next()) {
                local = new Loan();
                local.setLoanID(resultSet.getInt(1));
                local.setValue(resultSet.getDouble(3));
                local.setCurrency(resultSet.getString(4));
                local.setDetail(resultSet.getString(5));
                local.setDate(resultSet.getString(6));
                local.setDurationMonths(resultSet.getInt(7));
            } else
                throw new RuntimeException("Imprumutul clientului " + cnp + " de la data " + date + " nu exista in DB.");
            System.out.println("Citirea imprumutului " + local.getLoanID() + " in valoare de " + local.getValue() + " " + local.getCurrency() + " din DBLoan a avut succes!");
            return local;
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la citirea din DBLoan.");
        } catch (LoanException e) {
            throw new RuntimeException("Eroare la inserare datelor la citirea din DBLoan.");
        }
    }

    protected List<Loan> readByFK(String cnp) {
        try (Connection connection = this.getConnection()) {
            Timestamp.timestamp("LoanRepository,readByFK");
            ClientValidation.validateCnp(cnp);
            String query = "SELECT id, value, currency, detail, date, durationMonths FROM project.loan WHERE cnp like ? ORDER BY id ASC;";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, cnp);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Loan> loanList = new ArrayList<>();
            Loan local;
            while (resultSet.next()) {
                local = new Loan();
                local.setLoanID(resultSet.getInt(1));
                local.setValue(resultSet.getDouble(2));
                local.setCurrency(resultSet.getString(3));
                local.setDetail(resultSet.getString(4));
                local.setDate(resultSet.getString(5));
                local.setDurationMonths(resultSet.getInt(6));
                loanList.add(local);
                System.out.println("Citirea imprumutului " + local.getLoanID() + " in valoare de " + local.getValue() + " " + local.getCurrency() + " din DBLoan a avut succes!");
            }
            return loanList;
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la citirea din DBLoan.");
        } catch (LoanException e) {
            throw new RuntimeException("Eroare la inserare datelor la citirea din DBLoan.");
        } catch (ClientException e) {
            throw new RuntimeException("Eroare validarea cnp-ului la citirea din DBLoan.");
        }
    }

    public void updateByFK(Loan loan, String FK) {
        try (Connection connection = this.getConnection()) {
            Timestamp.timestamp("LoanRepository,update");
            String query = "UPDATE project.loan SET id = ?, value = ?, currency = ?, detail = ?, durationMonths = ? WHERE cnp LIKE ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, loan.getLoanID());
            preparedStatement.setDouble(2, loan.getValue());
            preparedStatement.setString(3, loan.getCurrency());
            preparedStatement.setString(4, loan.getDetail());
            preparedStatement.setInt(5, loan.getDurationMonths());
            preparedStatement.setString(6, FK);
            preparedStatement.executeUpdate();
            System.out.println("Update-ul imprumutului in valoare de " + loan.getValue() + " " + loan.getCurrency() + " al clientului " + FK + " din DBLoan a avut succes!");
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la update-ul din DBLoan.");
        }
    }

    public void delete() {
        try (Connection connection = getConnection()) {
            Timestamp.timestamp("LoanRepository,delete");
            String query = "DELETE from loan;";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throw new RuntimeException("Nu se poate sterge din DBLoan.");
        }
    }

    public void delete(String cnp, String date) {
        try (Connection connection = getConnection()) {
            Timestamp.timestamp("LoanRepository,delete");
            String query = "DELETE from loan WHERE cnp LIKE ? and date LIKE ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, cnp);
            preparedStatement.setString(2, date);
            preparedStatement.executeUpdate();
            System.out.println("Stergerea imprumutului clientului " + cnp + " la data de " + date + " din DBLoan a avut succes!");
        } catch (SQLException throwables) {
            throw new RuntimeException("Nu se poate sterge din DBLoan pe baza de id.");
        }
    }

    protected void deleteByFK(String cnp) {
        try (Connection connection = getConnection()) {
            Timestamp.timestamp("LoanRepository,delete");
            String query = "DELETE from loan WHERE cnp LIKE ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, cnp);
            preparedStatement.executeUpdate();
            System.out.println("Stergerea imprumutului clientului " + cnp + " din DBLoan a avut succes!");
        } catch (SQLException throwables) {
            throw new RuntimeException("Nu se poate sterge din DBLoan pe baza de id.");
        }
    }
}
