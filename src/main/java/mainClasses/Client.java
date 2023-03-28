package mainClasses;

import service.exceptions.ClientException;
import service.Timestamp;
import service.validations.ClientValidation;

import java.util.Objects;

public class Client {
    protected String firstName;
    protected String lastName;
    protected int age;
    protected String cnp;

    public Client() {
        this.firstName = "";
        this.lastName = "";
        this.age = 0;
        this.cnp = "";
    }

    public Client(String firstName, String lastName, int age, String cnp) throws ClientException {
        ClientValidation.validateFirstName(firstName);
        ClientValidation.validateLastName(lastName);
        ClientValidation.validateAge(age);
        ClientValidation.validateCnp(cnp);

        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.cnp = cnp;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public int getAge() {
        return this.age;
    }

    public String getCnp() {
        return this.cnp;
    }

    public void setFirstName(String firstName) throws ClientException {
        ClientValidation.validateFirstName(firstName);
        this.firstName = firstName;
    }

    public void setLastName(String lastName) throws ClientException {
        ClientValidation.validateLastName(lastName);
        this.lastName = lastName;
    }

    public void setAge(int age) throws ClientException {
        ClientValidation.validateAge(age);
        this.age = age;
    }

    public void setCnp(String cnp) throws ClientException {
        ClientValidation.validateCnp(cnp);
        this.cnp = cnp;
    }

    protected String clientReaderUpdate() {
        Timestamp.timestamp("Client,clientReaderUpdate");
        return this.firstName + "," + this.lastName + "," + this.age + "," + this.cnp;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (this.getClass() != obj.getClass())
            return false;
        Client client = (Client) obj;
        if (!Objects.equals(this.firstName, client.firstName))
            return false;
        if (!Objects.equals(this.lastName, client.lastName))
            return false;
        if (this.age != client.age)
            return false;
        return Objects.equals(this.cnp, client.cnp);
    }

    @Override
    public String toString() {
        StringBuilder c;
        c = new StringBuilder();
        c.append("\t").append(this.firstName).append(" ").append(this.lastName).append(" in varsta de ").append(this.age).append(" ani, CNP: ").append(this.cnp);
        return c.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.firstName, this.lastName, this.age, this.cnp);
    }

}