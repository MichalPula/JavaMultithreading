package executor_service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ExecutorServiceTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //differentExecutors();
        //testExecute();
        //testSubmit();
        //testInvokeAnyAndAll();
        //testShutdownNow();
        //testAwaitTermination();
    }

    private static void differentExecutors() {
        Callable<Integer> mathTask = () -> {
            return 5 * 5;
        };
        Runnable someRunnable = () -> {
            System.out.println("I'm scheduled!");
        };
        ExecutorService executor1 = Executors.newCachedThreadPool();
        ExecutorService executor2 = Executors.newFixedThreadPool(10);
        ExecutorService executor3 = Executors.newSingleThreadExecutor();

        ExecutorService ex1 = Executors.newSingleThreadScheduledExecutor();//ThreadFactory
        ExecutorService ex2 = Executors.newScheduledThreadPool(5);//ThreadFactory

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);

        //ScheduledFuture<Integer> futureInteger1 = executor.schedule(mathTask, 2, TimeUnit.SECONDS);

        //będzie wykonywać zadanie po 2 sekundach co 3 sekundy
        //ScheduledFuture<?> futureInteger2 = executor.scheduleAtFixedRate(someRunnable, 2,3, TimeUnit.SECONDS);
        //ScheduledFuture<?> futureInteger3 = executor.scheduleWithFixedDelay(someRunnable, 2,2, TimeUnit.SECONDS);

        ExecutorService forkJoinPool = Executors.newWorkStealingPool();
        ForkJoinPool fork = new ForkJoinPool();
    }

    private static Runnable newRunnable(String msg) {
        return () -> {
            String completeMsg = Thread.currentThread().getName() + ": " + msg;
            System.out.println(completeMsg);
        };
    }
    private static Callable<String> newCallable(String msg){
        return () -> {
            String completeMsg = Thread.currentThread().getName() + ": " + msg;
            return completeMsg + " completed";
        };
    }
    private static Callable<Integer> newMathTask(){
        return () -> {
            Thread.sleep(3000);
            return 5*5;
        };
    }

    private static void testExecute() {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        executor.execute(newRunnable("Task 1"));
        executor.execute(newRunnable("Task 2"));
        executor.execute(newRunnable("Task 3"));
        executor.shutdown();
    }

    private static void testSubmit() {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        Future<String> futureString = executor.submit(newCallable("Task 1.1"));
        System.out.println(futureString.isDone()); //false

        String result = "";
        try {
            result = futureString.get();
        }catch (InterruptedException | ExecutionException e){
            e.printStackTrace();
        }
        System.out.println(result); //pool-1-thread-1: Task 1.1 completed
        System.out.println(futureString.isDone()); //true
        executor.shutdown();
    }

    private static void testInvokeAnyAndAll() throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        List<Callable<String>> callables = new ArrayList<>();
        callables.add(newCallable("Task 1.1"));
        callables.add(newCallable("Task 1.2"));
        callables.add(newCallable("Task 1.3"));

        String result = "";
        result = executor.invokeAny(callables);
        //wykona się jedno z zadań 1.1/1.2/1.3
        System.out.println(result);



        List<Future<String>> results;
        results = executor.invokeAll(callables);
        results.forEach(stringFuture -> {
            try {
                System.out.println(stringFuture.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        //pool-1-thread-1: Task 1.1 completed
        //pool-1-thread-2: Task 1.2 completed
        //pool-1-thread-3: Task 1.3 completed
        executor.shutdown();
    }

    private static void testShutdownNow() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Integer> futureInteger = executor.submit(newMathTask());
        executor.shutdownNow();

        System.out.println(futureInteger.isDone());//false
        System.out.println(futureInteger);
        //java.util.concurrent.FutureTask@68de145[Not completed, task = executor_service.ExecutorServiceTest$$Lambda$15/0x0000000800b94440@b81eda8]
    }

    private static void testAwaitTermination() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Integer> futureInteger = executor.submit(newMathTask());
        executor.shutdown();
        System.out.println(futureInteger.isDone()); //false
        Integer result = 0;
        try {
            //jeśli po odczekaniu 3010ms Executor się wyłączył (shutdown() wywołane wyżej)
            //czyli wykonał zadanie - zwrócone zostanie true a wynik przypisany do result
            if(executor.awaitTermination(3010, TimeUnit.MILLISECONDS)) {
                result = futureInteger.get();
            } else{
                executor.shutdownNow();//jeśli po 3010ms Executor jeszcze się nie wyłączył
                //zmuszamy go do zakończenia pracy natychmiast - isDone() niżej będzie false a Integer = 0
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println(futureInteger.isDone()); //true
        System.out.println(result);//25
    }
}
