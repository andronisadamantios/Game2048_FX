package game2048_fx;

import graphicsfx.BoardBase;
import matrix.Matrix;
import game2048.move.AddTile;
import game2048.move.MoveBoard;
import game2048.move.MoveTile;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Board extends BoardBase {

    public Board(int rows, int cols) {
        super(rows, cols);
    }

    public boolean existsTile(Matrix.Coor c) {
        return this.getChildren().stream().filter(n -> n instanceof Tile).map(n -> (Tile) n)
                .anyMatch(t -> t.getRow() == c.getRow() && t.getCol() == c.getCol());
    }

    public Stream<Tile> getTiles(Matrix.Coor c) {
        return this.getChildren().stream().filter(n -> n instanceof Tile).map(n -> (Tile) n)
                .filter(t -> t.getRow() == c.getRow() && t.getCol() == c.getCol());
    }

    public Tile getTile(Matrix.Coor c) {
        return this.getTiles(c).findFirst().orElse(null);
    }

    private boolean noTileAnimating() {
        return this.getChildren().stream().filter(c -> c instanceof Tile).map(c -> (Tile) c)
                .noneMatch(Tile::anyAnimationsRunning);
    }
    //private final DelayQueue<MoveBoard> movesToDo = new DelayQueue<>();
    private final Queue<MoveBoard> movesToDo = new ArrayDeque<>();

    public void addMove(MoveBoard lastMove) {
        if (lastMove == null || movesToDo.contains(lastMove)) {
            return;
        }
        ApplicationGame2048.debugHelp("added move: " + lastMove.hashCode());
        movesToDo.add(lastMove);
        this.doNextMove();
    }

    protected void doNextMove() {
        if (movesToDo.isEmpty() || !this.noTileAnimating()) {
            return;
        }
        this.doMove(movesToDo.remove());
    }

    private void doMove(MoveBoard lastMove) {

        System.out.println();
        ApplicationGame2048.debugHelp("begin move: " + " " + lastMove.hashCode());

        List<Matrix.Coor> starts = lastMove.StreamTileMoves().map(MoveTile::getStart)
                .collect(Collectors.toList());

        // apo ola ta ends ektos apo auta pou einai kai start
        lastMove.StreamTileMoves().map(MoveTile::getEnd)
                .filter(((Predicate<Matrix.Coor>) starts::contains).negate())
                .map(this::getTile).filter(Predicate.isEqual(null).negate())
                .forEach(tEnd -> {
                    if (tEnd != null) {
                        ApplicationGame2048.debugHelp("start disappear: " + tEnd.toString());
                        tEnd.disappear();
                    }
                });

        // move tiles
        lastMove.StreamTileMoves()
                .forEach(mt -> {
                    Tile t = this.getTile(mt.getStart());
                    if (t != null) {
                        t.toFront();
                        ApplicationGame2048.debugHelp("start translation: " + t.toString() + " using [" + mt.toString() + "]");
                        t.move(mt);
                    } else {
                        ApplicationGame2048.debugHelp(String.format("tile at %s not found", mt.getStart()));
                    }
                });

        // add new tiles
        lastMove.StreamAddTiles().forEach((AddTile t) -> {

            Tile tile = new Tile(this, t.getCoor().getRow(), t.getCoor().getCol(), t.getValue());
            this.addTile(tile);
            ApplicationGame2048.debugHelp("start appear: " + tile.toString());
            tile.appear();
        });

        // remove any tiles krymmena katw apo alla
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.cols; j++) {
                this.getTiles(new Matrix.Coor(i, j)).sorted(new Comparator<Tile>() {
                    @Override
                    public int compare(Tile o1, Tile o2) {
                        int i1 = Board.this.getChildren().indexOf(o1);
                        int i2 = Board.this.getChildren().indexOf(o2);
                        return Integer.compare(i2, i1);
                    }
                }).skip(1).forEach(this.getChildren()::remove);
            }
        }

    }

    protected void onTileAnimationFinished(Tile t) {
        this.doNextMove();
    }

    void onTileAnimationDisappeared(Tile t) {
        this.getChildren().remove(t);
    }

}
