package game2048_fx;

import game2048.Direction;
import game2048.Game2048;
import game2048.MoveTile;
import game2048.matrix.Matrix;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Queue;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class ApplicationGame2048 extends Application {

    private final Game2048 game = new Game2048();
    private final Board board = new Board(game.getMatrix());
    private final BoardBackground boardBackground = new BoardBackground(Game2048.ROWS, Game2048.COLS);
    private final Queue<KeyCode> inputBuffer = new ArrayDeque<>();
    private boolean playing;

    @Override
    public void start(Stage primaryStage) {
        Group root = new Group();
        root.getChildren().add(this.boardBackground);
        root.getChildren().add(this.board);
        this.board.toFront();
        this.update();
        Scene scene = new Scene(root, root.getBoundsInLocal().getWidth(), root.getBoundsInLocal().getHeight());

        primaryStage.setTitle("Game 2048");
        primaryStage.setScene(scene);
        primaryStage.show();

        scene.setOnKeyPressed(this::handleKey);

    }

    private void handleKey(KeyEvent event) {
        if (this.playing) { // using queues as input buffer and animation buffer
            return ; // did not work to fix problem when many keys are pressed fast and animation is slow
        } // proxeiro fix: do not play if it is animating, oute afto works
        if (null != event.getCode()) {
            switch (event.getCode()) {
                case UP:
                case DOWN:
                case LEFT:
                case RIGHT:
                    this.inputBuffer.add(event.getCode());
                    this.playNext();
                    break;
                default:
                    break;
            }
        }
    }

    private void playNext() {
        if (!this.playing && !this.inputBuffer.isEmpty()) {
            this.play(getDirection(this.inputBuffer.remove()));
        }
    }

    private void play(Direction direction) {
        this.playing = true;
        if (this.checkFinished()) {
            return;
        }
        if (this.game.move(direction)) {
            this.update();
            this.checkFinished();
        }
        this.playing = false;
        this.playNext();
    }

    private void update() {
        this.board.update();

        Collection<Matrix.Coor> lastAdded = this.game.getLastAdded();
        if (lastAdded != null && !lastAdded.isEmpty()) {
            lastAdded.forEach(c -> {
                this.board.addNewTile(c.getRow(), c.getCol(), this.game.getMatrix().getValue(c.getRow(), c.getCol()));
            });
        }
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

    /*
    must display message that game is finished
    todo
     */
    private boolean checkFinished() {
        return this.game.isFinished();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
