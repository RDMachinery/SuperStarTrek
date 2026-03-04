package superstartrek;

public class GalacticPosition {

    /*
     * The width & height of the galaxy in parsecs. These must be multiples of
     * 64*8, or bad Joo-Joo will happen with ship navigation.
     */
    public static final int GALACTIC_WIDTH_PARSECS = 12 * 64 * 8;
    public static final int GALACTIC_HEIGHT_PARSECS = 4 * 64 * 8;

    /*
     * There are 8 quadrants across, and 8 quadrants down, making 64 quadrants
     * in the galaxy.
     */
    public static final int QUADRANT_WIDTH_PARSECS = GALACTIC_WIDTH_PARSECS / 8;
    public static final int QUADRANT_HEIGHT_PARSECS = GALACTIC_HEIGHT_PARSECS / 8;

    /*
     * Within each of the quadrants, we have 8 sectors across and 8 sectors down
     * giving 64 sectors per quadrant.
     */
    public static final int SECTOR_WIDTH_PARSECS = QUADRANT_WIDTH_PARSECS / 8;
    public static final int SECTOR_HEIGHT_PARSECS = QUADRANT_HEIGHT_PARSECS / 8;

    private long parsecX;
    private long parsecY;

    private int quadrantRow;
    private int quadrantColumn;
    private int sectorRow;
    private int sectorColumn;


    public static final String[] GALACTIC_REGION_NAMES = {
            "Antares","Sirius","Rigel","Deneb","Procyon","Capella","Vega",
            "Betelgeuse","Canopus","Aldebaran","Altair","Regulus","Sagittarius",
            "Arcturus","Pollux","Spica"};

    public static final String[] GALACTIC_QUADRANT_NAMES = {"I","II","III","IV"};
    private int anInt;

    public GalacticPosition() {}

    public void setPosition(long parsecX, long parsecY) {
        this.parsecX = parsecX;
        this.parsecY = parsecY;
        parsecToQuadrantSector();
    }

    public void setPosition(int quadrantColumn, int quadrantRow, int sectorColumn, int sectorRow) {
        this.quadrantRow = quadrantRow; this.quadrantColumn = quadrantColumn;
        this.sectorRow = sectorRow; this.sectorColumn = sectorColumn;
        quadrantSectorToParsec();
        //System.out.println("GaslacticPosition.setPos() = " + toString());
    }

    private void parsecToQuadrantSector() {
        quadrantColumn = ((int) parsecX / QUADRANT_WIDTH_PARSECS) % 8;
        quadrantRow = ((int) parsecY / QUADRANT_HEIGHT_PARSECS) % 8;
        sectorColumn = ((int) parsecX / SECTOR_WIDTH_PARSECS) % 8;
        sectorRow = ((int) parsecY / SECTOR_HEIGHT_PARSECS) % 8;
    }

    private void quadrantSectorToParsec() {
        parsecX = quadrantColumn * QUADRANT_WIDTH_PARSECS;
        parsecY = quadrantRow * QUADRANT_HEIGHT_PARSECS;
        parsecX += sectorColumn * SECTOR_WIDTH_PARSECS;
        parsecY += sectorRow * SECTOR_HEIGHT_PARSECS;
    }

    public String getQuadrantName() {
        if( isOutsideGalaxy() )
            return "The Void";

        long quadrantNumber = getQuadrantNumber();
        return GALACTIC_REGION_NAMES[ (int) quadrantNumber / 4 ] +
                " " + GALACTIC_QUADRANT_NAMES[ (int) quadrantNumber % 4 ];
    }

    public long getQuadrantNumber() {
        return parsecX / QUADRANT_WIDTH_PARSECS + (parsecY / QUADRANT_HEIGHT_PARSECS) * 8;
    }

    public boolean isOutsideGalaxy() {
        return parsecX < 0 || parsecX >= GALACTIC_WIDTH_PARSECS ||
                parsecY < 0 || parsecY >= GALACTIC_HEIGHT_PARSECS;
    }
    public boolean quadrantContains(long parsecX, long parsecY) {
        long quadColumnParsec = quadrantColumn * QUADRANT_WIDTH_PARSECS;
        long quadrantRowParsec = quadrantRow * QUADRANT_HEIGHT_PARSECS;
        long quadColumnParsecMax = quadColumnParsec + QUADRANT_WIDTH_PARSECS - 1;
        long quadRowParsecMax = quadrantRowParsec + QUADRANT_HEIGHT_PARSECS - 1;

        //System.out.println("Bounds for " + quadrantColumn + ", " + quadrantRow);
        //System.out.println("qc = " + quadColumnParsec + " qr = " + quadrantRowParsec +
        //        " qcMax = " + quadColumnParsecMax + " qrMax = " + quadRowParsecMax);
        if( parsecX >= quadColumnParsec && parsecX <= quadColumnParsecMax &&
            parsecY >= quadrantRowParsec && parsecY <= quadRowParsecMax)
            return true;

        return false;
    }
    public boolean sectorContains(long parsecX, long parsecY) {
        long quadColumnParsec = quadrantColumn * QUADRANT_WIDTH_PARSECS;
        long quadRowParsec = quadrantRow * QUADRANT_HEIGHT_PARSECS;

        long sectorColumnParsec = quadColumnParsec + sectorColumn * SECTOR_WIDTH_PARSECS;
        long sectorRowParsec = quadRowParsec + sectorRow * SECTOR_HEIGHT_PARSECS;
        long sectorColumnParsecMax =  sectorColumnParsec + SECTOR_WIDTH_PARSECS - 1;
        long sectorRowParsecMax = sectorRowParsec + SECTOR_HEIGHT_PARSECS - 1;

        if( parsecX >= sectorColumnParsec && parsecX <= sectorColumnParsecMax &&
            parsecY >= sectorRowParsec && parsecY <= sectorRowParsecMax )
            return true;
        return false;
    }

    public long getParsecX() {
        return parsecX;
    }

    public long getParsecY() {
        return parsecY;
    }

    public int getQuadrantRow() {
        return quadrantRow;
    }

    public int getQuadrantColumn() {
        return quadrantColumn;
    }

    public int getSectorRow() {
        return sectorRow;
    }

    public int getSectorColumn() {
        return sectorColumn;
    }

    public final double distance(GalacticPosition pos) {
        Vector v1 = new Vector(parsecX, parsecY);
        Vector v2 = new Vector(pos.getParsecX(), pos.getParsecY());
        return v1.dist(v2);
    }

    public String toString() {
        return "Quadrant Number = " + getQuadrantNumber() +
                " QuadrantColumn = " + quadrantColumn + ", QuadrantRow = " + quadrantRow +
                ", SectorColumn = " + sectorColumn + ", SectorRow = " + sectorRow +
                ", parsecX = " + parsecX + ", parsecY = " + parsecY;
    }
}
