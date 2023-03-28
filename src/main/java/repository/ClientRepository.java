package repository;

import mainClasses.Client;
import service.Timestamp;
import service.dbResources.service.BankService;
import service.exceptions.ClientException;
import service.validations.ClientValidation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientRepository {
    private Connection getConnection() {
        try {
            Timestamp.timestamp("ClientRepository,getConnection");
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/project", "root", "root");
        } catch (SQLException throwables) {
            throw new RuntimeException("Nu se poate conecta la baza de date.");
        }
    }

    private Boolean verifyIsThere(Client client) {
        try (Connection connection = getConnection()) {
            Timestamp.timestamp("ClientRepository,verifyIsThere");
            String query = "SELECT count(*) FROM project.client WHERE cnp LIKE ?";
            int counter;
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, client.getCnp());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                counter = resultSet.getInt(1);
                return counter != 0;
            }
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new RuntimeException("Clientul " + client.getFirstName() + " " + client.getLastName() + " este deja in DBClient.");
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la inserarea imprumutului in DBClient.");
        }
    }

    private Boolean hasProducts(String cnp) {
        try (Connection connection = getConnection()) {
            Timestamp.timestamp("ClientRepository,hasproducts");
            String queryBankAccount = "SELECT count(*) FROM project.bankaccount WHERE cnp LIKE ?;";
            String queryLoan = "SELECT count(*) FROM project.loan WHERE cnp LIKE ?;";
            int counterBankAccount = 0;
            int counterLoan;
            PreparedStatement preparedStatement = connection.prepareStatement(queryBankAccount);
            preparedStatement.setString(1, cnp);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                counterBankAccount = resultSet.getInt(1);
            }
            PreparedStatement preparedStatement1 = connection.prepareStatement(queryLoan);
            preparedStatement1.setString(1, cnp);
            ResultSet resultSet1 = preparedStatement1.executeQuery();
            if (resultSet1.next()) {
                counterLoan = resultSet1.getInt(1);
                System.out.println("Counter: " + (counterBankAccount + counterLoan));
                return (counterBankAccount + counterLoan) != 0;
            }
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new RuntimeException("Clientul " + cnp + " este deja in DBClient.");
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la inserarea imprumutului in DBClient.");
        }
    }

    public void create(Client client) {
        try (Connection connection = getConnection()) {
            Timestamp.timestamp("ClientRepository,create");
            String query = "INSERT into client(firstName, lastName, age, cnp, bankID) VALUES (?, ?, ?, ?, ?)";
            if (!verifyIsThere(client)) {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, client.getFirstName());
                preparedStatement.setString(2, client.getLastName());
                preparedStatement.setInt(3, client.getAge());
                preparedStatement.setString(4, client.getCnp());
                preparedStatement.setNull(5, Types.INTEGER);
                preparedStatement.executeUpdate();
                System.out.println("Clientul " + client.getFirstName() + " " + client.getLastName() + " a fost introdus in DBClient cu succes!");
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la inserarea imprumutului in DBClient.");
        }
    }

    public void create(Client client, int bankID) {
        try (Connection connection = getConnection()) {
            Timestamp.timestamp("ClientRepository,create");
            String query = "INSERT into client(firstName, lastName, age, cnp, bankID) VALUES (?, ?, ?, ?, ?)";
            if (!verifyIsThere(client)) {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, client.getFirstName());
                preparedStatement.setString(2, client.getLastName());
                preparedStatement.setInt(3, client.getAge());
                preparedStatement.setString(4, client.getCnp());
                preparedStatement.setInt(5, bankID);
                preparedStatement.executeUpdate();
                System.out.println("Clientul " + client.getFirstName() + " " + client.getLastName() + " a fost introdus in DBClient cu succes!");
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la inserarea imprumutului in DBClient.");
        }
    }

    public List<Object> read() {
        try (Connection connection = this.getConnection()) {
            Timestamp.timestamp("ClientRepository,read");
            String query = "SELECT * FROM project.client ORDER BY cnp ASC;";
            List<Object> clientList = new ArrayList<>();
            Client local;
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                local = new Client();
                local.setFirstName(resultSet.getString(1));
                local.setLastName(resultSet.getString(2));
                local.setAge(resultSet.getInt(3));
                local.setCnp(resultSet.getString(4));
                clientList.add(local);
            }
            System.out.println("Citirea tuturor datelor din DBClient s-a efectuat cu succes!\n");
            return clientList;
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la citirea din DBClient.");
        } catch (ClientException e) {
            throw new RuntimeException("Eroare la inserare datelor la citirea din DBClient.");
        }
    }

    public Client read(String cnp) {
        try (Connection connection = this.getConnection()) {
            Timestamp.timestamp("ClientRepository,read");
            ClientValidation.validateCnp(cnp);
            String query = "SELECT * FROM project.client WHERE cnp LIKE ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, cnp);
            ResultSet resultSet = preparedStatement.executeQuery();
            Client local;
            if (resultSet.next()) {
                local = new Client();
                local.setFirstName(resultSet.getString(1));
                local.setLastName(resultSet.getString(2));
                local.setAge(resultSet.getInt(3));
                local.setCnp(resultSet.getString(4));
            } else
                throw new RuntimeException("Clientul cu CNP-il " + cnp + " nu exista in DB.");
            System.out.println("Citirea clientului " + local.getFirstName() + " " + local.getLastName() + " din DBClient a avut succes!");
            return local;
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la citirea din DBClient.");
        } catch (ClientException e) {
            throw new RuntimeException("Eroare la inserare datelor la citirea din DBClient.");
        }
    }

    protected List<Client> readByFK(int bankID) {
        try (Connection connection = this.getConnection()) {
            Timestamp.timestamp("ClientRepository,readByFK");
            String query = "SELECT firstName, lastName, age, cnp FROM project.client WHERE bankID = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, bankID);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Client> clientList = new ArrayList<>();
            Client local;
            while (resultSet.next()) {
                local = new Client();
                local.setFirstName(resultSet.getString(1));
                local.setLastName(resultSet.getString(2));
                local.setAge(resultSet.getInt(3));
                local.setCnp(resultSet.getString(4));
                clientList.add(local);
                System.out.println("Citirea clientului " + local.getFirstName() + " " + local.getLastName() + " din DBClient a avut succes!");
            }
            return clientList;
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la citirea din DBClient.");
        } catch (ClientException e) {
            throw new RuntimeException("Eroare la inserare datelor la citirea din DBClient.");
        }
    }

    public void update(Client client) {
        try (Connection connection = this.getConnection()) {
            Timestamp.timestamp("ClientRepository,update");
            String query = "UPDATE project.client SET firstName = ?, lastName = ?, age = ? WHERE CNP LIKE ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, client.getFirstName());
            preparedStatement.setString(2, client.getLastName());
            preparedStatement.setInt(3, client.getAge());
            preparedStatement.setString(4, client.getCnp());
            preparedStatement.executeUpdate();
            System.out.println("Update-ul clientului " + client.getFirstName() + " " + client.getLastName() + " din DBClient a avut succes!");
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la update-ul din DBClient.");
        }
    }

    public void update(String CNP, int bankID) {
        try (Connection connection = this.getConnection()) {
            Timestamp.timestamp("ClientRepository,update");
            String query = "UPDATE project.client SET bankID = ? WHERE CNP LIKE ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, bankID);
            preparedStatement.setString(2, CNP);
            preparedStatement.executeUpdate();
            System.out.println("Transferul clientului cu cnp-ul " + CNP + " la banca cu id-ul " + bankID + " a avut succes!");
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la update-ul din DBClient.");
        }
    }

    public void delete() {
        try (Connection connection = getConnection()) {
            Timestamp.timestamp("ClientRepository,delete");
            String query = "DELETE from client;";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throw new RuntimeException("Nu se poate sterge din DBClient.");
        }
    }

    public void delete(String cnp) {
        try (Connection connection = getConnection()) {
            Timestamp.timestamp("ClientRepository,delete");
            ClientValidation.validateCnp(cnp);
            if (!hasProducts(cnp)) {
                String query = "DELETE from client WHERE cnp like ?;";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, cnp);
                preparedStatement.executeUpdate();
                System.out.println("Stergerea clientului cu cnp-ul " + cnp + " din DBClient a avut succes!");
            }
        } catch (SQLException throwables) {
            throw new RuntimeException("Nu se poate sterge din DBClient pe baza de cnp.");
        } catch (ClientException e) {
            throw new RuntimeException("Eroare la inserare datelor la citirea din DBClient.");
        }
    }

    public void deleteCheckBankAccount(String cnp) {
        try (Connection connection = getConnection()) {
            Timestamp.timestamp("ClientRepository,deleteCheckBankAccount");
            ClientValidation.validateCnp(cnp);
            String queryBankAccount = "SELECT count(*) FROM project.bankaccount WHERE cnp LIKE ?;";
            int counterBankAccount;
            PreparedStatement preparedStatement = connection.prepareStatement(queryBankAccount);
            preparedStatement.setString(1, cnp);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                counterBankAccount = resultSet.getInt(1);
                if (counterBankAccount == 0) {
                    String query = "DELETE from client WHERE cnp like ?;";
                    PreparedStatement preparedStatement1 = connection.prepareStatement(query);
                    preparedStatement1.setString(1, cnp);
                    preparedStatement1.executeUpdate();
                    System.out.println("Stergerea clientului cu cnp-ul " + cnp + " din DBClient a avut succes!");
                } else {
                    LoanRepository loanRepository = new LoanRepository();
                    loanRepository.deleteByFK(cnp);
                    System.out.println("Stergerea imprumuturilor clientului cu cnp-ul " + cnp + " din DBClient a avut succes!");
                }
            }
        } catch (SQLException throwables) {
            throw new RuntimeException("Nu se poate sterge din DBClient pe baza de cnp.");
        } catch (ClientException e) {
            throw new RuntimeException("Eroare la inserare datelor la citirea din DBClient.");
        }
    }

    public void deleteCheckLoan(String cnp) {
        try (Connection connection = getConnection()) {
            Timestamp.timestamp("ClientRepository,deleteCheckBankAccount");
            ClientValidation.validateCnp(cnp);
            String queryLoan = "SELECT count(*) FROM project.loan WHERE cnp LIKE ?;";
            int counterLoan;
            PreparedStatement preparedStatement = connection.prepareStatement(queryLoan);
            preparedStatement.setString(1, cnp);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                counterLoan = resultSet.getInt(1);
                if (counterLoan == 0) {
                    String query = "DELETE from client WHERE cnp like ?;";
                    PreparedStatement preparedStatement1 = connection.prepareStatement(query);
                    preparedStatement1.setString(1, cnp);
                    preparedStatement1.executeUpdate();
                    System.out.println("Stergerea clientului cu cnp-ul " + cnp + " din DBClient a avut succes!");
                } else {
                    BankAccountRepository bankAccountRepository = new BankAccountRepository();
                    bankAccountRepository.deleteByFK(cnp);
                    System.out.println("Stergerea imprumuturilor clientului cu cnp-ul " + cnp + " din DBClient a avut succes!");
                }
            }
        } catch (SQLException throwables) {
            throw new RuntimeException("Nu se poate sterge din DBClient pe baza de cnp.");
        } catch (ClientException e) {
            throw new RuntimeException("Eroare la inserare datelor la citirea din DBClient.");
        }
    }
}