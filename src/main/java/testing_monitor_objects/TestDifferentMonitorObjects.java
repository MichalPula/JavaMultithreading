package testing_monitor_objects;

import java.util.concurrent.*;

public class TestDifferentMonitorObjects {
    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {

        ExecutorService ex1 = Executors.newFixedThreadPool(10);
        Runnable task1 = TestDifferentMonitorObjects::methodSynchronizedOnTheSameObject1;
        Runnable task2 = TestDifferentMonitorObjects::methodSynchronizedOnTheSameObject2;
        ex1.submit(task1);
        ex1.submit(task1);
        ex1.submit(task2);
        ex1.submit(task2);
        ex1.shutdown();

        if(ex1.awaitTermination(8, TimeUnit.SECONDS)) {
            ExecutorService ex2 = Executors.newFixedThreadPool(5);
            Runnable task3 = TestDifferentMonitorObjects::methodSynchronizedOnClassObject1;
            Runnable task4 = TestDifferentMonitorObjects::methodSynchronizedOnClassObject2;
            ex2.submit(task3);
            ex2.submit(task4);
            ex2.shutdown();
        }
    }

    private static final Object object1 = new Object();
    //private static final Object object2 = new Object();

    public static void methodSynchronizedOnTheSameObject1(){
        System.out.println("Started method 1");
        synchronized (object1) {
            try{
                Thread.sleep(1500);
                System.out.println("Inside 1");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public static void methodSynchronizedOnTheSameObject2(){
        System.out.println("Started method 2");
        synchronized (object1) {//object2
            try {
                Thread.sleep(2000);
                System.out.println("Inside 2");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }



    private static void methodSynchronizedOnClassObject1() {
        synchronized (Object.class) {
            try {
                Thread.sleep(2000);
                System.out.println(Thread.currentThread().getName() + " is working inside method using Object.class 1");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private static void methodSynchronizedOnClassObject2() {
        synchronized (Object.class) {
            try {
                Thread.sleep(2000);
                System.out.println(Thread.currentThread().getName() + " is working inside method using Object.class 2");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
