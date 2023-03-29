package tests;

import mainClasses.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import service.CurrencyExchange;
import service.FormatDouble;
import service.exceptions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;

public class gabi {

    // Nu e nevoie de WriterFiles.getInstance().clearAllFolders() si BankService.getInstance().deleteAllData()
    // deoarece nu vom scrie nici in BD nici in dump-uri
    private final BankAccount debitAccount1 = new DebitAccount("RO59RZBR0000065122344800", "12-09-2021", "-", 20030, "Lei");
    private final BankAccount debitAccount2 = new DebitAccount("RO59RZBR0000068222375802", "15-05-2012", "20-07-2018", 12312, "Dolari");
    private final BankAccount savingsAccount1 = new SavingsAccount("RO69RZBR0000068222375804", "15-05-2000", "-", 12312, "Dolari");
    private final BankAccount savingsAccount2 = new SavingsAccount("RO59INGB0000062522326801", "25-07-2019", "12-09-2021", 1000, "Dolari", 3.3);
    private final Loan bankLoan = new Loan(21410.3, "Lei", "Imprumut pentru nevoi personale", "03-12-2021", 60);
    private final Client bankClient = new Client("Ionita", "Dragos", 20, "5540522460057");
    private Bank bank;

    private final BankAccount nonExistingAccount = new DebitAccount("RO69RZBR0000067142375123", "25-07-2019", "20-07-2018", 40568, "Euro");

    // Pt verifica de System.out
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    private double convert(BankAccount account, double value, String baseCurrency, String wantedCurrency) {
        Integer[] local;
        if(wantedCurrency == null || !account.getClosingDate().equals("-")) {
            // Practic nu se face nici un transfer
            local = CurrencyExchange.searchByCurrency(baseCurrency, baseCurrency);
        }
        else {
            local = CurrencyExchange.searchByCurrency(baseCurrency, wantedCurrency);
        }
        // Daca nu exista currency ul cerut, nu se schimba nimic
        if(local[0] != -1 && local[1] != -1) {
            return value * CurrencyExchange.exchangeRates[local[0]][local[1]];
        }
        return value;
    }

    public gabi() throws BankAccountException, LoanException, ClientException {
    }

    private BankAccount getBankAccountFromMap(BankAccount bankAccount, Client client, Map<Client, List<BankAccount>> map) {
        for (Map.Entry<Client, List<BankAccount>> x : map.entrySet()) {
            if (x.getKey().equals(client)) {
                List<BankAccount> accounts = x.getValue();
                for (BankAccount y : accounts) {
                    if (Objects.equals(y.getIBAN(), bankAccount.getIBAN())) {
                        return y;
                    }
                }
            }
        }
        // Va returna contul; aici mereu va fi nemodificat ptc nu va exista in banca
        return bankAccount;
    }

    @Before
    public void SetUp() throws  BankException {
        String bankName = "Raiffeisen Bank";
        String bankLocation = "O Strada Aproape De Casa";

        List<BankAccount> accountsList = new ArrayList<>();
        accountsList.add(debitAccount1);
        accountsList.add(debitAccount2);
        accountsList.add(savingsAccount1);
        accountsList.add(savingsAccount2);

        List<Loan> loanList = new ArrayList<>();
        loanList.add(bankLoan);

        bank = new Bank(bankName, bankLocation, bankClient, accountsList, bankClient, loanList);
        System.setOut(new PrintStream(outputStreamCaptor));
    }

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
     * -> C_513 = {(x, y, c) | n ∈ N_5, y ∈ S_1, c ∈ C_3}
     * -> C_523 = {(x, y, c) | n ∈ N_5, y ∈ S_2, c ∈ C_3}
     * -> C_533 = {(x, y, c) | n ∈ N_5, y ∈ S_3, c ∈ C_3}
     *
     * author: Andrita Lucian-Gabriel
     */
    @Test
    public void testCurrencyExchange_C112 () throws BankAccountException {
        //Integrity checks
        BankAccount tmp = getBankAccountFromMap(debitAccount1, bankClient, bank.getClientBankAccountMap());
        Assert.assertEquals(debitAccount1, tmp);

        // Prepping for the final checks
        String wantedCurrency = null;
        double valueAfterConversion = convert(debitAccount1, debitAccount1.getBalance(), debitAccount1.getCurrency(), wantedCurrency);

        // Actual call
        bank.currencyExchange(debitAccount1, wantedCurrency);

        // Retrieving the modified account from the bank
        tmp = getBankAccountFromMap(debitAccount1, bankClient, bank.getClientBankAccountMap());

        // Verify the accounts' updated information
        Assert.assertEquals(debitAccount1.getCurrency(), tmp.getCurrency());
        Assert.assertEquals(valueAfterConversion, tmp.getBalance(), Math.ulp(valueAfterConversion));

        // Check the messages. The logs are really detailed so we have to check if our string is in them
        Assert.assertTrue(outputStreamCaptor.toString().trim().contains("Conversie esuata!"));
    }

