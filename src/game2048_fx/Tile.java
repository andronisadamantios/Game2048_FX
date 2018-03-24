package game2048_fx;

import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class Tile extends TileBackground {

    private final static boolean ANIMATION_ROTATIONS = true;
    private final static Duration DURATION = Duration.seconds(1);
    private final static Color COLOR_FOREGROUND = Color.BLACK;


    private static Text createText(int row, int col) {
        Text text = new Text("");
        text.setFont(new Font(60));
          text.setFill(COLOR_FOREGROUND);    
          
        Bounds b = text.getBoundsInLocal();
        double x = getX(col) + RECT_SIZE / 2 - b.getWidth() / 2;
        double y = getY(row) + RECT_SIZE / 2 + b.getHeight() / 4;
        text.setX(x);
        text.setY(y);

        return text;
    }

    private final RotateTransition rotateRect = new RotateTransition(DURATION);
    private final RotateTransition rotateText = new RotateTransition(DURATION);
    private final TranslateTransition translate = new TranslateTransition(DURATION, this);

    private int value;
    private boolean deleted;

    private  Text text;


    public boolean isDeleted() {
        return this.deleted;
    }

    public void delete() {
        this.deleted = true;
    }

    public int getValue() {
        if (this.value < 2) {
            return 0;
        }
        return value;
    }

    public void setValue(int value) {
        this.value = value;
        this.text.setText(Integer.toString(this.value));
        int rgb = game2048.utils.getRGB2(value);
        final Color color = Color.rgb((rgb >> 16) & 255, (rgb >> 8) & 255, rgb & 255);
        this.rect.setFill(color);
    }

    public void doubleValue() {
        this.setValue(this.getValue() << 1);
    }

    public Tile(int row, int col) {
        super(row, col);

        this.text = createText(this.row, this.col);

        this.getChildren().add( this.text);
        
        this.rotateRect.setNode(this.rect);
        this.rotateText.setNode(this.text);

        this.rotateRect.onFinishedProperty().addListener((observable, oldValue, newValue) -> {
            Tile.this.resetTransitions();
        });
        this.rotateText.onFinishedProperty().addListener((observable, oldValue, newValue) -> {
            Tile.this.resetTransitions();
        });
        this.translate.onFinishedProperty().addListener((observable, oldValue, newValue) -> {
            Tile.this.resetTransitions();
        });
    }

    public void moveInLogic(Point2D vector) {
        this.row += vector.getY();
        this.col += vector.getX();

        this.prepareToMoveInGraphics(vector);
    }

    private void prepareToMoveInGraphics(Point2D vector) {
        final double m = vector.getX() + vector.getY();
        boolean p = m > 0;
        boolean v = vector.getX() == 0;
        Point2D vector1 = vector.multiply(1 / vector.magnitude());

        if (ANIMATION_ROTATIONS) {
            boolean rc = (this.row * vector1.getY() + this.col * vector1.getX()) % 2 == 0;
            boolean b = p ^ (rc ^ v);
            int a = (b ? 1 : -1);
            double angle = 90 * m * a;
            this.rotateRect.setByAngle(angle);
            this.rotateText.setByAngle(-angle);
        }

        Point2D vector2 = vector.multiply(MARGIN + RECT_SIZE);
        this.translate.setByX(vector2.getX());
        this.translate.setByY(vector2.getY());
    }

    private void resetTransitions() {
        this.rotateRect.setByAngle(0);
        this.rotateText.setByAngle(0);
        this.translate.setByX(0);
        this.translate.setByY(0);
    }

    public void moveInGraphics() {
        if (ANIMATION_ROTATIONS) {
            this.rotateRect.play();
            this.rotateText.play();
        }
        this.translate.play();
    }

    @Override
    public String toString() {
        return String.format("[%d, %d]: %d", this.row, this.col, this.value);
    }


    
    
}
