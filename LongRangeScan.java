package superstartrek;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LongRangeScan {

    private ScanResult[][] scanResult;

    public LongRangeScan() {}

    public ScanResult[][] scan(HashMap<String, GameObject> galaxy, GalacticPosition pos) {

        scanResult = new ScanResult[3][3];
        int quadCol = pos.getQuadrantColumn();
        int quadRow = pos.getQuadrantRow();
        GalacticPosition pos1 = new GalacticPosition();
        pos1.setPosition(quadCol-1, quadRow - 1,0,0);
        GalacticPosition pos2 = new GalacticPosition();
        pos2.setPosition(quadCol, quadRow - 1,0,0);
        GalacticPosition pos3 = new GalacticPosition();
        pos3.setPosition(quadCol+1, quadRow - 1,0,0);
        GalacticPosition pos4 = new GalacticPosition();
        pos4.setPosition(quadCol-1, quadRow,0,0);
        GalacticPosition pos5 = new GalacticPosition();
        pos5.setPosition(quadCol, quadRow,0,0);
        GalacticPosition pos6 = new GalacticPosition();
        pos6.setPosition(quadCol+1, quadRow,0,0);
        GalacticPosition pos7 = new GalacticPosition();
        pos7.setPosition(quadCol-1, quadRow + 1,0,0);
        GalacticPosition pos8 = new GalacticPosition();
        pos8.setPosition(quadCol, quadRow + 1,0,0);
        GalacticPosition pos9 = new GalacticPosition();
        pos9.setPosition(quadCol+1, quadRow + 1,0,0);

        ScanResult sr1 = new ScanResult(pos1); sr1.scan(galaxy);
        ScanResult sr2 = new ScanResult(pos2); sr2.scan(galaxy);
        ScanResult sr3 = new ScanResult(pos3); sr3.scan(galaxy);
        ScanResult sr4 = new ScanResult(pos4); sr4.scan(galaxy);
        ScanResult sr5 = new ScanResult(pos5); sr5.scan(galaxy);
        ScanResult sr6 = new ScanResult(pos6); sr6.scan(galaxy);
        ScanResult sr7 = new ScanResult(pos7); sr7.scan(galaxy);
        ScanResult sr8 = new ScanResult(pos8); sr8.scan(galaxy);
        ScanResult sr9 = new ScanResult(pos9); sr9.scan(galaxy);

        scanResult[0][0] = sr1;
        scanResult[0][1] = sr2;
        scanResult[0][2] = sr3;
        scanResult[1][0] = sr4;
        scanResult[1][1] = sr5;
        scanResult[1][2] = sr6;
        scanResult[2][0] = sr7;
        scanResult[2][1] = sr8;
        scanResult[2][2] = sr9;

        return scanResult;
    }

    public ArrayList<GameObject> getGameObjectsInQuadrant(HashMap<String, GameObject> galaxy, GalacticPosition quadrant) {
        ArrayList<GameObject> list = new ArrayList<>();
        Iterator iter = galaxy.entrySet().iterator();
        while( iter.hasNext()) {
            Map.Entry pair = (Map.Entry)iter.next();
            GalacticPosition pos = galaxy.get(pair.getKey()).getPosition();
            if( quadrant.quadrantContains(pos.getParsecX(), pos.getParsecY())) {
                list.add(galaxy.get(pair.getKey()));

            }
            //siter.remove(); // Avoids concurrent modification
        }

        return list;
    }
    public class ScanResult {
        public int numStars;
        public int numStarBases;
        public int numKlingons;
        private GalacticPosition pos;

        public ScanResult(GalacticPosition pos) {
            this.pos = pos;
        }
        public void scan(HashMap<String, GameObject> galaxy) {
            if( pos.isOutsideGalaxy() ) {
                numStars = -1;
                numStarBases = -1;
                numKlingons = -1;
                return;
            }
            ArrayList<GameObject> list = getGameObjectsInQuadrant(galaxy, pos);
            for(int i=0; i<list.size(); i++) {
                GameObject go = list.get(i);
                if (go instanceof Star)
                    numStars++;
                else if (go instanceof StarBase)
                    numStarBases++;
                else if (go instanceof Klingon)
                    numKlingons++;
            }
            //System.out.println(toString());
        }
        public String toString() {
            return " " + numKlingons + "" + numStarBases + "" + numStars + " ";
        }
    }
}