    @Test
    public void testCurrencyExchange_C121 () throws BankAccountException {
        //Integrity checks
        BankAccount tmp = getBankAccountFromMap(debitAccount1, bankClient, bank.getClientBankAccountMap());
        Assert.assertEquals(debitAccount1, tmp);

        // Prepping for the final checks
        String wantedCurrency = "Euro";
        double valueAfterConversion = convert(debitAccount1, debitAccount1.getBalance(), debitAccount1.getCurrency(), wantedCurrency);

        // Actual call
        bank.currencyExchange(debitAccount1, wantedCurrency);

        // Retrieving the modified account from the bank
        tmp = getBankAccountFromMap(debitAccount1, bankClient, bank.getClientBankAccountMap());

        // Verify the accounts' updated information
        Assert.assertEquals(wantedCurrency, tmp.getCurrency());
        Assert.assertEquals(valueAfterConversion, tmp.getBalance(), Math.ulp(valueAfterConversion));

        // Check the messages. The logs are really detailed so we have to check if our string is in them
        Assert.assertTrue(outputStreamCaptor.toString().trim().contains("Conversie realizata! (Lei->Euro)"));
    }

    @Test
    public void testCurrencyExchange_C132 () throws BankAccountException {
        //Integrity checks
        BankAccount tmp = getBankAccountFromMap(debitAccount1, bankClient, bank.getClientBankAccountMap());
        Assert.assertEquals(debitAccount1, tmp);

        // Prepping for the final checks
        String wantedCurrency = "Yen";
        double valueAfterConversion = convert(debitAccount1, debitAccount1.getBalance(), debitAccount1.getCurrency(), wantedCurrency);

        // Actual call
        bank.currencyExchange(debitAccount1, wantedCurrency);

        // Retrieving the modified account from the bank
        tmp = getBankAccountFromMap(debitAccount1, bankClient, bank.getClientBankAccountMap());

        // Verify the accounts' updated information
        Assert.assertEquals(debitAccount1.getCurrency(), tmp.getCurrency());
        Assert.assertEquals(valueAfterConversion, tmp.getBalance(), Math.ulp(valueAfterConversion));

        // Check the messages. The logs are really detailed so we have to check if our string is in them
        Assert.assertTrue(outputStreamCaptor.toString().trim().contains("Conversie esuata!"));
    }

    @Test
    public void testCurrencyExchange_C214 () throws BankAccountException {
        //Integrity checks
        BankAccount tmp = getBankAccountFromMap(debitAccount2, bankClient, bank.getClientBankAccountMap());
        Assert.assertEquals(debitAccount2, tmp);

        // Prepping for the final checks
        String wantedCurrency = null;
        double valueAfterConversion = convert(debitAccount2, debitAccount2.getBalance(), debitAccount2.getCurrency(), wantedCurrency);

        // Actual call
        bank.currencyExchange(debitAccount2, wantedCurrency);

        // Retrieving the modified account from the bank
        tmp = getBankAccountFromMap(debitAccount2, bankClient, bank.getClientBankAccountMap());

        // Verify the accounts' updated information
        Assert.assertEquals(debitAccount2.getCurrency(), tmp.getCurrency());
        Assert.assertEquals(valueAfterConversion, tmp.getBalance(), Math.ulp(valueAfterConversion));

        // Check the messages. The logs are really detailed so we have to check if our string is in them
        Assert.assertTrue(outputStreamCaptor.toString().trim().contains("Nu se poate face conversia! Contul " + tmp.getIBAN() + " a fost inchis!"));
    }

