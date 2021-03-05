import org.junit.Test;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class SessionClient {
    private SessionUtils utils = new SessionUtils();
    private DeliverySession session;


    @Test
    public void Test() {
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        for (int i = 0; i < 10; i++) {
            cachedThreadPool.execute(new Runnable() {
                public void run() {
                    utils.execute(2);

                }
            });
        }
        while (true) {

        }

    }

    @Test
    public void testXml () {
        session = new DeliverySession(1, DeliverySession.ActionType.Start, 3, 4);
        String ans = utils.jaxbObjectToXml(session);
        System.out.println(ans);

    }







}
