import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class SessionUtils {
    private final static Logger logger = LoggerFactory.getLogger(SessionUtils.class);
    private DeliverySession session;
    private static String orignUrl = "http://127.0.0.1:8081/nbi/deliverysession";

    // send post request and get response from server
    public String doPost(String url, DeliverySession session) {
        String xml = jaxbObjectToXml(session);
        CloseableHttpClient client = null;
        CloseableHttpResponse resp = null;
        String resultMsg = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type", "text/xml; charset=UTF-8");
            client = HttpClients.createDefault();
            HttpEntity entityParams = new StringEntity(xml,"utf-8");
            httpPost.setEntity(entityParams);
            resp = client.execute(httpPost);
            if (resp.getStatusLine().getStatusCode() == 200) {
                resultMsg = "200 OK";
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try {
                if(client!=null){
                    client.close();
                }
                if(resp != null){
                    resp.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return resultMsg;
        }
    }

    // convert java object to xml
    public String jaxbObjectToXml (DeliverySession session) {
        String xmlContent = null;
        try {
            //Create JAXB Context
            JAXBContext jaxbContext = JAXBContext.newInstance(DeliverySession.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            StringWriter sw = new StringWriter();
            jaxbMarshaller.marshal(session, sw);
            xmlContent = sw.toString();
        } catch (JAXBException e) {
            e.printStackTrace();
        } finally {
            return xmlContent;
        }
    }

    // execute sending request
    public synchronized void execute(int aliveTime) {
        // start session
        // get sessionId && startTime
        Random rand = new Random();
        int randNum = rand.nextInt(1000);
        final int startTime = (int) (System.currentTimeMillis());
        final int sessionId = randNum;
        final String curUrl = orignUrl+"?id="+String.valueOf(sessionId);

        // get end time
        final int endTime = startTime+aliveTime*1000;
        session = new DeliverySession(sessionId, DeliverySession.ActionType.Start, startTime, endTime);
        String startXmlContent = getXml(session);
        final String rep1 = doPost(curUrl, session);
        logger.info("Current sessionId: " + sessionId + "\n" + "Send time: " + startTime + "\n"
                 + "url: " + curUrl + "\n" + "Request body: "
                + startXmlContent + "Response: " + rep1);

        // end session
        session = new DeliverySession(sessionId, DeliverySession.ActionType.Stop, startTime, endTime);
        String endXmlContent = getXml(session);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                String rep2 = doPost(curUrl, session);
                logger.info("Current sessionId: " + sessionId + "\n" + "Send time: " + endTime + "\n"
                         + "url: " + curUrl + "\n" + "Request body: "
                        + endXmlContent + "Response: " + rep2);
            }
        }, aliveTime*1000 );
    }


    private String getXml (DeliverySession session) {
        return jaxbObjectToXml(session);
    }

}