    @Test
    public void testCurrencyExchange_C224 () throws BankAccountException {
        //Integrity checks
        BankAccount tmp = getBankAccountFromMap(debitAccount2, bankClient, bank.getClientBankAccountMap());
        Assert.assertEquals(debitAccount2, tmp);

        // Prepping for the final checks
        String wantedCurrency = "Euro";
        double valueAfterConversion = convert(debitAccount2, debitAccount2.getBalance(), debitAccount2.getCurrency(), wantedCurrency);

        // Actual call
        bank.currencyExchange(debitAccount2, wantedCurrency);

        // Retrieving the modified account from the bank
        tmp = getBankAccountFromMap(debitAccount2, bankClient, bank.getClientBankAccountMap());

        // Verify the accounts' updated information
        Assert.assertEquals(debitAccount2.getCurrency(), tmp.getCurrency());
        Assert.assertEquals(valueAfterConversion, tmp.getBalance(), Math.ulp(valueAfterConversion));

        // Check the messages. The logs are really detailed so we have to check if our string is in them
        Assert.assertTrue(outputStreamCaptor.toString().trim().contains("Nu se poate face conversia! Contul " + tmp.getIBAN() + " a fost inchis!"));
    }

    @Test
    public void testCurrencyExchange_C234 () throws BankAccountException {
        //Integrity checks
        BankAccount tmp = getBankAccountFromMap(debitAccount2, bankClient, bank.getClientBankAccountMap());
        Assert.assertEquals(debitAccount2, tmp);

        // Prepping for the final checks
        String wantedCurrency = "Yen";
        double valueAfterConversion = convert(debitAccount2, debitAccount2.getBalance(), debitAccount2.getCurrency(), wantedCurrency);

        // Actual call
        bank.currencyExchange(debitAccount2, wantedCurrency);

        // Retrieving the modified account from the bank
        tmp = getBankAccountFromMap(debitAccount2, bankClient, bank.getClientBankAccountMap());

        // Verify the accounts' updated information
        Assert.assertEquals(debitAccount2.getCurrency(), tmp.getCurrency());
        Assert.assertEquals(valueAfterConversion, tmp.getBalance(), Math.ulp(valueAfterConversion));

        // Check the messages. The logs are really detailed so we have to check if our string is in them
        Assert.assertTrue(outputStreamCaptor.toString().trim().contains("Nu se poate face conversia! Contul " + tmp.getIBAN() + " a fost inchis!"));
    }

    @Test
    public void testCurrencyExchange_C312 () throws BankAccountException {
        //Integrity checks
        BankAccount tmp = getBankAccountFromMap(savingsAccount1, bankClient, bank.getClientBankAccountMap());
        Assert.assertEquals(savingsAccount1, tmp);

        // Prepping for the final checks
        String wantedCurrency = null;
        double valueAfterConversion = convert(savingsAccount1, savingsAccount1.getBalance(), savingsAccount1.getCurrency(), wantedCurrency);

        // Actual call
        bank.currencyExchange(savingsAccount1, wantedCurrency);

        // Retrieving the modified account from the bank
        tmp = getBankAccountFromMap(savingsAccount1, bankClient, bank.getClientBankAccountMap());

        // Verify the accounts' updated information
        Assert.assertEquals(savingsAccount1.getCurrency(), tmp.getCurrency());
        Assert.assertEquals(valueAfterConversion, tmp.getBalance(), Math.ulp(valueAfterConversion));

        // Check the messages. The logs are really detailed so we have to check if our string is in them
        Assert.assertTrue(outputStreamCaptor.toString().trim().contains("Conversie esuata!"));
    }

    @Test
    public void testCurrencyExchange_C321 () throws BankAccountException {
        //Integrity checks
        BankAccount tmp = getBankAccountFromMap(savingsAccount1, bankClient, bank.getClientBankAccountMap());
        Assert.assertEquals(savingsAccount1, tmp);

        // Prepping for the final checks
        String wantedCurrency = "Lei";
        double valueAfterConversion = convert(savingsAccount1, savingsAccount1.getBalance(), savingsAccount1.getCurrency(), wantedCurrency);

        // Actual call
        bank.currencyExchange(savingsAccount1, wantedCurrency);

        // Retrieving the modified account from the bank
        tmp = getBankAccountFromMap(savingsAccount1, bankClient, bank.getClientBankAccountMap());

        // Verify the accounts' updated information
        Assert.assertEquals(savingsAccount1.getCurrency(), tmp.getCurrency());
        Assert.assertEquals(valueAfterConversion, tmp.getBalance(), Math.ulp(valueAfterConversion));

        // Check the messages. The logs are really detailed so we have to check if our string is in them
        Assert.assertTrue(outputStreamCaptor.toString().trim().contains("Conversie realizata!"));
    }

