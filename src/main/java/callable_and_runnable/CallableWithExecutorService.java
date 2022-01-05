package callable_and_runnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class CallableWithExecutorService {
    public static void main(String[] args) throws InterruptedException {

        class MathTask implements Callable<Integer>{
            int a, b, sleepTime;
            public MathTask(int a, int b, int sleepTime){
                this.a = a;
                this.b = b;
                this.sleepTime = sleepTime;
            }
            @Override
            public Integer call() throws Exception {
                Thread.sleep(sleepTime);
                return a * b;
            }
        }

        Callable <List<Integer>> callableWithTasksInside = () -> {
            MathTask mathTask1 = new MathTask(10,10, 2000);
            MathTask mathTask2 = new MathTask(20,20, 2000);
            MathTask mathTask3 = new MathTask(30,30, 2000);

            List<Callable<Integer>> tasks = new ArrayList<>(Arrays.asList(mathTask1, mathTask2, mathTask3));
            List<Integer> finalResults = new ArrayList<>();

            ExecutorService executor = Executors.newFixedThreadPool(3);
            //SinlgeThreadExecutor wydrkuje wszystkie wyniki po 6s
            //FixedThreadPool(3) wydrkuje wszystkie wyniki po 2s

            executor.invokeAll(tasks).forEach(futureInteger -> {
                try {
                    finalResults.add(futureInteger.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            });
            executor.shutdown();
            return finalResults;
        };



        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<List<Integer>> futureResults = executor.submit(callableWithTasksInside);
        executor.shutdown();
        //executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        //nic poniżej się nie wykona dopóki executor nie skończy pracy

        System.out.println("Waiting for results...");

        List<Integer> integerResults = new ArrayList<>();
        try{
            System.out.println(futureResults.isDone());//false
            integerResults = futureResults.get(); //.get() czeka aż zadania się wykonają
            System.out.println(futureResults.isDone());//true

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        System.out.println(integerResults); //[100, 400, 900]
    }
}
