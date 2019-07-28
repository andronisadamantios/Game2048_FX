package game2048_fx;

import graphicsfx.TileBackground;
import graphicsfx.BoardBase;

public class BoardBackground extends BoardBase {

    public BoardBackground(int rows, int cols) {
        super(rows, cols);
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.cols; j++) {
                this.getChildren().add(new TileBackground(this, i, j));
            }
        }
    }

}
