package synchronization_with_Nam_Ha_Minh;

import java.util.Random;

public class Transaction implements Runnable {
    private BankService bankService;
    private int fromAccount;

    public Transaction(BankService bankService, int fromAccount) {
        this.bankService = bankService;
        this.fromAccount = fromAccount;
    }

    @Override
    public void run() {
        while (true) {
            int toAccount = new Random().nextInt(BankService.ACCOUNT_NUMBER);//0-9

            int amount = new Random().nextInt(BankService.MAX_TRANSFER_AMOUNT - 1) + 1;//1-9

            bankService.transfer(fromAccount, toAccount, amount);

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
