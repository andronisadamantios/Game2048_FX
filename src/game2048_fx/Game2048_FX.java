package game2048_fx;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class Game2048_FX extends Application {

    private final game2048.matrix.IMatrix2048 matrix = new game2048.matrix.Matrix2048_hv_1(game2048.Game2048.ROWS, game2048.Game2048.COLS);
    private final Board board = new Board(game2048.Game2048.ROWS , game2048.Game2048.COLS);
    private final BoardBackground boardBackground = new BoardBackground(game2048.Game2048.ROWS , game2048.Game2048.COLS);

    @Override
    public void start(Stage primaryStage) {
        Group root = new Group();
        root.getChildren().add(this.boardBackground);
        root.getChildren().add(this.board);
        this.board.toFront();
        Scene scene = new Scene(root, root.getBoundsInLocal().getWidth(), root.getBoundsInLocal().getHeight());
        this.board.start();

        primaryStage.setTitle("Game 2048");
        primaryStage.setScene(scene);
        primaryStage.show();

        scene.setOnKeyPressed((KeyEvent event) -> {
            if (null != event.getCode()) {
                switch (event.getCode()) {
                    case UP:
                        Game2048_FX.this.board.up();
                        break;
                    case DOWN:
                        Game2048_FX.this.board.down();
                        break;
                    case LEFT:
                        Game2048_FX.this.board.left();
                        break;
                    case RIGHT:
                        Game2048_FX.this.board.right();
                        break;
                    default:
                        break;
                }
            }
        });

    }

    public static void main(String[] args) {
        launch(args);
    }

}
