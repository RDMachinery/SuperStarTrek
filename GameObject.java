package superstartrek;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class GameObject {

    private GalacticPosition position;
    private ArrayList<GameListener> listeners;

    public GameObject() {

        position = new GalacticPosition();
        listeners = new ArrayList<>();
    }

    protected void notifyListeners(GameEvent evt) {
        for(int i=listeners.size()-1; i>=0; i--) {
            listeners.get(i).handleEvent(evt, evt.getEventName());
        }
    }

    public void addListener(GameListener l) {
        listeners.add(l);
    }

    public void removeListener(GameListener l) {
        listeners.remove(l);
    }

    public void setPosition(HashMap<String, GameObject> galaxy, long parsecX, long parsecY) {
        galaxy.remove(getKey());
        position.setPosition(parsecX, parsecY);
        galaxy.put(getKey(), this);
    }

    public void setPosition(HashMap<String, GameObject> galaxy, int quadrantColumn, int quadrantRow, int sectorColumn, int sectorRow) {
        galaxy.remove(getKey());
        position.setPosition(quadrantColumn, quadrantRow, sectorColumn, sectorRow);
        galaxy.put(getKey(), this);
    }

    public GalacticPosition getPosition() {
        GalacticPosition pos = new GalacticPosition();
        pos.setPosition(position.getParsecX(), position.getParsecY());
        return pos;
    }

    public String getKey() {
        return position.getParsecX() + ":" + position.getParsecY();
    }
}
