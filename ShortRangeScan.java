package superstartrek;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ShortRangeScan {

    private SRScanResult[][] scanResult;

    public ShortRangeScan() {
    }

    public SRScanResult[][] scan(HashMap<String, GameObject> galaxy, GalacticPosition quadrant) {
        scanResult = new SRScanResult[8][8];
        for(int sectorColumn = 0; sectorColumn<8; sectorColumn++) {
            for(int sectorRow = 0; sectorRow<8; sectorRow++) {
                GalacticPosition gp = new GalacticPosition();
                gp.setPosition(quadrant.getQuadrantColumn(), quadrant.getQuadrantRow(),
                        sectorColumn, sectorRow);
                SRScanResult srs = new SRScanResult(gp);
                scanResult[sectorRow][sectorColumn] = srs;
                srs.scan(galaxy);
            }
        }
        return scanResult;
    }

    private ArrayList<GameObject> getGameObjectsInSector(HashMap<String, GameObject> galaxy, GalacticPosition sector) {
        ArrayList<GameObject> list = new ArrayList<>();
        Iterator iter = galaxy.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry pair = (Map.Entry) iter.next();
            GalacticPosition pos = galaxy.get(pair.getKey()).getPosition();
            if (sector.sectorContains(pos.getParsecX(), pos.getParsecY())) {
                list.add(galaxy.get(pair.getKey()));
            }

        }
        return list;
    }

    public class SRScanResult {
        GalacticPosition sector;
        public int numKlingons;
        public int numStarBases;
        public int numStars;
        public int numShips;

        public SRScanResult(GalacticPosition sector) {
            this.sector = sector;
        }

        public void scan(HashMap<String, GameObject> galaxy) {
            ArrayList<GameObject> objects = getGameObjectsInSector(galaxy, sector);
            for(int i=0; i<objects.size(); i++) {
                GameObject go = objects.get(i);
                if( go instanceof Klingon )
                    numKlingons++;
                else if( go instanceof StarBase )
                    numStarBases++;
                else if( go instanceof  Star )
                    numStars++;
                else if( go instanceof Enterprise )
                    numShips++;
            }
        }
    }
}