    @Test
    public void testCurrencyExchange_C332 () throws BankAccountException {
        //Integrity checks
        BankAccount tmp = getBankAccountFromMap(savingsAccount1, bankClient, bank.getClientBankAccountMap());
        Assert.assertEquals(savingsAccount1, tmp);

        // Prepping for the final checks
        String wantedCurrency = "Yen";
        double valueAfterConversion = convert(savingsAccount1, savingsAccount1.getBalance(), savingsAccount1.getCurrency(), wantedCurrency);

        // Actual call
        bank.currencyExchange(savingsAccount1, wantedCurrency);

        // Retrieving the modified account from the bank
        tmp = getBankAccountFromMap(savingsAccount1, bankClient, bank.getClientBankAccountMap());

        // Verify the accounts' updated information
        Assert.assertEquals(savingsAccount1.getCurrency(), tmp.getCurrency());
        Assert.assertEquals(valueAfterConversion, tmp.getBalance(), Math.ulp(valueAfterConversion));

        // Check the messages. The logs are really detailed so we have to check if our string is in them
        Assert.assertTrue(outputStreamCaptor.toString().trim().contains("Conversie esuata!"));
    }

    @Test
    public void testCurrencyExchange_C414 () throws BankAccountException {
        //Integrity checks
        BankAccount tmp = getBankAccountFromMap(savingsAccount2, bankClient, bank.getClientBankAccountMap());
        Assert.assertEquals(savingsAccount2, tmp);

        // Prepping for the final checks
        String wantedCurrency = null;
        double valueAfterConversion = convert(savingsAccount2, savingsAccount2.getBalance(), savingsAccount2.getCurrency(), wantedCurrency);

        // Actual call
        bank.currencyExchange(savingsAccount2, wantedCurrency);

        // Retrieving the modified account from the bank
        tmp = getBankAccountFromMap(savingsAccount2, bankClient, bank.getClientBankAccountMap());

        // Verify the accounts' updated information
        Assert.assertEquals(savingsAccount2.getCurrency(), tmp.getCurrency());
        Assert.assertEquals(valueAfterConversion, tmp.getBalance(), Math.ulp(valueAfterConversion));

        // Check the messages. The logs are really detailed so we have to check if our string is in them
        Assert.assertTrue(outputStreamCaptor.toString().trim().contains("Nu se poate face conversia! Contul " + tmp.getIBAN() + " a fost inchis!"));
    }

    @Test
    public void testCurrencyExchange_C424 () throws BankAccountException {
        //Integrity checks
        BankAccount tmp = getBankAccountFromMap(savingsAccount2, bankClient, bank.getClientBankAccountMap());
        Assert.assertEquals(savingsAccount2, tmp);

        // Prepping for the final checks
        String wantedCurrency = "Euro";
        double valueAfterConversion = convert(savingsAccount2, savingsAccount2.getBalance(), savingsAccount2.getCurrency(), wantedCurrency);

        // Actual call
        bank.currencyExchange(savingsAccount2, wantedCurrency);

        // Retrieving the modified account from the bank
        tmp = getBankAccountFromMap(savingsAccount2, bankClient, bank.getClientBankAccountMap());

        // Verify the accounts' updated information
        Assert.assertEquals(savingsAccount2.getCurrency(), tmp.getCurrency());
        Assert.assertEquals(valueAfterConversion, tmp.getBalance(), Math.ulp(valueAfterConversion));

        // Check the messages. The logs are really detailed so we have to check if our string is in them
        Assert.assertTrue(outputStreamCaptor.toString().trim().contains("Nu se poate face conversia! Contul " + tmp.getIBAN() + " a fost inchis!"));
    }

    @Test
    public void testCurrencyExchange_C434 () throws BankAccountException {
        //Integrity checks
        BankAccount tmp = getBankAccountFromMap(savingsAccount2, bankClient, bank.getClientBankAccountMap());
        Assert.assertEquals(savingsAccount2, tmp);

        // Prepping for the final checks
        String wantedCurrency = "Yen";
        double valueAfterConversion = convert(savingsAccount2, savingsAccount2.getBalance(), savingsAccount2.getCurrency(), wantedCurrency);

        // Actual call
        bank.currencyExchange(savingsAccount2, wantedCurrency);

        // Retrieving the modified account from the bank
        tmp = getBankAccountFromMap(savingsAccount2, bankClient, bank.getClientBankAccountMap());

        // Verify the accounts' updated information
        Assert.assertEquals(savingsAccount2.getCurrency(), tmp.getCurrency());
        Assert.assertEquals(valueAfterConversion, tmp.getBalance(), Math.ulp(valueAfterConversion));

        // Check the messages. The logs are really detailed so we have to check if our string is in them
        Assert.assertTrue(outputStreamCaptor.toString().trim().contains("Nu se poate face conversia! Contul " + tmp.getIBAN() + " a fost inchis!"));
    }

