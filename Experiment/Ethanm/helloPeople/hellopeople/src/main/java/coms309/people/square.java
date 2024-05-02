package coms309.people;


import java.util.ArrayList;

/**
 * Provides the Definition/Structure for the people row
 *
 * @author Vivek Bengre
 */

public class square {

    int length, width;

    public square(){

    }

    public square(int tempLength, int tempWidth) {
        this.length = tempLength;
        this.width = tempWidth;
    }

    public int getLength() {
        return length;
    }

    public int getWidth() {
        return width;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public ArrayList<Character> printsquare(){
        ArrayList<Character> square = new ArrayList<Character>();

        for (int i = 0; i < length; i++){
            for (int j = 0; j < width;j++){
                if (i == 0 || i == length - 1) {
                    square.add('-');
                }
                else {
                    if (j == width - 1 || j == 0) {
                        square.add('|');
                    } else {
                        square.add(' ');
                    }
                }
            }
            square.add('\n');
        }

        return square;
    }

}
