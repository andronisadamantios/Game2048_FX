package game2048_fx;

import graphicsfx.BoardBase;
import game2048.Game2048;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import matrix.Matrix;

public class ApplicationGame2048 extends Application {

    /**
     * να κάνουν και περιστροφές τα τετράγωνα καθώς κινούνται στις θέσεις τους.
     * περιστρέφονται ανάλογα με τη θέση τους και τη φορά της κίνησης.
     */
    public static final boolean ANIMATION_ROTATIONS = true;

    /**
     * η διάρκεια του κάθε animation
     */
    public static final Duration DURATION = Duration.seconds(0.2);

    private final Game2048 game = new Game2048();
    private final Board board = new Board(((Matrix) game.getMatrix()).getRows(), ((Matrix) game.getMatrix()).getCols());
    private final BoardBackground boardBackground = new BoardBackground(Game2048.ROWS, Game2048.COLS);

    @Override
    public void start(Stage primaryStage) {
        Group root = new Group();
        root.getChildren().add(this.boardBackground);
        root.getChildren().add(this.board);
        this.board.toFront();
        this.board.addMove(this.game.getMatrix().getLastMove());
        Scene scene = new Scene(root, root.getBoundsInLocal().getWidth() + 2 * BoardBase.PADDING_LEFT, root.getBoundsInLocal().getHeight() + 2 * BoardBase.PADDING_TOP);

        primaryStage.setTitle("Game 2048");
        primaryStage.setScene(scene);
        primaryStage.show();

        scene.setOnKeyPressed(this::handleKey);

    }

    /**
     * on key pressed handler
     *
     * @param event
     */
    private void handleKey(KeyEvent event) {
        if (null != event.getCode()) {
            switch (event.getCode()) {
                case UP:
                case DOWN:
                case LEFT:
                case RIGHT:
                    this.play(getMatrixVector(event.getCode()));
                    break;
                default:
            }
        }
    }

    private void play(Matrix.Vector direction) {
        if (!this.game.isGameOver() && this.game.move(direction)) {
            this.board.addMove(this.game.getMatrix().getLastMove());
        }

        if (this.game.isFinished()) {
            if (this.game.isGameOver()) {
                // todo: show game over message
            } else {
                // todo: show won message
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }


    /**
     * maps keys to matrix vectors
     * @param kc
     * @return
     */
    public static Matrix.Vector getMatrixVector(KeyCode kc) {
        switch (kc) {
            case UP:
                return Matrix.Vector.UP;
            case DOWN:
                return Matrix.Vector.DOWN;
            case LEFT:
                return Matrix.Vector.LEFT;
            case RIGHT:
                return Matrix.Vector.RIGHT;
            default:
                throw new AssertionError();
        }
    }


}