    @Test
    public void testCurrencyExchange_C513 () throws BankAccountException {
        //Integrity checks
        BankAccount tmp = getBankAccountFromMap(nonExistingAccount, bankClient, bank.getClientBankAccountMap());
        Assert.assertEquals(nonExistingAccount, tmp);

        // Prepping for the final checks
        String wantedCurrency = null;
        double valueAfterConversion = convert(nonExistingAccount, nonExistingAccount.getBalance(), nonExistingAccount.getCurrency(), wantedCurrency);

        // Actual call
        bank.currencyExchange(nonExistingAccount, wantedCurrency);

        // Retrieving the modified account from the bank
        tmp = getBankAccountFromMap(nonExistingAccount, bankClient, bank.getClientBankAccountMap());

        // Verify the accounts' updated information
        Assert.assertEquals(nonExistingAccount.getCurrency(), tmp.getCurrency());
        Assert.assertEquals(valueAfterConversion, tmp.getBalance(), Math.ulp(valueAfterConversion));

        // Check the messages. The logs are really detailed so we have to check if our string is in them
        Assert.assertTrue(outputStreamCaptor.toString().trim().contains("Nu exista contul " + tmp.getIBAN()));
    }

    @Test
    public void testCurrencyExchange_C523 () throws BankAccountException {
        //Integrity checks
        BankAccount tmp = getBankAccountFromMap(nonExistingAccount, bankClient, bank.getClientBankAccountMap());
        Assert.assertEquals(nonExistingAccount, tmp);

        // Prepping for the final checks
        String wantedCurrency = "Lei";
        double valueAfterConversion = convert(nonExistingAccount, nonExistingAccount.getBalance(), nonExistingAccount.getCurrency(), wantedCurrency);

        // Actual call
        bank.currencyExchange(nonExistingAccount, wantedCurrency);

        // Retrieving the modified account from the bank
        tmp = getBankAccountFromMap(nonExistingAccount, bankClient, bank.getClientBankAccountMap());

        // Verify the accounts' updated information
        Assert.assertEquals(nonExistingAccount.getCurrency(), tmp.getCurrency());
        Assert.assertEquals(valueAfterConversion, tmp.getBalance(), Math.ulp(valueAfterConversion));

        // Check the messages. The logs are really detailed so we have to check if our string is in them
        Assert.assertTrue(outputStreamCaptor.toString().trim().contains("Nu exista contul " + tmp.getIBAN()));
    }

    @Test
    public void testCurrencyExchange_C533 () throws BankAccountException {
        //Integrity checks
        BankAccount tmp = getBankAccountFromMap(nonExistingAccount, bankClient, bank.getClientBankAccountMap());
        Assert.assertEquals(nonExistingAccount, tmp);

        // Prepping for the final checks
        String wantedCurrency = "Yen";
        double valueAfterConversion = convert(nonExistingAccount, nonExistingAccount.getBalance(), nonExistingAccount.getCurrency(), wantedCurrency);

        // Actual call
        bank.currencyExchange(nonExistingAccount, wantedCurrency);

        // Retrieving the modified account from the bank
        tmp = getBankAccountFromMap(nonExistingAccount, bankClient, bank.getClientBankAccountMap());

        // Verify the accounts' updated information
        Assert.assertEquals(nonExistingAccount.getCurrency(), tmp.getCurrency());
        Assert.assertEquals(valueAfterConversion, tmp.getBalance(), Math.ulp(valueAfterConversion));

        // Check the messages. The logs are really detailed so we have to check if our string is in them
        Assert.assertTrue(outputStreamCaptor.toString().trim().contains("Nu exista contul " + tmp.getIBAN()));
    }

