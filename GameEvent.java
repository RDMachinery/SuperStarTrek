package superstartrek;

public class GameEvent {


    private GameObject source;


    private EventName eventName;

    public GameEvent(GameObject source, EventName eventName) {
        this.source = source;
        this.eventName = eventName;
    }

    public GameObject getSource() {
        return source;
    }

    public EventName getEventName() {
        return eventName;
    }

    enum EventName {
        ENTERPRISE_MOVED;
    }
}
