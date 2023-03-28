package mainClasses;

import service.exceptions.CardException;
import service.Timestamp;
import service.validations.CardValidation;

import java.util.Objects;

public class Card {
    protected String cardNumber;
    protected int PIN;
    protected String issueDate;

    public Card() {
        this.cardNumber = "";
        this.issueDate = "";
    }

    public Card(String cardNumber, int PIN, String issueDate) throws CardException {
        CardValidation.validateCardNumber(cardNumber);
        CardValidation.validatePin(PIN);
        CardValidation.validateIssueDate(issueDate);

        this.cardNumber = cardNumber;
        this.PIN = PIN;
        this.issueDate = issueDate;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) throws CardException {
        CardValidation.validateCardNumber(cardNumber);
        this.cardNumber = cardNumber;
    }

    public int getPIN() {
        return PIN;
    }

    public void setPIN(int PIN) throws CardException {
        CardValidation.validatePin(PIN);
        this.PIN = PIN;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) throws CardException {
        CardValidation.validateIssueDate(issueDate);
        this.issueDate = issueDate;
    }

    protected String cardReaderUpdate() {
        Timestamp.timestamp("Card,cardReaderUpdate");
        return this.cardNumber + "," + this.PIN + "," + this.issueDate;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (this.getClass() != obj.getClass())
            return false;
        Card card = (Card) obj;
        if (!Objects.equals(this.cardNumber, card.cardNumber))
            return false;
        if (this.PIN != card.PIN)
            return false;
        return Objects.equals(this.issueDate, card.issueDate);
    }

    @Override
    public String toString() {
        StringBuilder c;
        c = new StringBuilder();
        c.append("Cardul ").append(this.cardNumber).append(" cu pinul ").append(this.PIN).append(" a fost emis la data de ").append(this.issueDate).append(".");
        return c.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.cardNumber, this.PIN, this.issueDate);
    }
}