    /*
     * STRUCTURAL: Se va folosi acoperirea la nivel de instructiune.
     * Graful este in root-ul proiectului si se numeste interBanking.drawio.
     * Nodurile sunt scoate din mainClasses.Bank.interBanking
     * Am folosit https://app.diagrams.net.
     * Structura nume la teste: testInterBanking_Nod1Nod2Nod3..._NodIndividual_NodIndividual_...
     * Pt noduri cu separarea dintre noduri se va face cu un x. (Avem noduri de cu 2 cifre si e mai lizibil)
     */
    @Test
    public void testInterBanking_1_2 () throws TransactionException {
        bank.interBanking(debitAccount1.getIBAN(), savingsAccount1.getIBAN(), -10);

        // Check if the accounts are still the same
        BankAccount tmp1 = getBankAccountFromMap(debitAccount1, bankClient, bank.getClientBankAccountMap());
        BankAccount tmp2 = getBankAccountFromMap(savingsAccount1, bankClient, bank.getClientBankAccountMap());

        Assert.assertEquals(debitAccount1, tmp1);
        Assert.assertEquals(savingsAccount1, tmp2);

        // Check the messages. The logs are really detailed so we have to check if our string is in them
        Assert.assertTrue(outputStreamCaptor.toString().trim().contains("De ce incerci asta? Fa-o invers :)"));
    }

    @Test
    public void testInterBanking_1_3x5_6_7_8x17_18x24_26_28_30_32_34_36 () throws TransactionException {
        double value = 100;
        double initialSenderBalance = debitAccount1.getBalance();
        double initialReceiverBalance = savingsAccount1.getBalance();

                       // receiver
        bank.interBanking(getBankAccountFromMap(savingsAccount1, bankClient, bank.getClientBankAccountMap()).getIBAN(),
                       // sender
                          getBankAccountFromMap(debitAccount1, bankClient, bank.getClientBankAccountMap()).getIBAN(), value);

        // We need to factor the conversion in
        double toGiveToReceiver = CurrencyExchange.convertTransferWithoutText(value, savingsAccount1.getCurrency(), debitAccount1.getCurrency());

        // Check the balances
        Assert.assertEquals(initialSenderBalance - value, getBankAccountFromMap(debitAccount1, bankClient, bank.getClientBankAccountMap()).getBalance(), Math.ulp(getBankAccountFromMap(debitAccount1, bankClient, bank.getClientBankAccountMap()).getBalance()));
        Assert.assertEquals(initialReceiverBalance + toGiveToReceiver, getBankAccountFromMap(savingsAccount1, bankClient, bank.getClientBankAccountMap()).getBalance(), Math.ulp(getBankAccountFromMap(savingsAccount1, bankClient, bank.getClientBankAccountMap()).getBalance()));

        // Check the messages. The logs are really detailed so we have to check if our string is in them
        Assert.assertTrue(outputStreamCaptor.toString().trim().contains("Transferul din contul " + debitAccount1.getIBAN() + " in contul " +
                savingsAccount1.getIBAN() + " in valoare de " + FormatDouble.format(value) + " " +
                debitAccount1.getCurrency() + " a avut succes!"));
    }

    @Test
    public void testInterBanking_1_3x5_6_7_8x17_18x24_25 () throws TransactionException {
        double value = 100;
        double initialSenderBalance = debitAccount1.getBalance();
        double initialReceiverBalance = savingsAccount2.getBalance();

                          // receiver
        bank.interBanking(getBankAccountFromMap(savingsAccount2, bankClient, bank.getClientBankAccountMap()).getIBAN(),
                          // sender
                          getBankAccountFromMap(debitAccount1, bankClient, bank.getClientBankAccountMap()).getIBAN(), value);

        // Check the balances
        Assert.assertEquals(initialSenderBalance, getBankAccountFromMap(debitAccount1, bankClient, bank.getClientBankAccountMap()).getBalance(), Math.ulp(getBankAccountFromMap(debitAccount1, bankClient, bank.getClientBankAccountMap()).getBalance()));
        Assert.assertEquals(initialReceiverBalance, getBankAccountFromMap(savingsAccount2, bankClient, bank.getClientBankAccountMap()).getBalance(), Math.ulp(getBankAccountFromMap(savingsAccount2, bankClient, bank.getClientBankAccountMap()).getBalance()));

        // Check the messages. The logs are really detailed so we have to check if our string is in them
        Assert.assertTrue(outputStreamCaptor.toString().trim().contains("Nu se poate face transferul! Contul " + savingsAccount2.getIBAN() + " a fost inchis!"));
    }

