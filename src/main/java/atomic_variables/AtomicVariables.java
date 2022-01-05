package atomic_variables;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class AtomicVariables {
    public static void main(String[] args) throws InterruptedException {

        //final int[] count = {0};
        //final Object lock = new Object();
        AtomicInteger counterAtomic = new AtomicInteger();

        Runnable runnableCounter = () -> {
            //synchronized (lock){
            for (int i = 0; i < 10_000_0; i++) {
                counterAtomic.incrementAndGet();
                //count[0] ++;
            }
            //lock.notifyAll();
            //}
        };

        Thread firstThread = new Thread(runnableCounter, "First");
        Thread secondThread = new Thread(runnableCounter, "Second");

        firstThread.start();
        secondThread.start();
        firstThread.join();
        secondThread.join();

        //System.out.println(count[0]);
        System.out.println(counterAtomic); //200000




        AtomicInteger number = new AtomicInteger(222);
        number.compareAndSet(222, 444);//ok newValue = 444
        number.compareAndSet(444, 445);//ok newValue = 445
        number.compareAndSet(223, 555);// false 223 != 445
        System.out.println("the updated number: " + number);//445

        Apple apple = new Apple(300);
        AtomicReference<Apple> atomicApple = new AtomicReference<>(apple);
        atomicApple.compareAndSet(apple, new Apple(350));//ok Apple{weight=350}
        atomicApple.compareAndSet(atomicApple.get(), new Apple(400));//ok Apple(weight=400)
        atomicApple.compareAndSet(apple, new Apple(450));//false Apple(weight=300) != Apple(weight=400)
        System.out.println(atomicApple.get().toString());//Apple(weight=400)
        System.out.println(apple.toString());//Apple(weight=300)
    }
}
