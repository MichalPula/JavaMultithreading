package blocking_queues;

import atomic_variables.Apple;

import java.util.concurrent.*;

public class BlockingQueues {
    public static void main(String[] args) throws InterruptedException {
        //differentQueues();
        //testDelayQueue();
        //testPriorityBlockingQueue();
        //testSynchronousQueue();
    }

    private static void differentQueues() {
        BlockingQueue<String> arrayBlockingQueue = new ArrayBlockingQueue<>(5);//bounded
        BlockingQueue<ClassImplementingDelayed> delayQueue = new DelayQueue<>();//unbounded
        BlockingQueue<String> linkedBlockingQueue = new LinkedBlockingQueue<>(5);//mixed
        BlockingQueue<String> linkedBlockingDeque = new LinkedBlockingDeque<>(5);//mixed
        BlockingQueue<String> linkedTransferQueue = new LinkedTransferQueue<>(); //unbounded
        PriorityBlockingQueue<String> priorityBlockingQueue = new PriorityBlockingQueue<>();//unbounded
    }

    private static void testDelayQueue() throws InterruptedException {
        BlockingQueue<ClassImplementingDelayed> delayQueue = new DelayQueue<>();
        delayQueue.add(new ClassImplementingDelayed("A", 1000));//1s
        delayQueue.add(new ClassImplementingDelayed("B", 2000));
        delayQueue.add(new ClassImplementingDelayed("C", 3000));
        delayQueue.add(new ClassImplementingDelayed("D", 4000));
        try{
            Thread.sleep(1100);
            System.out.println(delayQueue.remove());//ClassImplementingDelayed{name='A, delayTime=1641408692953}
            Thread.sleep(1100);
            System.out.println(delayQueue.remove());//ClassImplementingDelayed{name='B, delayTime=1641408728503}
            Thread.sleep(1100);
            System.out.println(delayQueue.remove());//ClassImplementingDelayed{name='C, delayTime=1641408729503}
            Thread.sleep(1100);
            System.out.println(delayQueue.remove());//ClassImplementingDelayed{name='D, delayTime=1641408730503}
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }
    }

    private static void testPriorityBlockingQueue() {
        BlockingQueue<String> priorityBlockingQueue = new PriorityBlockingQueue<>();
        priorityBlockingQueue.offer("5");
        priorityBlockingQueue.offer("4");
        priorityBlockingQueue.offer("3");
        priorityBlockingQueue.offer("3");
        priorityBlockingQueue.offer("3");
        priorityBlockingQueue.offer("2");
        priorityBlockingQueue.offer("1");
        System.out.println(priorityBlockingQueue); //[1, 3, 2, 5, 3, 4, 3]
        System.out.println(priorityBlockingQueue.remove());//1
        System.out.println(priorityBlockingQueue.remove());//2
        System.out.println(priorityBlockingQueue.remove());//3
        System.out.println(priorityBlockingQueue.remove());//3
        System.out.println(priorityBlockingQueue.remove());//3
        System.out.println(priorityBlockingQueue.remove());//4
        System.out.println(priorityBlockingQueue.remove());//5

        PriorityBlockingQueue<String> priorityBlockingQueueReversed = new PriorityBlockingQueue<>
                (11, (o1, o2) -> o2.hashCode() - o1.hashCode());//reversed order
    }

    private static void testSynchronousQueue() throws InterruptedException {
        BlockingQueue<Apple> synchronousQueue = new SynchronousQueue<>();
        ExecutorService executor = Executors.newFixedThreadPool(2);

        Runnable producer = () -> {
            Apple producedElement = new Apple(300);
            System.out.println("I'm giving you apple! " + producedElement);
            synchronousQueue.offer(producedElement);
        };

        Runnable consumer = () -> {
            try {
                Apple consumedElement = synchronousQueue.take();
                System.out.println("I got an apple! <3 " + consumedElement);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        };

        //Na odwrót nie zadziała. Najpierw consumer musi dać znać, że chce element i dopiero wtedy producer może go wstawić do SynchronousQueue
        executor.execute(consumer);
        Thread.sleep(100);
        executor.execute(producer);
        //I'm giving you apple! Apple(weight=300)
        //I got an apple! <3 Apple(weight=300)

        executor.shutdown();
    }
}
