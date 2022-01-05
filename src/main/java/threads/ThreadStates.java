package threads;

public class ThreadStates {
    public static void main(String[] args) throws InterruptedException {

        Runnable lambdaRunnable = () -> {
            try {
                Thread.sleep(1000);
                System.out.println("My name is: " + Thread.currentThread().getName());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        Thread thread1 = new Thread(lambdaRunnable, "Thread1");
        System.out.println(thread1.getName() + " is " + thread1.getState()); //NEW
        thread1.start();
        System.out.println(thread1.getName() + " is " + thread1.getState()); //RUNNABLE

        thread1.join(); //powoduje, że main thread czeka, aż thread1 zakończy swoją pracę i dopiero później wykonuje linie kodu poniżej

        System.out.println("main was waiting for Thread1, so this prints AFTER Thread1 executes lambdaRunnable");

        if (thread1.getState() == Thread.State.TERMINATED) {
            System.out.println(thread1.getName() + " is TERMINATED");
        }

        testThreadInterrupt();
        testThreadPriorities();
    }

    private static void testThreadInterrupt() {
        Runnable lambdaRunnable = () -> {
            System.out.println("Run inside Thread1");
            for (int i = 1; i <= 5; i++) {
                System.out.println(i);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    System.out.println("I'm resumed");
                }
            }
            System.out.println("End of run");
        };

        Thread thread1 = new Thread(lambdaRunnable, "Thread1");
        thread1.start();
        try {
            Thread.sleep(2100);
            thread1.interrupt();
        } catch (InterruptedException ex) {
            // do nothing
        }
        //1
        //2
        //I'm resumed
        //3
        //4
        //5
        //End of run
    }


    private static void testThreadPriorities() {
        Thread thread1 = new Thread("Thread1");
        thread1.start();

        System.out.println(Thread.currentThread().getPriority()); //main = 5
        System.out.println(thread1.getPriority()); //thread1 = 5

        thread1.setPriority(Thread.MAX_PRIORITY);
        System.out.println(thread1.getPriority()); //thread1 = 10;
    }
}
