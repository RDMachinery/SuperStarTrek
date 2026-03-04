package superstartrek;

import java.io.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.StringTokenizer;

public class Run implements GameListener {

    public static final int MAX_KLINGONS = 8;
    public static final int MAX_STAR_BASES = 3;

    public final static double EPSILON = 1e-12;

    private HashMap<String, GameObject> galaxy;

    private Enterprise ship;
    private int stardate;

    private BufferedReader bufIn;
    private BufferedWriter bufOut;

    private static final String[] COMMANDS = {"nav","lrs","srs","help","xxx","goto"};

    private static Random rnd;
    private long seed = 7276431L;

    private int performance; // 0..100%

    public Run() {
        galaxy = new HashMap<>();
        bufIn = new BufferedReader(new InputStreamReader(System.in));
        bufOut = new BufferedWriter(new OutputStreamWriter(System.out));
    }

    private void reset() {
        ship = new Enterprise();
        galaxy.clear();
        rnd = new Random(seed);
        stardate = 3000;
        performance = 100;
        placeStars();
        placeKlingons();
        placeStarBases();
        placeShip();

        ship.addListener(this);
    }

    private void placeStars() {

        int sw = GalacticPosition.GALACTIC_WIDTH_PARSECS;
        int sh = GalacticPosition.GALACTIC_HEIGHT_PARSECS;
        // 64 x 64. I can't remember why this dimension works. It is late and I am not looking it up :)
        for(int i=0; i < 64 * 64; i++) {
            for(int j=0; j < 5; j++) {
                int rndX = nextInt(0, sw);
                int rndY = nextInt(0, sh);
                Star s = new Star();
                s.setPosition(galaxy, rndX, rndY);
            }
        }
    }

    private void placeKlingons() {
        int gw = GalacticPosition.GALACTIC_WIDTH_PARSECS;
        int gh = GalacticPosition.GALACTIC_HEIGHT_PARSECS;
        int rndX = 0;
        int rndY = 0;
        for(int i=0; i<MAX_KLINGONS; i++) {
            do {
                rndX = nextInt(0, gw);
                rndY = nextInt(0, gh);
            } while( ! isNull(rndX, rndY));
            Klingon k = new Klingon();
            k.setPosition(galaxy, rndX, rndY);
            System.out.println("Run.placeKlingons() Klingon #"+i+" position = " + k.getPosition());
        }
    }

    private void placeStarBases() {
        int gw = GalacticPosition.GALACTIC_WIDTH_PARSECS;
        int gh = GalacticPosition.GALACTIC_HEIGHT_PARSECS;
        int rndX = 0;
        int rndY = 0;
        for(int i=0; i<MAX_STAR_BASES; i++) {
            do {
                rndX = nextInt(0, gw);
                rndY = nextInt(0, gh);
            } while( ! isNull(rndX, rndY));
            StarBase s = new StarBase();
            s.setPosition(galaxy, rndX, rndY);
            //System.out.println("Run().placeStarBases() Star Base #"+i+" position = " + s.getPosition());
        }
    }

    private void placeShip() {
        int gw = GalacticPosition.GALACTIC_WIDTH_PARSECS;
        int gh = GalacticPosition.GALACTIC_HEIGHT_PARSECS;
        int rndX = 0;
        int rndY = 0;
        do {
            rndX = nextInt(0, gw);
            rndY = nextInt(0, gh);
        } while( ! isNull(rndX, rndY));
        ship.setPosition(galaxy, rndX, rndY);
    }

    private boolean isNull(int parsecX, int parsecY) {
        boolean nl = true;
        if( galaxy.get(parsecX + ":" + parsecY) != null )
            nl = false;
        return nl;
    }

    public void init() throws IOException {
        boolean playAgain = false;
        do{
            reset();
            commandLoop();
            playAgain = askPlayAgain();
        }while( playAgain );
    }

