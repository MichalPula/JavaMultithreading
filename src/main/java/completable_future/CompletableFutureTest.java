package completable_future;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class CompletableFutureTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //testGet();
        //testIsDoneCancelledAndCompletedExceptionally();
        //testRunAsync();
        //testSupplyAsync();
        //testThenApply();
        //testThenAccept();
        //testThenRun();
        //testThenCompose();
        //testThenCombine();
        //testAllOf();
        //testAnyOf();
        //testExceptionally();
        //testHandle();
    }


    private static void testGet() {
        //Rzuca checked exceptions - ExecutionException oraz InterruptedException.
        //Metoda get() blokuje wątek, który ją wykonuje do momentu aż Future zakończy pracę.
        //W tym przypadku zostaniemy zablokowani na zawsze, ponieważ Future nie ma miejsca, w którym kończy pracę.
        CompletableFuture<Integer> futureInt = new CompletableFuture<>();
        //futureInt.complete(5);
        try {
            Integer result = futureInt.get();
            System.out.println(result);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }

    private static void testIsDoneCancelledAndCompletedExceptionally() {
        CompletableFuture<Integer> futureInt = new CompletableFuture<>();

        System.out.println(futureInt.isCompletedExceptionally());//false
        //zwraca true, jeśli wykonanie Future spowodowało wyjątek. Powodem może być .cancel(), .completeExceptionally() lub inny problem
        System.out.println(futureInt.isCancelled());//false
        //zwraca true, jeśli CompletableFuture zostało anulowane, zanim zakończyło normalnie pracę
        System.out.println(futureInt.isDone());//false
        //zwraca true, jeśli CompletableFuture zakończyło pracę normalnie/przez wyjątek/lub zostało anulowane

        futureInt.cancel(true);
        System.out.println(futureInt.isCompletedExceptionally());//true
        System.out.println(futureInt.isCancelled());//true
        System.out.println(futureInt.isDone());//true
        //Przy normalnie zakończonej pracy true zwróci tylko metoda isDone()

    }

    private static void testRunAsync() throws ExecutionException, InterruptedException {
        //Wykonuje asynchronicznie zadanie, które nie zwraca wartości
        //Defaultowo w CompletableFuture jako ThreadPool używany jest ForkJoinPool, jednak do metody runAsync() można przekazać swój ThreadPool
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            System.out.println("Print print print!");
        });
        System.out.println(future.get());//null, ponieważ Runnable nie zwraca wartości

    }

    private static void testSupplyAsync() throws InterruptedException {
        //Wykonuje asynchronicznie zadania, które zwracają wartość
        ExecutorService executor = Executors.newFixedThreadPool(5);
        CompletableFuture<Integer> futureInteger = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 5*5;
        }, executor);
        executor.shutdown();
        //join() działa tak samo jak get, ale rzuca unchecked CompletionException.
        //W przeciwieństwie do get() nie musimy łapać wyjątków
        System.out.println(futureInteger.join());//25
    }

    private static void testThenApply() {
        //thenApply() - funkcja zostanie wykonana przez ten sam wątek, który wykonuje zadanie w CompletableFuture (jeśli CF wykona się bardzo szybko, thenApply może zostać wykonane przez wątek main)
        //thenApplyAsync() - funkcja zostanie wykonana przez inny wątek z ForkJoinPoola
        //thenApplyAsync(Executor) - funkcja zostanie wykonana przez nasz Executor
        CompletableFuture<Integer> futureInteger = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName()); //ForkJoinPool.commonPool-worker-3
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 5 * 5;
        })
        .thenApply(integer -> integer * 4)
        .thenApply(integer -> integer + 100)
        .thenApply(integer -> {
            System.out.println(Thread.currentThread().getName()); //ForkJoinPool.commonPool-worker-3
            return integer;
        });
        System.out.println(futureInteger.join());//200
    }

    private static void testThenAccept() {
        CompletableFuture<Integer> futureInteger = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName()); //worker-3
            return 5*5;
        }).thenApply(integer -> {
            System.out.println(Thread.currentThread().getName());//main lub worker-3
            return integer * 4;
        });
        futureInteger.join();//bez poczekania na wynik dzięki join() - thenAcceptAsync() może się nie wykonać
        //wynik supplyAsync() i thenApply() może jeszcze nie być dostępny!

        futureInteger.thenAcceptAsync(integer -> {
            System.out.println(Thread.currentThread().getName());//worker-3
            System.out.println(integer); //100
        });
    }

    private static void testThenRun() {
        CompletableFuture<Integer> futureInteger = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName()); //worker-3
            return 5*5;
        });
        futureInteger.join();//bez poczekania na wynik dzięki join() - thenRun() się nie wykona!
        futureInteger.thenRun(() -> System.out.println(Thread.currentThread().getName()));//main
    }

    private static void testThenCompose() {
        //Łączy dwa zależne od siebie obiekty CompletableFuture w jeden (drugi bazuje na pierwszym, działa to podobnie jak wywołanie .thenApply())
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> "Hello")
                .thenCompose(string -> CompletableFuture.supplyAsync(() -> string + " World"));
        System.out.println(completableFuture.join());//Hello World
    }

    private static void testThenCombine() {
        //Łączy dwa niezależne od siebie obiekty CompletableFuture w jeden w momencie gdy oba są gotowe
        CompletableFuture<Double> weightInKgFuture = CompletableFuture.supplyAsync(() -> {
            return 81.0;
        });
        CompletableFuture<Double> heightInCmFuture = CompletableFuture.supplyAsync(() -> {
            return 188.0;
        });

        CompletableFuture<Double> BMIFuture = weightInKgFuture
                .thenCombine(heightInCmFuture, (weightInKg, heightInCm) -> {
                    Double heightInMeter = heightInCm/100;
                    return weightInKg/(heightInMeter*heightInMeter);
                });
        System.out.println("Your BMI is - " + BMIFuture.join());//22.917
    }



    private static void testAllOf() {
        System.out.println(getPagesContent());//[VCsC4, lFTA9, 7DIDz, NPCdG, 6Kz75, nJulE, Xm39U]
    }
    private static List<String> getPagesContent() {
        List<String> webPageLinks = new ArrayList<>(
                Arrays.asList("link1", "link2", "link3", "link4", "link5", "link6", "link7"));

        List<CompletableFuture<String>> webPagesContentFuturesList = webPageLinks.stream().map(link -> downloadWebPage(link)).collect(Collectors.toList());

        try {CompletableFuture.allOf(webPagesContentFuturesList.toArray(CompletableFuture[]::new)).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }

        List<String> finalResults = new ArrayList<>();
        webPagesContentFuturesList.forEach(webPageContentFuture -> {
            finalResults.add(webPageContentFuture.join());
        });
        return finalResults;
    }
    private static CompletableFuture<String> downloadWebPage(String pageLink) {
        return CompletableFuture.supplyAsync(() -> {
            String fakeWebPageContent = RandomStringUtils.randomAlphanumeric(pageLink.length());
            return fakeWebPageContent;
        });
    }



    private static void testAnyOf() {
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Result of Future 1";
        });

        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            return "Result of Future 2";
        });

        CompletableFuture<String> future3 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            return "Result of Future 3";
        });

        CompletableFuture<Object> anyOfFuture = CompletableFuture.anyOf(future1, future2, future3);
        //kto pierwszy ten lepszy - będzie to future2, który nie jest usypiany, czyli wykona się najszybciej
        System.out.println(anyOfFuture);//java.util.concurrent.CompletableFuture@3d646c37[Completed normally]
        System.out.println(anyOfFuture.join()); // Result of Future 2
    }

    private static void testExceptionally() throws ExecutionException, InterruptedException {
        //exceptionally() daje szansę na wyratowanie się z sytuacji, gdy zostanie rzucony wyjątek
        //Gdy tak się stanie - zwrócona zostanie wartość defaultowa
        Integer age = -1;
        CompletableFuture<String> maturityFuture = CompletableFuture.supplyAsync(() -> {
                    if(age < 0) {
                        throw new IllegalArgumentException("Age can not be negative");
                    }
                    if(age > 18) {
                        return "Adult";
                    } else {
                        return "Child";
                    }
                }).exceptionally(throwable -> {
                    System.out.println("Oops! We have an exception - " + throwable.getMessage());
                    return "Unknown!";
                }).thenApply(s -> s.toUpperCase(Locale.ROOT))
                .exceptionally(throwable -> {
                    System.out.println("ToUpperCase failed" + throwable.getMessage());
                    return "unknown";
                    //oczywiście .toUpperCase() nie rzuci wyjątkiem więc ta sekcja się nie wykona
                });

        System.out.println("Maturity : " + maturityFuture.get());
        //Oops! We have an exception - java.lang.IllegalArgumentException: Age can not be negative
        //Maturity : UNKNOWN!
    }

    private static void testHandle() throws ExecutionException, InterruptedException {
        //Handle z kolei wykona się nie zależnie od tego, czy wyjątek został rzucony, czy nie
        //Działa podobnie jak blok finally
        Integer age = 5;
        CompletableFuture<String> maturityFuture = CompletableFuture.supplyAsync(() -> {
            if(age < 0) {
                throw new IllegalArgumentException("Age can not be negative");
            }
            if(age > 18) {
                return "Adult";
            } else {
                return "Child";
            }
        }).handle((string, throwable) -> {
            if(throwable != null) {
                System.out.println("Oops! We have an exception - " + throwable.getMessage());
                return "Unknown!";
            }
            //reszta metody handle() wykona się bez względu na to, czy wyjątek się pojawi, czy nie
            System.out.println("Handle method...");
            return string;
        });
        System.out.println("Maturity : " + maturityFuture.get());
        //Handle method...
        //Maturity : Child
    }
}

