package superstartrek;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

public class TextBoxGrid {

    private LinkedList<LinkedList<TextBox>> model;
    private char[][] charBuffer;

    public TextBoxGrid() {
        model = new LinkedList<>();
    }

    public char[][] format(ArrayList<ArrayList> table) {
        model.clear();

        return charBuffer;
    }

    private static String formatString(String s, int columnWidth, Alignment alignment, int padding) {
        StringBuffer buf = new StringBuffer();
        int len = s.length();

        if( alignment == Alignment.LEFT ) {
            appendSpaces(buf, padding);
            buf.append(s);
            appendSpaces(buf, columnWidth - len);
            appendSpaces(buf, padding);
        } else if( alignment == Alignment.CENTER ) {
            int numSpacesLeftRight = columnWidth / 2 - len / 2;
            appendSpaces(buf, padding);
            appendSpaces(buf, numSpacesLeftRight);
            buf.append(s);
            appendSpaces(buf, numSpacesLeftRight);
            appendSpaces(buf, padding);
        } else if( alignment == Alignment.RIGHT ) {
            appendSpaces(buf, padding);
            appendSpaces(buf, columnWidth - len);
            buf.append(s);
            appendSpaces(buf, padding);
        }
        return buf.toString();
    }

    private static void appendSpaces(StringBuffer buf, int numSpaces) {
        for(int i=0; i<numSpaces; i++)
            buf.append(' ');
    }


}
