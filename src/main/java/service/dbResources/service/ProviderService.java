package service.dbResources.service;

import operations.Provider;
import repository.ProviderRepository;
import service.Timestamp;

import java.util.List;

public class ProviderService {
    private final ProviderRepository providerRepository = new ProviderRepository();

    protected void create(Provider provider) {
        Timestamp.timestamp("ProviderService,create");
        providerRepository.create(provider);
    }

    protected List<Object> read() {
        Timestamp.timestamp("ProviderService,read");
        return providerRepository.read();
    }

    protected Provider read(String IBAN) {
        Timestamp.timestamp("ProviderService,read");
        return providerRepository.read(IBAN);
    }

    protected void update(Provider provider) {
        Timestamp.timestamp("ProviderService,update");
        providerRepository.update(provider);
    }

    protected void delete() {
        Timestamp.timestamp("ProviderService,delete");
        providerRepository.delete();
    }

    protected void delete(String IBAN) {
        Timestamp.timestamp("ProviderService,delete");
        providerRepository.delete(IBAN);
    }
}
