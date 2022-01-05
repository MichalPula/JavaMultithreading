package synchronization_with_Nam_Ha_Minh;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TransactionTest {
    public static void main(String[] args) {

        BankService bankService = new BankService();

        ExecutorService executor = Executors.newFixedThreadPool(BankService.ACCOUNT_NUMBER);
        for (int i = 0; i < BankService.ACCOUNT_NUMBER; i++) {
            executor.submit(new Transaction(bankService, i));
        }
    }
}
