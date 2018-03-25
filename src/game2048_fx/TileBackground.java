package game2048_fx;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class TileBackground extends Group {

    protected static final Color COLOR_BACKGROUND = Color.LIGHTGRAY;
    protected static final double PADDING_TOP = 20;
    protected static final double PADDING_LEFT = 20;
    protected static final double MARGIN = 10;
    protected static final double RECT_SIZE = 100;
    protected static final double ARC_SIZE = 20;

    protected static double getX(int col) {
        return PADDING_LEFT + col * (MARGIN + RECT_SIZE);
    }

    protected static double getY(int row) {
        return PADDING_TOP + row * (MARGIN + RECT_SIZE);
    }

    private Rectangle createRectangle() {
        Rectangle rect = new Rectangle(RECT_SIZE, RECT_SIZE, COLOR_BACKGROUND);
        rect.setArcHeight(ARC_SIZE);
        rect.setArcWidth(ARC_SIZE);
        return rect;
    }

    protected int row;
    protected int col;
    protected final Rectangle rect;

    public TileBackground(int row, int col) {
        this.row = row;
        this.col = col;

        this.setTranslateX(getX(this.col));
        this.setTranslateY(getY(this.row));
        this.rect = this.createRectangle();
        this.getChildren().add(this.rect);
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public Point2D getMatrixCoor() {
        return new Point2D(this.getCol(), this.getRow());
    }

}
