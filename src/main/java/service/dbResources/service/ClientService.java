package service.dbResources.service;

import mainClasses.Client;
import repository.ClientRepository;
import service.Timestamp;

import java.util.List;

public class ClientService {
    private final ClientRepository clientRepository = new ClientRepository();

    protected void create(Client client) {
        Timestamp.timestamp("ClientService,create");
        clientRepository.create(client);
    }

    protected void create(Client client, int bankID) {
        Timestamp.timestamp("ClientService,create");
        clientRepository.create(client, bankID);
    }

    protected List<Object> read() {
        Timestamp.timestamp("ClientService,read");
        return clientRepository.read();
    }

    protected Client read(String cnp) {
        Timestamp.timestamp("ClientService,read");
        return clientRepository.read(cnp);
    }

    protected void update(Client client) {
        Timestamp.timestamp("ClientService,update");
        clientRepository.update(client);
    }

    protected void update(String PK, String FK) {
        Timestamp.timestamp("ClientService,update");
        clientRepository.update(PK, Integer.parseInt(FK));
    }

    protected void delete() {
        Timestamp.timestamp("ClientService,delete");
        clientRepository.delete();
    }

    protected void delete(String cnp) {
        Timestamp.timestamp("ClientService,delete");
        clientRepository.delete(cnp);
    }

    protected void deleteCheckBankAccount(String cnp) {
        Timestamp.timestamp("ClientService,deleteCheckBankAccount");
        clientRepository.deleteCheckBankAccount(cnp);
    }

    protected void deleteCheckLoan(String cnp) {
        Timestamp.timestamp("ClientService,deleteCheckLoan");
        clientRepository.deleteCheckLoan(cnp);
    }
}