    @Test
    public void testInterBanking_1_3x5_6_7_8x17_18x24_26_27 () throws TransactionException {
        double value = 100;
        double initialSenderBalance = debitAccount2.getBalance();
        double initialReceiverBalance = savingsAccount1.getBalance();

                // receiver
        bank.interBanking(getBankAccountFromMap(savingsAccount1, bankClient, bank.getClientBankAccountMap()).getIBAN(),
                // sender
                getBankAccountFromMap(debitAccount2, bankClient, bank.getClientBankAccountMap()).getIBAN(), value);

        // Check the balances
        Assert.assertEquals(initialSenderBalance, getBankAccountFromMap(debitAccount2, bankClient, bank.getClientBankAccountMap()).getBalance(), Math.ulp(getBankAccountFromMap(debitAccount2, bankClient, bank.getClientBankAccountMap()).getBalance()));
        Assert.assertEquals(initialReceiverBalance, getBankAccountFromMap(savingsAccount1, bankClient, bank.getClientBankAccountMap()).getBalance(), Math.ulp(getBankAccountFromMap(savingsAccount1, bankClient, bank.getClientBankAccountMap()).getBalance()));

        // Check the messages. The logs are really detailed so we have to check if our string is in them
        Assert.assertTrue(outputStreamCaptor.toString().trim().contains("Nu se poate face transferul! Contul " + debitAccount2.getIBAN() + " a fost inchis!"));
    }

    @Test
    public void testInterBanking_1_3x5_6_7_8x17_18x24_26_28_29 () throws TransactionException {
        double value = 100;
        double initialSenderBalance = debitAccount2.getBalance();
        double initialReceiverBalance = savingsAccount2.getBalance();

                // receiver
        bank.interBanking(getBankAccountFromMap(savingsAccount2, bankClient, bank.getClientBankAccountMap()).getIBAN(),
                // sender
                getBankAccountFromMap(debitAccount2, bankClient, bank.getClientBankAccountMap()).getIBAN(), value);

        // Check the balances
        Assert.assertEquals(initialSenderBalance, getBankAccountFromMap(debitAccount2, bankClient, bank.getClientBankAccountMap()).getBalance(), Math.ulp(getBankAccountFromMap(debitAccount2, bankClient, bank.getClientBankAccountMap()).getBalance()));
        Assert.assertEquals(initialReceiverBalance, getBankAccountFromMap(savingsAccount2, bankClient, bank.getClientBankAccountMap()).getBalance(), Math.ulp(getBankAccountFromMap(savingsAccount2, bankClient, bank.getClientBankAccountMap()).getBalance()));

        // Check the messages. The logs are really detailed so we have to check if our string is in them
        Assert.assertTrue(outputStreamCaptor.toString().trim().contains("Nu se poate face transferul! Ambele conturi au fost inchise!"));
    }

    @Test
    public void testInterBanking_1_3x5_6_7_8x17_18x24_26_30_31 () throws TransactionException {
        double value = 100;
        double initialSenderBalance = debitAccount1.getBalance();
        double initialReceiverBalance = nonExistingAccount.getBalance();

                // receiver
        bank.interBanking(nonExistingAccount.getIBAN(),
                // sender
                getBankAccountFromMap(debitAccount1, bankClient, bank.getClientBankAccountMap()).getIBAN(), value);

        // Check the balances
        Assert.assertEquals(initialSenderBalance, getBankAccountFromMap(debitAccount1, bankClient, bank.getClientBankAccountMap()).getBalance(), Math.ulp(getBankAccountFromMap(debitAccount1, bankClient, bank.getClientBankAccountMap()).getBalance()));
        Assert.assertEquals(initialReceiverBalance, getBankAccountFromMap(nonExistingAccount, bankClient, bank.getClientBankAccountMap()).getBalance(), Math.ulp(getBankAccountFromMap(nonExistingAccount, bankClient, bank.getClientBankAccountMap()).getBalance()));

        // Check the messages. The logs are really detailed so we have to check if our string is in them
        Assert.assertTrue(outputStreamCaptor.toString().trim().contains("Nu exista contul in care transferati"));
    }

