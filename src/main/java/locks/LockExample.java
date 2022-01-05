package locks;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class LockExample {

    private static final ReentrantLock lock = new ReentrantLock();
    private static final Condition condition = lock.newCondition();
    private static Integer money = 100;

    public static void main(String[] args) {
        Runnable depositMoney = () -> {
            lock.lock();
            try {
                Thread.sleep(1500);
                money += 10;
                System.out.println(Thread.currentThread().getName() + " added 10PLN" + " money = " + money);
                if (money > 70) {
                    condition.signalAll();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {

                lock.unlock();
            }
        };
        Runnable withdrawMoney = () -> {
            lock.lock();
            try {
                while (money < 70) {
                    System.out.println(Thread.currentThread().getName() + " is waiting ");
                    condition.await();
                }
                Thread.sleep(1500);
                money -= 20;
                System.out.println(Thread.currentThread().getName() + " withdrawn 20PLN" + " money = " + money);

                condition.signalAll();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        };

        ExecutorService ex = Executors.newFixedThreadPool(10);
        ex.submit(depositMoney);//110
        ex.submit(withdrawMoney);//90
        ex.submit(withdrawMoney);//70
        ex.submit(withdrawMoney);//50
        ex.submit(withdrawMoney);//is waiting
        ex.submit(depositMoney);//60
        ex.submit(depositMoney);//70
        ex.submit(depositMoney);//80
        ex.submit(depositMoney);//90
        ex.submit(withdrawMoney);//50
        ex.shutdown();
    }
}
