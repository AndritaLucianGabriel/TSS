package repository;

import mainClasses.BankAccount;
import mainClasses.Card;
import mainClasses.DebitAccount;
import mainClasses.SavingsAccount;
import service.Timestamp;
import service.exceptions.BankAccountException;
import service.exceptions.ClientException;
import service.validations.BankAccountValidation;
import service.validations.ClientValidation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BankAccountRepository {
    private final CardRepository cardRepository = new CardRepository();

    private Connection getConnection() {
        try {
            Timestamp.timestamp("BankAccountRepository,getConnection");
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/project", "root", "root");
        } catch (SQLException throwables) {
            throw new RuntimeException("Nu se poate conecta la baza de date.");
        }
    }

    public void create(BankAccount bankAccount) {
        try (Connection connection = getConnection()) {
            Timestamp.timestamp("BankAccountRepository,create");
            String query = "INSERT into bankAccount(id, IBAN, cnp, openingDate, closingDate, balance, currency, annualInterestRate) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, bankAccount.getBankAccountID());
            preparedStatement.setString(2, bankAccount.getIBAN());
            preparedStatement.setNull(3, Types.VARCHAR);
            preparedStatement.setString(4, bankAccount.getOpeningDate());
            preparedStatement.setString(5, bankAccount.getClosingDate());
            preparedStatement.setDouble(6, bankAccount.getBalance());
            preparedStatement.setString(7, bankAccount.getCurrency());
            if (bankAccount instanceof SavingsAccount)
                preparedStatement.setDouble(8, ((SavingsAccount) bankAccount).getAnnualInterestRate());
            else
                preparedStatement.setDouble(8, 0);
            preparedStatement.executeUpdate();
            for (Card x : bankAccount.getCardList()) {
                cardRepository.create(x, bankAccount.getIBAN());
            }
            System.out.println("Contul " + bankAccount.getIBAN() + " a fost introdus in DBBankAccount cu succes!");
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new RuntimeException("Contul " + bankAccount.getIBAN() + " este deja in DBBankAccount.");
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la inserarea imprumutului in DBBankAccount.");
        }
    }

    public void create(BankAccount bankAccount, String cnp) {
        try (Connection connection = getConnection()) {
            Timestamp.timestamp("BankAccountRepository,create");
            String query = "INSERT into bankAccount(id, IBAN, cnp, openingDate, closingDate, balance, currency, annualInterestRate) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, bankAccount.getBankAccountID());
            preparedStatement.setString(2, bankAccount.getIBAN());
            preparedStatement.setString(3, cnp);
            preparedStatement.setString(4, bankAccount.getOpeningDate());
            preparedStatement.setString(5, bankAccount.getClosingDate());
            preparedStatement.setDouble(6, bankAccount.getBalance());
            preparedStatement.setString(7, bankAccount.getCurrency());
            if (bankAccount instanceof SavingsAccount)
                preparedStatement.setDouble(8, ((SavingsAccount) bankAccount).getAnnualInterestRate());
            else
                preparedStatement.setDouble(8, 0);
            preparedStatement.executeUpdate();
            System.out.println(" Contul " + bankAccount.getIBAN() + " a fost introdus in DBBankAccount cu succes!");
            for (Card x : bankAccount.getCardList()) {
                cardRepository.create(x, bankAccount.getIBAN());
            }
            System.out.println();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new RuntimeException("Contul " + bankAccount.getIBAN() + " este deja in DBBankAccount.");
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la inserarea imprumutului in DBBankAccount.");
        }
    }

    public List<Object> read() {
        try (Connection connection = this.getConnection()) {
            Timestamp.timestamp("BankAccountRepository,read");
            String query = "SELECT * FROM project.bankaccount ORDER BY id ASC;";
            List<Object> bankAccountList = new ArrayList<>();
            BankAccount local;
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                if (Double.parseDouble(resultSet.getString(8)) == 0) {
                    local = new DebitAccount();
                    local.setBankAccountID(resultSet.getInt(1));
                    local.setIBAN(resultSet.getString(2));
                    local.setOpeningDate(resultSet.getString(4));
                    local.setClosingDate(resultSet.getString(5));
                    local.setBalance(Double.parseDouble(resultSet.getString(6)));
                    local.setCurrency(resultSet.getString(7));
                } else {
                    local = new SavingsAccount();
                    local.setBankAccountID(resultSet.getInt(1));
                    local.setIBAN(resultSet.getString(2));
                    local.setOpeningDate(resultSet.getString(4));
                    local.setClosingDate(resultSet.getString(5));
                    local.setBalance(Double.parseDouble(resultSet.getString(6)));
                    local.setCurrency(resultSet.getString(7));
                    local.setAnnualInterestRate(resultSet.getDouble(8));
                }
                bankAccountList.add(local);
            }
            System.out.println("Citirea tuturor datelor din DBBankAccount s-a efectuat cu succes!\n");
            return bankAccountList;
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la citirea din DBBankAccount.");
        } catch (BankAccountException e) {
            throw new RuntimeException("Eroare la inserare datelor la citirea din DBBankAccount.");
        }
    }

    public BankAccount read(String IBAN) {
        try (Connection connection = this.getConnection()) {
            Timestamp.timestamp("BankAccountRepository,read");
            BankAccountValidation.validateIBAN(IBAN);
            String query = "SELECT * FROM project.bankaccount WHERE IBAN LIKE ? ORDER BY id ASC;";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, IBAN);
            ResultSet resultSet = preparedStatement.executeQuery();
            BankAccount local;
            if (resultSet.next()) {
                if (Double.parseDouble(resultSet.getString(8)) == 0) {
                    local = new DebitAccount();
                    local.setBankAccountID(resultSet.getInt(1));
                    local.setIBAN(resultSet.getString(2));
                    local.setOpeningDate(resultSet.getString(4));
                    local.setClosingDate(resultSet.getString(5));
                    local.setBalance(Double.parseDouble(resultSet.getString(6)));
                    local.setCurrency(resultSet.getString(7));
                } else {
                    local = new SavingsAccount();
                    local.setBankAccountID(resultSet.getInt(1));
                    local.setIBAN(resultSet.getString(2));
                    local.setOpeningDate(resultSet.getString(4));
                    local.setClosingDate(resultSet.getString(5));
                    local.setBalance(Double.parseDouble(resultSet.getString(6)));
                    local.setCurrency(resultSet.getString(7));
                    local.setAnnualInterestRate(resultSet.getDouble(8));
                }
            } else
                throw new RuntimeException("Contul cu IBAN-ul " + IBAN + " nu exista in DBBankAccount.");
            System.out.println("Citirea contului " + local.getIBAN() + " din DBBankAccount a avut succes!");
            return local;
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la citirea din DBBankAccount.");
        } catch (BankAccountException e) {
            throw new RuntimeException("Eroare la inserare datelor la citirea din DBBankAccount.");
        }
    }

    protected List<BankAccount> readByFK(String cnp) {
        try (Connection connection = this.getConnection()) {
            Timestamp.timestamp("BankAccountRepository,readByFK");
            ClientValidation.validateCnp(cnp);
            String query = "SELECT id, IBAN, cnp, openingDate, closingDate, balance, currency, annualInterestRate FROM project.bankaccount WHERE cnp LIKE ? ORDER BY id ASC;";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, cnp);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<BankAccount> bankAccountList = new ArrayList<>();
            BankAccount local;
            while (resultSet.next()) {
                if (Double.parseDouble(resultSet.getString(8)) == 0) {
                    local = new DebitAccount();
                    local.setBankAccountID(resultSet.getInt(1));
                    local.setIBAN(resultSet.getString(2));
                    local.setOpeningDate(resultSet.getString(4));
                    local.setClosingDate(resultSet.getString(5));
                    local.setBalance(Double.parseDouble(resultSet.getString(6)));
                    local.setCurrency(resultSet.getString(7));
                } else {
                    local = new SavingsAccount();
                    local.setBankAccountID(resultSet.getInt(1));
                    local.setIBAN(resultSet.getString(2));
                    local.setOpeningDate(resultSet.getString(4));
                    local.setClosingDate(resultSet.getString(5));
                    local.setBalance(Double.parseDouble(resultSet.getString(6)));
                    local.setCurrency(resultSet.getString(7));
                    local.setAnnualInterestRate(resultSet.getDouble(8));
                }
                local.setCardList(cardRepository.readByFK(local.getIBAN()));
                bankAccountList.add(local);
                System.out.println(" Citirea contului " + local.getIBAN() + " din DBBankAccount a avut succes!");
            }
            return bankAccountList;
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la citirea din DBBankAccount.");
        } catch (BankAccountException e) {
            throw new RuntimeException("Eroare la inserare datelor la citirea din DBBankAccount.");
        } catch (ClientException e) {
            throw new RuntimeException("Eroare validarea cnp-ului la citirea din DBBankAccount.");
        }
    }

    public void update(BankAccount bankAccount) {
        try (Connection connection = this.getConnection()) {
            Timestamp.timestamp("BankAccountRepository,update");
            String query = "UPDATE project.bankaccount SET id = ?, openingDate = ?, closingDate = ?, balance = ?, currency = ?, annualInterestRate = ? WHERE IBAN LIKE ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, bankAccount.getBankAccountID());
            preparedStatement.setString(2, bankAccount.getOpeningDate());
            preparedStatement.setString(3, bankAccount.getClosingDate());
            preparedStatement.setDouble(4, bankAccount.getBalance());
            preparedStatement.setString(5, bankAccount.getCurrency());
            if (bankAccount instanceof SavingsAccount)
                preparedStatement.setDouble(6, ((SavingsAccount) bankAccount).getAnnualInterestRate());
            else
                preparedStatement.setDouble(6, 0);
            preparedStatement.setString(7, bankAccount.getIBAN());
            preparedStatement.executeUpdate();
            System.out.println("Update-ul contului " + bankAccount.getIBAN() + " din DBBankAccount a avut succes!");
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la update-ul din DBBankAccount.");
        }
    }

    public void update(String IBAN, String cnp) {
        try (Connection connection = this.getConnection()) {
            Timestamp.timestamp("BankAccountRepository,update");
            String query = "UPDATE project.bankaccount SET cnp = ? WHERE IBAN LIKE ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, cnp);
            preparedStatement.setString(2, IBAN);
            preparedStatement.executeUpdate();
            System.out.println("Transferul contului " + IBAN + " la clientul " + cnp + " a avut succes!");
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la transferul contului din DBBankAccount.");
        }
    }

    public void setBalance(double balance, String IBAN) {
        try (Connection connection = this.getConnection()) {
            Timestamp.timestamp("BankAccountRepository,setBalance");
            String query = "UPDATE project.bankaccount SET balance = ? WHERE IBAN LIKE ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setDouble(1, balance);
            preparedStatement.setString(2, IBAN);
            preparedStatement.executeUpdate();
            System.out.println("Balanta contului " + IBAN + " din DBBankAccount a fost modificata cu " + balance + "!");
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la transferul contului din DBBankAccount.");
        }
    }

    public void setCurrency(String currency, String IBAN) {
        try (Connection connection = this.getConnection()) {
            Timestamp.timestamp("BankAccountRepository,setBalance");
            String query = "UPDATE project.bankaccount SET currency = ? WHERE IBAN LIKE ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, currency);
            preparedStatement.setString(2, IBAN);
            preparedStatement.executeUpdate();
            System.out.println("Valuta contului " + IBAN + " din DBBankAccount a fost modificata in " + currency + "!");
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la transferul contului din DBBankAccount.");
        }
    }

    public void delete() {
        try (Connection connection = getConnection()) {
            Timestamp.timestamp("BankAccountRepository,delete");
            String query = "DELETE from bankAccount;";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throw new RuntimeException("Nu se poate sterge din DBbankAccount.");
        }
    }

    public void delete(String IBAN) {
        try (Connection connection = getConnection()) {
            Timestamp.timestamp("BankAccountRepository,delete");
            BankAccountValidation.validateIBAN(IBAN);
            String query = "DELETE from bankaccount WHERE IBAN like ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, IBAN);
            preparedStatement.executeUpdate();
            System.out.println("Stergerea contului " + IBAN + " din DBBankAccount a avut succes!");
        } catch (SQLException throwables) {
            throw new RuntimeException("Nu se poate sterge din DBBankAccount pe baza de IBAN.");
        } catch (BankAccountException e) {
            throw new RuntimeException("Eroare la inserare datelor la citirea din DBBankAccount.");
        }
    }

    protected void deleteByFK(String FK) {
        try (Connection connection = getConnection()) {
            Timestamp.timestamp("BankAccountRepository,deleteByFK");
            ClientValidation.validateCnp(FK);
            String query = "DELETE from project.bankaccount WHERE cnp like ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, FK);
            preparedStatement.executeUpdate();
            System.out.println("Stergerea contului clientului " + FK + " din DBBankAccount a avut succes!");
        } catch (SQLException throwables) {
            throw new RuntimeException("Nu se poate sterge din DBBankAccount pe baza de IBAN.");
        } catch (ClientException e) {
            throw new RuntimeException("Eroare la inserare datelor la citirea din DBBankAccount.");
        }
    }
}
