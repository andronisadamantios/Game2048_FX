package game2048_fx;

import game2048.MoveTile;
import game2048.matrix.IMatrix2048;
import game2048.matrix.Matrix;
import java.util.Collection;

public class Board extends BoardBase {

    private final IMatrix2048 matrix2048;

    public Board(IMatrix2048 matrix2048) {
        super(((Matrix) matrix2048).getRows(), ((Matrix) matrix2048).getCols());
        this.matrix2048 = matrix2048;
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

    public void update() {
        Collection<MoveTile> lastMove = this.matrix2048.getLastMove();
        if (lastMove == null || lastMove.isEmpty()) {
            return;
        }
        lastMove.stream()
                .forEach((mt) -> {
                    Tile tEnd = this.getTile(mt.getEnd().getRow(), mt.getEnd().getCol());
                    if (tEnd != null) {
                        tEnd.disappear();
                    }
                    Tile t = this.getTile(mt.getStart().getRow(), mt.getStart().getCol());
                    if (t != null) {
                        t.toFront();
                        t.move(mt.getVector(), this.matrix2048.getValue(mt.getEnd().getRow(), mt.getEnd().getCol()));
                    }
                });
    }

}
