package producer_consumer.blocking_queue;

import java.util.Random;
import java.util.concurrent.LinkedTransferQueue;

public class ProducerConsumerLinkedTransferQueue {
    public static void main(String[] args) {

        LinkedTransferQueue<Integer> linkedTransferQueue = new LinkedTransferQueue<>();

        new Thread(() -> {
            Random random = new Random(1);
            try {
                while (true) {
                    System.out.println("Producer is waiting to transfer message...");
                    Integer message = random.nextInt();
                    boolean added = linkedTransferQueue.tryTransfer(message);
                    if(added) {
                        System.out.println("Producer added the message - " + message);
                    }
                    Thread.sleep(2000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                while (true) {
                    System.out.println("Consumer is waiting to take message...");
                    Integer message = linkedTransferQueue.take();
                    System.out.println("Consumer recieved the message - " + message);
                    Thread.sleep(2000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
