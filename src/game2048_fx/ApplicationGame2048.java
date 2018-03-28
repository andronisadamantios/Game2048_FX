package game2048_fx;

import game2048.Direction;
import game2048.Game2048;
import game2048.move.MoveBoard;
import game2048.matrix.Matrix;
import java.util.Collection;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ApplicationGame2048 extends Application {

    public static final boolean ANIMATION_ROTATIONS = true;
    public static final Duration DURATION = Duration.seconds(0.1);

    private final Game2048 game = new Game2048();
    private final Board board = new Board(((Matrix) game.getMatrix()).getRows(), ((Matrix) game.getMatrix()).getCols());
    private final BoardBackground boardBackground = new BoardBackground(Game2048.ROWS, Game2048.COLS);

    @Override
    public void start(Stage primaryStage) {
        Group root = new Group();
        root.getChildren().add(this.boardBackground);
        root.getChildren().add(this.board);
        this.board.toFront();
        this.update();
        Scene scene = new Scene(root, root.getBoundsInLocal().getWidth() + 2 * BoardBase.PADDING_LEFT, root.getBoundsInLocal().getHeight() + 2 * BoardBase.PADDING_TOP);

        primaryStage.setTitle("Game 2048");
        primaryStage.setScene(scene);
        primaryStage.show();

        scene.setOnKeyPressed(this::handleKey);

    }

    private void handleKey(KeyEvent event) {
        if (null != event.getCode()) {
            switch (event.getCode()) {
                case UP:
                case DOWN:
                case LEFT:
                case RIGHT:
                    this.play(getDirection(event.getCode()));
                default:
            }
        }
    }

    private void play(Direction direction) {
        if (!this.game.isGameOver() && this.game.move(direction)) {
            this.update();
        }

        if (this.game.isFinished()) {
            if (this.game.isGameOver()) {
                // todo show game over message
            } else {
                // todo show won message
            }
        }
    }

    private void update() {
        this.board.addMove(this.game.getMatrix().getLastMove());
    }

    /*
    maps keys to directions
     */
    private Direction getDirection(KeyCode kc) {
        switch (kc) {
            case UP:
                return Direction.up;
            case DOWN:
                return Direction.down;
            case LEFT:
                return Direction.left;
            case RIGHT:
                return Direction.right;
            default:
                throw new AssertionError();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
