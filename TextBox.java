package superstartrek;

import java.awt.*;
import java.util.ArrayList;

public class TextBox {

    private Rectangle rectangle;
    private Insets insets;
    private ArrayList<String> text; // lines of text
    private int maxTextLength;
    private Alignment alignment;

    public TextBox() {
        text = new ArrayList<>();
        alignment = Alignment.LEFT;
        insets = new Insets(0,0,0,0);
        rectangle = new Rectangle(0,0,0,0);
    }

    public void setRow(int row) { rectangle.y = row; }
    public void setColumn(int column) { rectangle.x = column; }
    public int getRow() { return rectangle.y; }
    public int getColumn() { return rectangle.x; }

    private void calcDimensions() {
        int width = 0;
        for(int i=0; i<text.size(); i++) {
            if( text.get(i).length() > width )
                width = text.get(i).length();
        }
        maxTextLength = width;
        rectangle.width = insets.left + insets.right + maxTextLength;
        rectangle.height = insets.top + insets.bottom + text.size();
    }

    public void addText(String text) {
        this.text.add(text);
        calcDimensions();
    }

    public void removeText(int index) {
        text.remove(index);
        calcDimensions();
    }

    public void removeText(String text) {
        this.text.remove(text);
        calcDimensions();
    }

    public void setInsets(int top, int bottom, int left, int right) {
        insets.top = top;
        insets.bottom = bottom;
        insets.left = left;
        insets.right = right;
        calcDimensions();
    }

    public int getInsetLeft() {
        return insets.left;
    }

    public int getInsetRight() {
        return insets.right;
    }

    public int getInsetTop() {
        return insets.top;
    }

    public int getInsetBottom() {
        return insets.bottom;
    }

    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
    }

    public Alignment getAlignment() {
        return alignment;
    }

    public int getWidth() { return rectangle.width; }
    public int getHeight() { return rectangle.height; }

    public enum Alignment {
        LEFT, RIGHT, CENTER;
    }
}
