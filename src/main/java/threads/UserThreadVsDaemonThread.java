package threads;

public class UserThreadVsDaemonThread {

    public static void main(String[] args) throws InterruptedException {

        Runnable userRunnable = () -> {
            System.out.println("Run inside userThread");
            int x = 5;
            while (x > 0) {
                x--;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) { ex.printStackTrace(); }
            }
        };

        Runnable daemonRunnable = () -> {
            System.out.println("Run inside daemonThread");
            while (true) {
                System.out.println("Daemon thread is running...");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) { ex.printStackTrace(); }
            }
        };

        Thread userThread = new Thread(userRunnable, "UserThread");
        Thread daemonThread = new Thread(daemonRunnable, "DaemonThread");

        daemonThread.setDaemon(true);
        userThread.start();
        daemonThread.start();
    }
}
