package superstartrek;

import java.io.IOException;
import java.util.HashMap;

public class Enterprise extends GameObject {

    private Condition condition;
    private int photonTorpedoes;
    private int totalEnergy;
    private int shields;

    private GalacticPosition previousPosition;

    public Enterprise() {
        condition = Condition.GREEN;
        photonTorpedoes = 10;
        totalEnergy = 3000;
        shields = 0;
        previousPosition = new GalacticPosition();
    }
    public void setPosition(HashMap<String, GameObject> galaxy, long parsecX, long parsecY) {
        previousPosition.setPosition(getPosition().getParsecX(), getPosition().getParsecY());
        super.setPosition(galaxy, parsecX, parsecY);
        if( previousPosition.getParsecX() == getPosition().getParsecX() &&
            previousPosition.getParsecY() == getPosition().getParsecY()) {
            // ship did not move
            return;
        }
        notifyListeners(new GameEvent(this, GameEvent.EventName.ENTERPRISE_MOVED));
    }
    public void setPosition(HashMap<String, GameObject> galaxy,
                            int quadrantColumn, int quadrantRow,
                            int sectorColumn, int sectorRow) {
        previousPosition.setPosition(getPosition().getParsecX(), getPosition().getParsecY());
        super.setPosition(galaxy, quadrantColumn, quadrantRow, sectorColumn, sectorRow);
        if( previousPosition.getParsecX() == getPosition().getParsecX() &&
                previousPosition.getParsecY() == getPosition().getParsecY()) {
            // ship did not move
            return;
        }
        notifyListeners(new GameEvent(this, GameEvent.EventName.ENTERPRISE_MOVED));
    }
    public GalacticPosition getPreviousPosition() {
        return previousPosition;
    }

    public String toString() {
        return "<*>";
    }

    enum Condition {
        GREEN, RED
    };
}
