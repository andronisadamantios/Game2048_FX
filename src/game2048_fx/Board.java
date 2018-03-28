package game2048_fx;

import game2048.move.AddTile;
import game2048.move.MoveBoard;
import java.util.ArrayDeque;
import java.util.Queue;

public class Board extends BoardBase {

    public Board(int rows, int cols) {
        super(rows, cols);
    }

    public Tile getTile(int row, int col) {
        return this.getChildren().stream().filter(n -> n instanceof Tile).map(n -> (Tile) n)
                .filter(t -> t.getRow() == row && t.getCol() == col).findFirst().orElse(null);
    }

    public void addNewTile(int row, int col, int value) {
        Tile tile = new Tile(row, col, value);
        this.getChildren().add(tile);
        tile.appear();
    }

    private boolean noTileAnimating() {
        return this.getChildren().stream().filter(c -> c instanceof Tile).map(c -> (Tile) c)
                .noneMatch(Tile::anyAnimationsRunning);
    }
    //private final DelayQueue<MoveBoard> movesToDo = new DelayQueue<>();
    private final Queue<MoveBoard> movesToDo = new ArrayDeque<>();

    public void addMove(MoveBoard lastMove) {
        if (lastMove != null && !movesToDo.contains(lastMove)) {
            movesToDo.add(lastMove);
        }
        this.doNextMove();
    }

    protected void doNextMove() {
        if (movesToDo.isEmpty() || !this.noTileAnimating()) {
            return;
        }

        this.doMove(movesToDo.remove());
    }

    private void doMove(MoveBoard lastMove) {
        // move tiles
        lastMove.getTileMoves().forEach((mt) -> {
            Tile tEnd = this.getTile(mt.getEnd().getRow(), mt.getEnd().getCol());
            if (tEnd != null) {
                tEnd.disappear();
            }
            Tile t = this.getTile(mt.getStart().getRow(), mt.getStart().getCol());
            if (t != null) {
                t.toFront();
                t.move(mt, mt.getValue());
            }
        });

        // add new tiles
        lastMove.getTileAdds().forEach((AddTile t) -> {
            Board.this.addNewTile(t.getCoor().getRow(), t.getCoor().getCol(), t.getValue());
        });
    }

}
