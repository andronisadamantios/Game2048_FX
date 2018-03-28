package game2048_fx;

import game2048.move.MoveTile;
import game2048.matrix.Matrix;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Tile extends TileBackground {

    private final static Color COLOR_FOREGROUND = Color.BLACK;

    private static Text createText(int row, int col) {
        Text text = new Text("");
        text.setFont(Font.font(60));
        text.setFill(COLOR_FOREGROUND);

        Bounds b = text.getBoundsInLocal();
        text.setX(RECT_SIZE / 2 - b.getWidth() / 2);
        text.setY(RECT_SIZE / 2 + b.getHeight() / 4);

        return text;
    }

    private final RotateTransition rotateRect = new RotateTransition(ApplicationGame2048.DURATION);
    private final TranslateTransition translate = new TranslateTransition(ApplicationGame2048.DURATION, this);
    private final FadeTransition appear = new FadeTransition(ApplicationGame2048.DURATION, this);

    private final Text text;
    private int value;

    public void setValue(int value) {
        if (value == this.value) {
            return;
        }
        this.value = value;
        this.text.setText(Integer.toString(this.value));
        Bounds b = this.text.getBoundsInLocal();
        while (b.getWidth() > RECT_SIZE * 0.9) {
            this.text.setFont(Font.font(this.text.getFont().getSize() * 5 / 6));
            b = this.text.getBoundsInLocal();
        }
        this.text.setX(RECT_SIZE / 2 - b.getWidth() / 2);
        this.text.setY(RECT_SIZE / 2 + b.getHeight() / 4);
        int rgb = game2048.utils.getRGB2(value);
        final Color color = Color.rgb((rgb >> 16) & 255, (rgb >> 8) & 255, rgb & 255);
        this.rect.setFill(color);
    }

    public Tile(int row, int col, int value) {
        super(row, col);
        this.setOpacity(0);
        this.text = createText(this.row, this.col);
        this.setValue(value);
        this.getChildren().add(this.text);

        this.rotateRect.setNode(this.rect);

        this.translate.setOnFinished((ActionEvent event) -> {
            ((Board) this.getParent()).doNextMove();
        });
        this.appear.setOnFinished((ActionEvent event) -> {
            ((Board) this.getParent()).doNextMove();
        });

    }

    public void move(MoveTile mt, int newValue) {
        Matrix.Vector vectorMatrix = mt.getVector();
        final double m = vectorMatrix.getDCol() + vectorMatrix.getDRow();
        boolean vertical = vectorMatrix.getDRow() != 0;
        Point2D vector = new Point2D(vectorMatrix.getDCol(), vectorMatrix.getDRow());
        Point2D vector1 = vector.multiply(1 / vector.magnitude());
        boolean rc = (mt.getStart().getCol() * vector1.getY() + mt.getStart().getRow() * vector1.getX()) % 2 == 0;
        boolean b = (m > 0) ^ (rc ^ vertical);
        this.rotateRect.setByAngle(90 * Math.abs(m) * (b ? 1 : -1));
        Point2D vector2 = vector.multiply(BoardBase.MARGIN + RECT_SIZE);
        this.translate.setByX(vector2.getX());
        this.translate.setByY(vector2.getY());

        this.row += mt.getVector().getDRow();
        this.col += mt.getVector().getDCol();
        this.setValue(newValue);

        if (ApplicationGame2048.ANIMATION_ROTATIONS) {
            this.rotateRect.play();
        }
        this.translate.play();
    }

    protected boolean anyAnimationsRunning() {
        boolean result = this.translate.getStatus() == Animation.Status.RUNNING
                || this.appear.getStatus() == Animation.Status.RUNNING;
        if (ApplicationGame2048.ANIMATION_ROTATIONS) {
            result = result || this.rotateRect.getStatus() == Animation.Status.RUNNING;
        }
        return result;
    }

    public void appear() {
        this.appear.setByValue(1);
        this.appear.play();
    }

    public void disappear() {
        this.appear.setByValue(-1);
        this.appear.setOnFinished((ActionEvent event) -> {
            ((Board) Tile.this.getParent()).getChildren().remove(Tile.this);
        });
        this.appear.play();
    }

    @Override
    public String toString() {
        return String.format("[%d, %d]: %d", this.row, this.col, this.value);
    }

}
