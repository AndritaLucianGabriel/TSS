import mainClasses.*;
import operations.Provider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import service.dbResources.service.BankService;
import service.exceptions.*;
import service.files.WriterFiles;

import static junit.framework.TestCase.*;

public class BankTest {

    // Nu e nevoie de WriterFiles.getInstance().clearAllFolders() si BankService.getInstance().deleteAllData()
    // deoarece nu vom scrie nici in BD nici in dump-uri
    private String bankName = "Raiffeisen Bank";
    private String bankLocation = "O Strada Aproape De Casa";
    private Client bankClient = new Client("Ionita", "Dragos", 20, "5540522460057");
    private BankAccount bankBankAccount = new DebitAccount("RO59RZBR0000065122344800", "12-09-2021", "-", 20030, "Lei");
    private Loan bankLoan = new Loan(21410.3, "Lei", "Imprumut pentru nevoi personale", "03-12-2021", 60);
    private Bank bank;

    public BankTest() throws ClientException, BankAccountException, LoanException {}

    /*
     * FUNCTIONAL: Se va folosi partitionarea in categorii. Metoda are 2 intrari:
     * 1. Domeniul de intrari:
     * Nota: Nu se poate trimite null ca referinta pentru BankAccount deoarece parametrul trebuie sa implementeze acea
     * clasa abstracta (altfel primesc eroare la compilare -ambigous-)
     * !! Fie multimea conturilor existente ale bancii = P !!
     * -> un obiect ce implementeaza BankAccount | => N_1 = {x | x = DebitAccount ∈ P cu closingDate != "-"}
     *                                           |    N_2 = {x | x = DebitAccount ∈ P cu closingDate = "-"}
     *                                           |    N_3 = {x | x = SavingsAccount ∈ P cu closingDate != "-"}
     *                                           |    N_4 = {x | x = SavingsAccount ∈ P cu closingDate = "-"}
     *                                           |    N_5 = {x | x ∉ P}
     * -> un obiect de tip String                | => S_1 = {y | y = null}
     *                                           |    S_2 = {y | y ∈ {"Lei", "Euro", "Dolari"}}
     *                                           |    S_3 = {y | y ∉ {"Lei", "Euro", "Dolari"}}
     * 2. Domeniul de iesiri:
     * -> C_1(x, y) = {c | "Conversie realizata!"}
     * -> C_2(x, y) = {c | "Conversie esuata!"}
     * -> C_3(x, y) = {c | "Nu exista contul!"}
     * -> C_4(x, y) = {c | "Nu se poate face conversia! Contul a fost inchis!"}
     *
     * Clasele de echivalenta:
     * -> C_112 = {(x, y, c) | n ∈ N_1, y ∈ S_1, c ∈ C_2}
     * -> C_121 = {(x, y, c) | n ∈ N_1, y ∈ S_2, c ∈ C_1}
     * -> C_132 = {(x, y, c) | n ∈ N_1, y ∈ S_3, c ∈ C_2}
     *
     * -> C_214 = {(x, y, c) | n ∈ N_2, y ∈ S_1, c ∈ C_4}
     * -> C_224 = {(x, y, c) | n ∈ N_2, y ∈ S_2, c ∈ C_4}
     * -> C_234 = {(x, y, c) | n ∈ N_2, y ∈ S_3, c ∈ C_4}
     *
     * -> C_312 = {(x, y, c) | n ∈ N_3, y ∈ S_1, c ∈ C_2}
     * -> C_321 = {(x, y, c) | n ∈ N_3, y ∈ S_2, c ∈ C_1}
     * -> C_332 = {(x, y, c) | n ∈ N_3, y ∈ S_3, c ∈ C_2}
     *
     * -> C_414 = {(x, y, c) | n ∈ N_4, y ∈ S_1, c ∈ C_4}
     * -> C_424 = {(x, y, c) | n ∈ N_4, y ∈ S_2, c ∈ C_4}
     * -> C_434 = {(x, y, c) | n ∈ N_4, y ∈ S_3, c ∈ C_4}
     *
     * -> C_512 = {(x, y, c) | n ∈ N_5, y ∈ S_1, c ∈ C_3}
     * -> C_522 = {(x, y, c) | n ∈ N_5, y ∈ S_2, c ∈ C_3}
     * -> C_532 = {(x, y, c) | n ∈ N_5, y ∈ S_3, c ∈ C_3}
     *
     * author: Andrita Lucian-Gabriel
     */
    @Test
    public void testCurrencyExchange () throws BankAccountException, BankException {
        bank = new Bank(bankName, bankLocation, bankClient, bankBankAccount, bankClient, bankLoan);
        System.out.println(bank);
        BankAccount bankBankAccount = new DebitAccount("RO59RZBR0000065122344800", "12-09-2021", "-", 20030, "Lei");

        bank.currencyExchange(bankBankAccount, "Lei");
        System.out.println(bank);
    }

    /*
     * https://app.diagrams.net/#LinterBanking
     * STRUCTURAL: Se va folosi acoperirea la nivel de instructiune. Metoda are 3 intrari:
     * 1. Domeniul de intrari:
     * -> un obiect de tip String    | =>
     *                               |
     * -> un obiect de tip String    | =>
     *                               |
     * -> o primitiva de tip double  | =>
     *
     * author: Andrita Lucian-Gabriel
     */
    @Test
    public void testInterBanking () throws BankAccountException, BankException {

    }
}