    private boolean askPlayAgain() throws IOException {
        boolean playAgain = false;
        out("Play again [Y:N] ? ");
        String s = getString();
        playAgain = s.equalsIgnoreCase("y");

        if( playAgain ) {
            out("Same setup [Y:N] ? ");
            s = getString();
            if( "n".equalsIgnoreCase(s)) {
                // This will give the player a new galaxy setup
                seed = (int) Math.random() * 100000;
            }
        }
        return playAgain;
    }

    private void commandLoop() throws IOException {
        boolean gameOver = false;
        out("Your mission begins with your starship located in the\n");
        out("Galactic Quadrant, '" + ship.getPosition().getQuadrantName() + "'.\n");
        printPosition();
        out("\n");
        while( ! gameOver ) {
            try {
                out("Enter command: ");
                String cmd = inputCommand();
                if( "nav".equalsIgnoreCase(cmd) ) {
                    inputCourse();
                } else if("xxx".equalsIgnoreCase(cmd)) {
                    resignCommission();
                    gameOver = true;
                } else if("lrs".equalsIgnoreCase(cmd)) {
                    longRangeScan();
                } else if("srs".equalsIgnoreCase(cmd)) {
                    shortRangeScan();
                } else if("help".equalsIgnoreCase(cmd)) {
                    inputHelp();
                } else if("goto".equalsIgnoreCase(cmd)) {
                    gotoPosition();
                } else {
                    out("Unknown command. Permitted commands are:\n");
                    printCommandList();
                    out("\n");
                }
            }catch(IOException ioe) {
                out("Spock says,\"Most illogical Captain. "+
                        "The command computer seems to be malfunctioning.\": " + ioe.getMessage() + "\n");
            }
        }
    }

    private String inputCommand() throws IOException {
        String cmd = getString();
        for(int i = 0; i< COMMANDS.length; i++) {
           if( COMMANDS[i].equalsIgnoreCase(cmd))
                return cmd;
            }

        return "";
    }


    private void printPosition() throws IOException {
        GalacticPosition shipPos = ship.getPosition();
        out("\n");
        out(shipPos.getQuadrantName() + "\n");
        int qc = shipPos.getQuadrantColumn() + 1;
        int qr = shipPos.getQuadrantRow() + 1;
        int sc = shipPos.getSectorColumn() + 1;
        int sr = shipPos.getSectorRow() + 1;

        if( ! shipPos.isOutsideGalaxy() ) {
            out("Quadrant: " + qc + ", " + qr +
                    " Sector: " + sc + ", " + sr);
            out("\n");
        } else {
            out("Quadrant: XX, XX Sector: XX, XX");
            out("\n");
        }
        int quadColumn = shipPos.getQuadrantColumn();
        int quadRow = shipPos.getQuadrantRow();
        int secColumn = shipPos.getSectorColumn();
        int secRow = shipPos.getSectorRow();
        GalacticPosition gpos = new GalacticPosition();
        gpos.setPosition(quadColumn, quadRow, secColumn, secRow);

        StringBuffer border = new StringBuffer();
        for(int i=0; i<GalacticPosition.SECTOR_WIDTH_PARSECS; i++)
            border.append('_');
        out(border + "\n");

        for(long parsecY=gpos.getParsecY(); parsecY<gpos.getParsecY() + GalacticPosition.SECTOR_HEIGHT_PARSECS - 1; parsecY++) {
            for(long parsecX=gpos.getParsecX(); parsecX<gpos.getParsecX() + GalacticPosition.SECTOR_WIDTH_PARSECS - 1; parsecX++) {
                GameObject go = galaxy.get(parsecX + ":" + parsecY);
                String s = go == null ? " " : go.toString();
                out(s);
            }
            out("\n");
        }

        out(border + "\n");
        out("\n");
    }

    private void inputCourse()  throws IOException{
        double direction = 0;
        double warpFactor = 0;

        try {
            direction = inputDirection();
            warpFactor = inputWarpFactor();
            moveShip(direction, warpFactor);
        } catch(NumberFormatException nfe) {
            out("Uhuru: \"Message received from Starfleet, Captain\"\n");
            out("\n\t'Supplying improper navigation course data can lead to your summary dismissal!\n");
            decPerformance(10);
        }
    }

