import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.http.util.EntityUtils;


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
    }



    @Test
    public void testXml () {
        session = new DeliverySession(1, DeliverySession.ActionType.Start, 3, 4);
        String ans = utils.jaxbObjectToXml(session);
        System.out.println(ans);

    }







}
