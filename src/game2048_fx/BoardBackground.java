package game2048_fx;

import javafx.scene.Group;
import game2048.matrix.Matrix;

public class BoardBackground extends Group {

    protected final int cols;
    protected final int rows;

    public BoardBackground(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.cols; j++) {
                this.getChildren().add(new TileBackground(i, j));
            }
        }
    }

    @Override
    public String toString() {
        return "";
    }

}