    private void resignCommission() throws IOException {
        out("Uhuru says, \"An incoming message from Admiral Jameson, Captain.\"\n");
        out("\n\t'Your resignation has been accepted by Starfleet Command. I wish\n");
        out("\t you luck in your future endeavours.'\n\n");
    }

    private void longRangeScan() throws IOException {
        out("Long Range Scan for Quadrant " + (ship.getPosition().getQuadrantColumn()+1) + "," +
                (ship.getPosition().getQuadrantRow() + 1) + "\n");
        LongRangeScan scanner = new LongRangeScan();
        LongRangeScan.ScanResult[][] result = scanner.scan(galaxy, ship.getPosition());
        String s = null;
        out("----------------------------------\n");
        for(int row=0; row<3; row++) {
            out(":");
            for(int column=0; column<3; column++) {
                LongRangeScan.ScanResult sr = result[row][column];
                out(" ");
                s = sr.numKlingons == -1 ? "#" : sr.numKlingons + "";
                out(s+" ");
                s = sr.numStarBases == -1 ? "#" : sr.numStarBases + "";
                out(s+" ");
                s = sr.numStars == -1 ? "###" : sr.numStars + "";
                out(s+" ");
                out(" ");
                if( column < 2)
                    out(":");
            }
            out(":");
            out("\n");
        }
        out("----------------------------------\n");
    }

    private void shortRangeScan() throws IOException {
        out("Short Range Scan for Quadrant " + (ship.getPosition().getQuadrantColumn()+1) + "," +
                (ship.getPosition().getQuadrantRow() + 1) + "\n");
        ShortRangeScan srs = new ShortRangeScan();
        ShortRangeScan.SRScanResult[][] result = srs.scan(galaxy, ship.getPosition());
        String s = "";

        ArrayList<ArrayList> rows = new ArrayList<>();

        for(int row=0; row<8; row++) {
            rows.add(new ArrayList<ArrayList<String>>());
            for(int column=0; column<8; column++) {
                ShortRangeScan.SRScanResult sr = result[row][column];
                ArrayList<String> cellList = getCellList(sr.numKlingons, sr.numStarBases, sr.numStars, sr.numShips);

                rows.get(row).add(cellList);
                if( row == 1 && column == 5 ) {
                    cellList.add("Item ABCDEF");
                    cellList.add("Item B");
                    cellList.add("Item C");

                }
            }
        }
        TextBoxGrid stf = new TextBoxGrid();
        stf.format(rows);
    }

    private ArrayList<String> getCellList(int numKlingons, int numStarBases, int numStars, int numShips) {
        ArrayList<String> cell = new ArrayList<>();
        if( numKlingons > 0) {
            cell.add("+K+ x " + numKlingons);
        }
        if( numStarBases > 0) {
            cell.add(">!< x " + numStarBases );
        }
        if( numStars > 0) {
            cell.add("* x " + numStars);
        }
        if( numShips > 0) {
            cell.add("<*> x " + numShips);
        }

        return cell;
    }

    private void printCommandList()  throws IOException{
        out("\tNAV - Warp engine control.\n");
        out("\tSRS - Short Range Scan.\n");
        out("\tLRS - Long Range Scan.\n");
        out("\tXXX - Resign your commission.\n");
        out("\tHELP - Print the help information for a given command.\n");
    }

    private void inputHelp() throws IOException {
        out("Enter the command you need help with: ");
        String cmd = getString();
        if( cmd == null || cmd.equals("")) {
            return;
        }
        throw new IOException("inputHelp() not implemented.");
    }
    private void gotoPosition() throws IOException {
        out("Enter position quad column, quad row, sec column, sec row: ");
        String s = getString();
        StringTokenizer st = new StringTokenizer(s,",");
        int qc = Integer.parseInt(st.nextToken());
        int qr = Integer.parseInt(st.nextToken());
        int sc = Integer.parseInt(st.nextToken());
        int sr = Integer.parseInt(st.nextToken());
        ship.setPosition(galaxy, qc,qr,sc,sr);
    }
    private double inputDirection() throws IOException, NumberFormatException {
        double direction = -1;
        out("Enter heading [1-9]: ");
        String dir = getString();
        try {
            direction = Double.parseDouble(dir);
            if( direction >= 9 )
                direction = 1.0;
            if( direction < 1.0 )
                throw new NumberFormatException();
        } catch(NumberFormatException e) {
            out("Sulu reports: \"That is not a valid heading, Sir.\"\n");
            throw e;
        }
        return direction;
    }

