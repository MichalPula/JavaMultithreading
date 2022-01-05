package synchronization_with_Nam_Ha_Minh;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BankService {

    public static final int ACCOUNT_NUMBER = 10;
    public static final int MAX_TRANSFER_AMOUNT = 10;
    public static final int INITIAL_BALANCE = 100;
    private final List<Account> accounts = new ArrayList<>(ACCOUNT_NUMBER);

    private Lock bankLock;
    private Condition availableFund;


    public BankService() {
        for (int i = 0; i < ACCOUNT_NUMBER; i++) {
            Account account = new Account(INITIAL_BALANCE);
            accounts.add(account);
        }
        bankLock = new ReentrantLock();
        availableFund = bankLock.newCondition();
    }

    public synchronized void transfer(int from, int to, int amount) {
        //bankLock.lock();
        try{
            while (accounts.get(from).getBalance() < amount){
                //availableFund.await();
                wait();
            }

            accounts.get(from).withdraw(amount);
            accounts.get(to).deposit(amount);
            String message = "%s transfered %d from %s to %s. Total balance: %d\n";
            String threadName = Thread.currentThread().getName();
            System.out.printf(message, threadName, amount, from, to, getTotalBalance());

            //availableFund.signalAll();
            notifyAll();
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
//        finally {
//            bankLock.unlock();
//        }
    }

    public int getTotalBalance() {
        return accounts.stream().map(Account::getBalance).reduce(0, Integer::sum);
    }
    //Bez synchronizacji total balance, które cały czas powinno być równe 1000, maleje lub rośnie
    //Dzieje się tak przez występowanie Race condition read-modify-write
}
