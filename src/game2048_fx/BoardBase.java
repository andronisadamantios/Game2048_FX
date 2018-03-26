package game2048_fx;

import javafx.scene.Group;

public class BoardBase extends Group {

    public static final double PADDING_LEFT = 20;
    public static final double PADDING_TOP = 20;
    public static final double MARGIN = 10;

    protected final int cols;
    protected final int rows;

    public BoardBase(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
    }

    @Override
    public String toString() {
        return "";
    }

}
