package superstartrek;

import java.io.IOException;

public interface GameListener {

    void handleEvent(GameEvent evt, GameEvent.EventName eventName);
}