    @Test
    public void testInterBanking_1_3x5_6_7_8x17_18x24_26_30_32_33 () throws TransactionException {
        double value = 100;
        double initialSenderBalance = nonExistingAccount.getBalance();
        double initialReceiverBalance = debitAccount1.getBalance();

                // receiver
        bank.interBanking(debitAccount1.getIBAN(),
                // sender
                getBankAccountFromMap(nonExistingAccount, bankClient, bank.getClientBankAccountMap()).getIBAN(), value);

        // Check the balances
        Assert.assertEquals(initialSenderBalance, getBankAccountFromMap(nonExistingAccount, bankClient, bank.getClientBankAccountMap()).getBalance(), Math.ulp(getBankAccountFromMap(nonExistingAccount, bankClient, bank.getClientBankAccountMap()).getBalance()));
        Assert.assertEquals(initialReceiverBalance, getBankAccountFromMap(debitAccount1, bankClient, bank.getClientBankAccountMap()).getBalance(), Math.ulp(getBankAccountFromMap(debitAccount1, bankClient, bank.getClientBankAccountMap()).getBalance()));

        // Check the messages. The logs are really detailed so we have to check if our string is in them
        Assert.assertTrue(outputStreamCaptor.toString().trim().contains("Nu exista contul din care transferati"));
    }

    @Test
    public void testInterBanking_1_3x5_6_7_8x17_18x24_26_30_32_34_35_36 () throws TransactionException {
        double value = 100;
        double initialSenderBalance = nonExistingAccount.getBalance();
        double initialReceiverBalance = nonExistingAccount.getBalance();

                // receiver
        bank.interBanking(nonExistingAccount.getIBAN(),
                // sender
                getBankAccountFromMap(nonExistingAccount, bankClient, bank.getClientBankAccountMap()).getIBAN(), value);

        // Check the balances
        Assert.assertEquals(initialSenderBalance, getBankAccountFromMap(nonExistingAccount, bankClient, bank.getClientBankAccountMap()).getBalance(), Math.ulp(getBankAccountFromMap(nonExistingAccount, bankClient, bank.getClientBankAccountMap()).getBalance()));
        Assert.assertEquals(initialReceiverBalance, getBankAccountFromMap(nonExistingAccount, bankClient, bank.getClientBankAccountMap()).getBalance(), Math.ulp(getBankAccountFromMap(nonExistingAccount, bankClient, bank.getClientBankAccountMap()).getBalance()));

        // Check the messages. The logs are really detailed so we have to check if our string is in them
        Assert.assertTrue(outputStreamCaptor.toString().trim().contains("Nu exista nici un cont"));
    }


    /* KILLING MUTANTS */
    @Test
    public void testInterBanking_1_2_MUTANT () throws TransactionException {
        // linia 502 in cod, boundary mutant <= 0 il testa cu <0
        bank.interBanking(debitAccount1.getIBAN(), savingsAccount1.getIBAN(), 0);

        // Check if the accounts are still the same
        BankAccount tmp1 = getBankAccountFromMap(debitAccount1, bankClient, bank.getClientBankAccountMap());
        BankAccount tmp2 = getBankAccountFromMap(savingsAccount1, bankClient, bank.getClientBankAccountMap());

        Assert.assertEquals(debitAccount1, tmp1);
        Assert.assertEquals(savingsAccount1, tmp2);

        // Check the messages. The logs are really detailed so we have to check if our string is in them
        Assert.assertTrue(outputStreamCaptor.toString().trim().contains("De ce incerci asta? Fa-o invers :)"));
    }

    /*
     * Nevand alte variante pt a omori mutantii pe testele de mai sus, sunt nevoit sa fac niste teste pe o alta
     * metoda pentru a putea indeplini cerinta.
     * Metoda se afla in mainClasses.Loan
     */
    @Test
    public void testPayMonthlyRate () throws LoanException {
        // Testul nu are logica dpdv al functionalitatii bancii, el fiind facut sa punctam cerinta
        // Monthly rate ul aici va fi 200lei/2luni = 100 lei per luna
        Loan loan = new Loan(200, "Lei", "Imprumut pentru nevoi personale", "03-12-2021", 2);

        loan.payMonthlyRate(100, bankClient.getCnp());

        // Din imprumut mai raman 100lei
        Assert.assertEquals(100, loan.getValue(), Math.ulp(100));
    }

    @Test
    public void testPayMonthlyRate_MUTANT () throws LoanException {
        // Linia 116, verificam si faptul ca se scad lunile atunci cand se plateste o rata
        Loan loan = new Loan(200, "Lei", "Imprumut pentru nevoi personale", "03-12-2021", 2);

        loan.payMonthlyRate(100, bankClient.getCnp());

        // Din imprumut mai raman 100lei
        Assert.assertEquals(100, loan.getValue(), Math.ulp(100));
        // Imprumutul mai are o luna
        Assert.assertEquals(1, loan.getDurationMonths());
    }
}