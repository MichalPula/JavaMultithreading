package testing_monitor_objects;

import java.util.concurrent.locks.ReentrantLock;

class Counter {
    private int value;
    private ReentrantLock lock = new ReentrantLock();

    public void increment() {
        lock.lock();
        try{
            value += 1;
        }finally {
            lock.unlock();
        }
    }
    public int getValue() {
        return value;
    }
}

public class PotentialRaceConditionExample {
    public static void main(String[] args) throws InterruptedException{

        Counter counter = new Counter();

        Runnable runnable = () -> {
            for (int i = 0; i < 100000; i++) {
                counter.increment();
            }
        };

        Thread thread1 = new Thread(runnable);
        Thread thread2 = new Thread(runnable);
        Thread thread3 = new Thread(runnable);
        thread1.start();
        thread2.start();
        thread3.start();
        thread1.join();
        thread2.join();
        thread3.join();

        System.out.println(counter.getValue());
    }
}
