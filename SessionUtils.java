import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

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
    private DeliverySession session;
    //private static int aliveTime = 2;
    private static String orignUrl = "http://localhost:8081/nbi/deliverysession";

    public synchronized void doPost(String url, DeliverySession session) {
        String xml = jaxbObjectToXml(session);
        CloseableHttpClient client = null;
        CloseableHttpResponse resp = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type", "text/xml; charset=UTF-8");
            //client = HttpClients.createDefault();
            HttpEntity entityParams = new StringEntity(xml,"utf-8");
            httpPost.setEntity(entityParams);
            //client = HttpClients.createDefault();
            resp = client.execute(httpPost);
            String resultMsg = EntityUtils.toString(resp.getEntity(),"utf-8");

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
        }



    }

    // convert java object to xml
    public String jaxbObjectToXml (DeliverySession session) {
        String xmlContent = null;
        try {
            //Create JAXB Context
            JAXBContext jaxbContext = JAXBContext.newInstance(DeliverySession.class);
            //Create Marshaller
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            //Required formatting??
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            //Print XML String to Console
            StringWriter sw = new StringWriter();
            //Write XML to StringWriter
            jaxbMarshaller.marshal(session, sw);
            //Verify XML Content
            xmlContent = sw.toString();
        } catch (JAXBException e) {
            e.printStackTrace();
        } finally {
            return xmlContent;
        }
    }

    // execute sending request
    public void execute(int aliveTime) {
        // start session
        // get sessionId
        Random rand = new Random();
        int sessionId = rand.nextInt(100);
        final String curUrl = orignUrl+"?id="+String.valueOf(sessionId);


        // get start time
        int startTime = (int) (new Date().getTime()/1000);
        // get end time
        int endTime = startTime+aliveTime;
        session = new DeliverySession(sessionId, DeliverySession.ActionType.Start, startTime, endTime);
        doPost(curUrl, session);

        // end session
        session = new DeliverySession(sessionId, DeliverySession.ActionType.Stop, startTime, endTime);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                doPost(curUrl, session);
            }
        }, aliveTime*1000 );

    }

}

