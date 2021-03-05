import org.junit.Test;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SessionClient {
    private SessionUtils utils = new SessionUtils();
    private DeliverySession session;

    @Test
    public void Test() {
        ExecutorService threadPool = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            threadPool.execute(new Runnable() {
                public void run() {
                    utils.execute(3);
                }
            });
        }
        while (true) {

        }
    }
}
