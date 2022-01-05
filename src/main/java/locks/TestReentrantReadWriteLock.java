package locks;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TestReentrantReadWriteLock {
    public static void main(String[] args) throws InterruptedException {

        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        AtomicReference<String> message = new AtomicReference<>("");

        Runnable reader = () -> {
            if(lock.isWriteLocked()){
                System.out.println("Write lock present! You have to wait unitil it finishes it’s job" + Thread.currentThread().getName());
            }
            lock.readLock().lock();
            //kod poniżej nie zostanie wykonany, jeśli wątki posiadające writeLocka nie wykonają najpierw swojej pracy
            System.out.println(Thread.currentThread().getName() +":reader "+ message.get());
            lock.readLock().unlock();
        };

        Runnable writerA = () -> {
            lock.writeLock().lock();
            System.out.println(Thread.currentThread().getName() + " writerA doing its job");
            message.getAndUpdate(s -> s.concat("modifiedByWriterA "));
            lock.writeLock().unlock();
        };
        Runnable writerB = () -> {
            lock.writeLock().lock();
            System.out.println(Thread.currentThread().getName() + " writerB doing its job");
            message.getAndUpdate(s -> s.concat("modifiedByWriterB "));
            lock.writeLock().unlock();
        };

        ExecutorService executor = Executors.newFixedThreadPool(3);
        executor.execute(writerA);
        executor.execute(writerB);
        executor.execute(reader);
        executor.shutdown();
        //pool-1-thread-1 writerA doing its job
        //Write lock present! You have to wait unitil it finishes it’s jobpool-1-thread-3
        //pool-1-thread-2 writerB doing its job
        //pool-1-thread-3:reader modifiedByWriterA modifiedByWriterB
    }
}
