package service.dbResources.service;

import mainClasses.Card;
import repository.CardRepository;
import service.Timestamp;

import java.util.List;

public class CardService {
    private final CardRepository cardRepository = new CardRepository();

    protected void create(Card card) {
        Timestamp.timestamp("CardService,create");
        cardRepository.create(card);
    }

    public void create(Card card, String IBAN) {
        Timestamp.timestamp("CardService,create");
        cardRepository.create(card, IBAN);
    }

    protected List<Object> read() {
        Timestamp.timestamp("CardService,read");
        return cardRepository.read();
    }

    protected Card read(String cardNumber) {
        Timestamp.timestamp("CardService,read");
        return cardRepository.read(cardNumber);
    }

    protected void update(Card card) {
        Timestamp.timestamp("CardService,update");
        cardRepository.update(card);
    }

    protected void update(String PK, String FK) {
        Timestamp.timestamp("CardService,update");
        cardRepository.update(PK, FK);
    }

    protected void delete() {
        Timestamp.timestamp("CardService,delete");
        cardRepository.delete();
    }

    protected void delete(String cardNumber) {
        Timestamp.timestamp("CardService,delete");
        cardRepository.delete(cardNumber);
    }
}
