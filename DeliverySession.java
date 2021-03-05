import javafx.beans.binding.MapExpression;

import javax.xml.bind.annotation.*;

@XmlRootElement
public class DeliverySession {

    @XmlEnum(String.class)
    public enum ActionType {
        @XmlEnumValue("Start")
        Start("Start"),
        @XmlEnumValue("stop")
        Stop("Stop");

        private final String value;
        ActionType(String v) {
            value = v;
        }
        public String value() {
            return value;
        }

        public static ActionType fromValue(String v) {
            for (ActionType c: ActionType.values()) {
                if (c.value.equals(v)) {
                    return c;
                }
            }
            throw new IllegalArgumentException(v.toString());
        }
    }
    private int DeliverySessionId;
    private ActionType Action;

    @XmlElements(value = {
            @XmlElement(name="TMGIPool",
                    type=String.class),
            @XmlElement(name="TMGI",
                    type=String.class)
    })
    private Object Tmg;
    private int StartTime;
    private int EndTime;
    public DeliverySession() {};


    public DeliverySession(int DeliverySessionId, ActionType Action, int StartTime, int EndTime) {
        this.DeliverySessionId = DeliverySessionId;
        this.Action = Action;
        this.StartTime = StartTime;
        this.EndTime = EndTime;
    }


    public int getDeliverySessionId() {
        return DeliverySessionId;
    }

    @XmlElement
    public void setDeliverySessionId(int deliverySessionId) {
        DeliverySessionId = deliverySessionId;
    }

    public int getStartTime() {
        return StartTime;
    }

    @XmlElement
    public void setStartTime(int startTime) {
        StartTime = startTime;
    }

    public int getEndTime() {
        return EndTime;
    }

    @XmlElement
    public void setEndTime(int endTime) {
        EndTime = endTime;
    }

    public ActionType getActionType() {
        return Action;
    }

    @XmlElement
    public void setActionType(ActionType action) {
        Action = action;
    }
}
