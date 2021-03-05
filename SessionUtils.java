import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

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
    private static Logger logger = Logger.getLogger(SessionUtils.class);
    private DeliverySession session;
    //private static int aliveTime = 2;
    private static String orignUrl = "http://127.0.0.1:8081/nbi/deliverysession";

    public synchronized String doPost(String url, DeliverySession session) {
        String xml = jaxbObjectToXml(session);
        CloseableHttpClient client = null;
        CloseableHttpResponse resp = null;
        String resultMsg = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type", "text/xml; charset=UTF-8");
            //client = HttpClients.createDefault();
            HttpEntity entityParams = new StringEntity(xml,"utf-8");
            //System.out.println(entityParams);
            httpPost.setEntity(entityParams);
            //client = HttpClients.createDefault();
            resp = client.execute(httpPost);
            resultMsg = EntityUtils.toString(resp.getEntity(),"utf-8");

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
        // get sessionId && startTime
        Random rand = new Random();
        int randNum = rand.nextInt(100);
        final int startTime = (int) (new Date().getTime()/1000);
        final int sessionId = getSessionId(startTime, randNum);
        final String curUrl = orignUrl+"?id="+String.valueOf(sessionId);


        // get end time
        final int endTime = startTime+aliveTime;
        session = new DeliverySession(sessionId, DeliverySession.ActionType.Start, startTime, endTime);
        final String rep1 = doPost(curUrl, session);
        logger.info("Current sessionId: " + sessionId + "\n" + "Send time: " + startTime + "\n"
                + "Request body: " + getXml(session) + "\n" + "Response: " + rep1);
        //System.out.println(rep1);

        // end session
        session = new DeliverySession(sessionId, DeliverySession.ActionType.Stop, startTime, endTime);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                String rep2 = doPost(curUrl, session);
                logger.info("Current sessionId: " + sessionId + "\n" + "Send time: " + endTime + "\n"
                        + "Request body: " + getXml(session) + "\n" + "Response: " + rep2);
                //System.out.println(rep2);

            }
        }, aliveTime*1000 );

    }

    private int getSessionId(int startTime, int randNum) {
        StringBuilder sb = new StringBuilder();
        sb.append(startTime).append(randNum);
        int ans = Integer.valueOf(sb.toString());
        return ans;
    }

    private String getXml (DeliverySession session) {
        return jaxbObjectToXml(session);
    }

}

