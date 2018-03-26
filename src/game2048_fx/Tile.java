package game2048_fx;

import game2048.matrix.Matrix;
import java.util.ArrayDeque;
import java.util.Queue;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class Tile extends TileBackground {

    public final static boolean ANIMATION_ROTATIONS = false;
    public final static Duration DURATION = Duration.seconds(0.1);
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

    private final RotateTransition rotateRect = new RotateTransition(DURATION);
    //private final RotateTransition rotateText = new RotateTransition(DURATION);
    private final TranslateTransition translate = new TranslateTransition(DURATION, this);
    private final FadeTransition appear = new FadeTransition(DURATION, this);

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
        //this.rotateText.setNode(this.text);

        this.rotateRect.onFinishedProperty().addListener((observable, oldValue, newValue) -> {
            Tile.this.animateNext();
        });
//        this.rotateText.onFinishedProperty().addListener((observable, oldValue, newValue) -> {
//            Tile.this.animateNext();
//        });
        this.translate.onFinishedProperty().addListener((observable, oldValue, newValue) -> {
            Tile.this.animateNext();
        });

    }

    public void move(Matrix.Vector vectorMatrix, int newValue) {
        frame f = getFrame(vectorMatrix);

        frames.add(f);
        this.animateNext();

        this.row += vectorMatrix.getDRow();
        this.col += vectorMatrix.getDCol();
        this.setValue(newValue);
    }

    private frame getFrame(Matrix.Vector vectorMatrix) {
        final double m = vectorMatrix.getDCol() + vectorMatrix.getDRow();
        boolean p = m > 0;
        boolean v = vectorMatrix.getDRow() != 0;
        Point2D vector = new Point2D(vectorMatrix.getDCol(), vectorMatrix.getDRow());
        Point2D vector1 = vector.multiply(1 / vector.magnitude());
        frame f = new frame();
        boolean rc = (this.row * vector1.getY() + this.col * vector1.getX()) % 2 == 0;
        boolean b = !p ^ (rc ^ v);
        int a = (b ? 1 : -1);
        double angle = 90 * m * a;
        f.angle = angle;
        Point2D vector2 = vector.multiply(BoardBase.MARGIN + RECT_SIZE);
        f.x = vector2.getX();
        f.y = vector2.getY();
        return f;
    }

    static class frame {

        public double x, y, angle;
    }

    private final Queue<frame> frames = new ArrayDeque<>();

    private boolean animationsStopped() {
        return this.rotateRect.getStatus() == Animation.Status.STOPPED
                //&& this.rotateText.getStatus() == Animation.Status.STOPPED
                && this.translate.getStatus() == Animation.Status.STOPPED;
    }

    private void animate(frame f) {

        this.rotateRect.setByAngle(f.angle);
        //this.rotateText.setByAngle(f.angle);
        this.translate.setByX(f.x);
        this.translate.setByY(f.y);

        if (ANIMATION_ROTATIONS) {
            this.rotateRect.play();
            //this.rotateText.play();
        }
        this.translate.play();
    }

    private void animateNext() {
        if (this.animationsStopped() && !frames.isEmpty()) {
            this.animate(frames.remove());
        }
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