    private double inputWarpFactor() throws IOException, NumberFormatException {
        double warpFactor = -1;
        out("Enter Warp Factor: [0-8]: ");
        String wf = getString();
        try {
            warpFactor = Double.parseDouble(wf);
            if( warpFactor < 0 || warpFactor > 8 )
                throw new NumberFormatException();
        } catch(NumberFormatException e) {
            out("In the bowels of the engine room, Scotty yells: \"Captain, my engines cannae take that warp factor!\"\n");
            throw e;
        }
        return warpFactor;
    }

    private void moveShip(double direction, double warpFactor)  throws IOException {
        out("Sulu: \"Heading " + direction + ", Warp Factor "+ warpFactor + ". Aye, Sir!\"\n");

        double[][] vec = {{0,0},
                {1,0},
                {1,-1},
                {0,-1},
                {-1,-1},
                {-1,0},
                {-1,1},
                {0,1},
                {1,1}};

        double[] dir = vec[(int)direction];

        // calc new galactic position

        long iPart = (long) direction;
        double fPart = direction - iPart;
        double parsecsX = (dir[0] + fPart) * warpFactor;
        double parsecsY = (dir[1] + fPart) * warpFactor;


        parsecsX *= GalacticPosition.QUADRANT_WIDTH_PARSECS;
        parsecsY *= GalacticPosition.QUADRANT_HEIGHT_PARSECS;


        GalacticPosition pos = ship.getPosition();
        ship.setPosition(galaxy,
                (long)(pos.getParsecX() + parsecsX),
                (long) (pos.getParsecY() + parsecsY));
    }

    private void decPerformance(int amount) throws IOException{
        out("\nYour action has negatively affected your performance rating by " + amount + "%.\n");
        performance -= amount;
        if( performance < 0)
            performance = 0;
    }

    private String getString() throws IOException {
        String line =  bufIn.readLine();
        return line;
    }

    private void out(String s)  throws IOException{
        bufOut.write(s);
        bufOut.flush();
    }

    private String formatInt(int num) {
        return NumberFormat.getInstance().format(num);
    }

    /**
     *
     * Generate a random integer from rangeMin to rangeMax-1.
     *
     * @param rangeMin start range
     * @param rangeMax end range
     * @return the random number
     */
    public static int nextInt(int rangeMin, int rangeMax) {
        return (int) (rangeMin + (rangeMax - rangeMin) * rnd.nextDouble());
    }

    //
    // Listener interface implementation
    //
    public void handleEvent(GameEvent evt, GameEvent.EventName eventName)  {

        try {
            if (eventName == GameEvent.EventName.ENTERPRISE_MOVED) {
                GalacticPosition oldPos = ship.getPreviousPosition();

                if (ship.getPosition().isOutsideGalaxy()) {
                    out("Spock says, \"Captain, according to the navigation computer the ship is now outside the galaxy.\n");
                    out("             Logically, the correct action would be to reverse course.\"\n");
                    decPerformance(10);
                    return;
                }

                int distanceTravelled = (int) oldPos.distance(ship.getPosition());
                out("Distance travelled: " + formatInt(distanceTravelled) + " parsecs.\n");
                printPosition();

            }
        }catch(IOException ioe) {
            System.out.println(ioe.getMessage());
            ioe.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Run r = new Run();
        try {
            r.init();
        }catch(IOException ioe) {
            System.out.println("An I/O error occurred. There appears to be a problem with reading. or writing to your terminal's "+
                    "standard input and standard output.\n");
            System.out.println("IOException returned message: " + ioe.getMessage());
            ioe.printStackTrace();
        }
    }
}
