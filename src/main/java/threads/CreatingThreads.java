package threads;

public class CreatingThreads extends Thread {
    @Override
    public void run() {
        System.out.println("MyThread is running!");
    }


    public static void main(String[] args) {
        Runnable anonymousRunnable = new java.lang.Runnable() {
            @Override
            public void run() {
                System.out.println("AnonymousRunnable 2nd way running!");
            }
        };
        Thread myThread = new Thread(anonymousRunnable);



        Runnable lambdaRunnable = () -> {
            System.out.println("My name is: " + Thread.currentThread().getName());//LambdaThread
        };
        System.out.println("My name is: " + Thread.currentThread().getName()); //main
        Thread thread = new Thread(lambdaRunnable, "LambdaThread");
    }
}


class MyRunnable implements java.lang.Runnable {
    @Override
    public void run() {
        System.out.println("MyRunnable 1st way running!");
    }
    Thread myThread = new Thread(new MyRunnable());
}
