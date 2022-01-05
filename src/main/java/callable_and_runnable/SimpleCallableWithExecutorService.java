package callable_and_runnable;

import java.util.concurrent.*;

public class SimpleCallableWithExecutorService {
    public static void main(String[] args){

        Callable<Integer> mathTask = () -> {
            Thread.sleep(3000);
            return 5*5;
        };

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Integer> futureInteger = executor.submit(mathTask);
        executor.shutdown();
        System.out.println(futureInteger.isDone());//false
        Integer result = 0;

        try {
            result = futureInteger.get(2900, TimeUnit.MILLISECONDS);
            //jeśli w czasie 2900ms zadanie się nie wykona - zostanie rzucony wyjątek
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
        System.out.println(futureInteger.isDone()); //false
        System.out.println(result);//0
    }
}
