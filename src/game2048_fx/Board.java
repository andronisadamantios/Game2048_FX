package game2048_fx;

import game2048.Direction;
import game2048.matrix.Matrix;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyEvent;

public class Board extends BoardBackground {

    public Board(int rows, int cols) {
        super(rows, cols);


        this.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (null != event.getCode()) {
                    switch (event.getCode()) {
                        case UP:
                            Board.this.up();
                            break;
                        case DOWN:
                            Board.this.down();
                            break;
                        case LEFT:
                            Board.this.left();
                            break;
                        case RIGHT:
                            Board.this.right();
                            break;
                        default:
                            break;
                    }
                }
            }
        });
    }

    public void start() {
        this.addNewTile();
        this.addNewTile();
    }

    private boolean addNewTile() {
        Matrix m = new Matrix(this.rows, this.cols);

        this.streamTiles().forEach(t -> {
            m.set(t.getRow(), t.getCol(), t.getValue());
        });

        List<Matrix.Coor> emptyCoors = m.getEmptyCoors();
        if (!emptyCoors.isEmpty()) {
            Matrix.Coor c = emptyCoors.get((int) (Math.random() * emptyCoors.size()));
            this.streamTiles().filter(t -> t.getRow() == c.getRow() && t.getCol() == c.getCol())
                    .findFirst().get().setValue(game2048.Game2048.rg.get());
            return true;
        }
        return false;
    }

    private Stream<Tile> streamTiles(boolean reversed) {
        Stream<Tile> result = this.getChildren().stream().filter(n -> n instanceof Tile).map(n -> (Tile) n);
        Comparator<Tile> comp = Comparator.comparingInt(Tile::getRow).thenComparingInt(Tile::getCol);
        return result.sorted(reversed ? comp.reversed() : comp);
    }

    private Stream<Tile> streamTiles() {
        return this.streamTiles(false);
    }

    public Stream<Tile> streamRow(int row, boolean reverse, int afterCol) {
        return streamTiles(reverse).filter(t -> t.getRow() == row && t.getCol() != afterCol)
                .filter(t -> (afterCol > t.getCol()) ^ !reverse);
    }

    public Stream<Tile> streamCol(int col, boolean reverse, int afterRow) {
        return streamTiles(reverse).filter(t -> t.getCol() == col && t.getRow() != afterRow)
                .filter(t -> (afterRow > t.getRow()) ^ !reverse);
    }

    private Optional<Tile> findDestV(int a, int start1, int start2, boolean b) {
        Optional<Tile> result = this.streamCol(a, b, start1).filter(t -> t.getValue() > 0).findFirst();
        if (!result.isPresent()) {
            result = this.streamCol(a, !b, start2).findFirst();
        }
        return result;
    }

    private Optional<Tile> findDestH(int a, int start1, int start2, boolean b) {
        Optional<Tile> result = this.streamRow(a, b, start1).filter(t -> t.getValue() > 0).findFirst();
        if (!result.isPresent()) {
            result = this.streamRow(a, !b, start2).findFirst();
        }
        return result;
    }

    private Tile findDest(Tile origin, Direction dir) {
        // ta parakatw noumera einai athroismata 2 orwn, enas gia kathe orientation (ver|hor)
        int rowcol = dir.getAbsV() * origin.getCol()
                + dir.getAbsH() * origin.getRow();
        int start1 = dir.getAbsV() * origin.getRow()
                + dir.getAbsH() * origin.getCol();

        // map {-1, 1} -> {(rows|cols), -1}
        int start2 = dir.getAbsV() * (dir.getV() * (this.rows + 1) - this.rows + 1) / 2
                + dir.getAbsH() * (dir.getH() * (this.cols + 1) - this.cols + 1) / 2;

        Optional<Tile> optional;
        switch (dir.getOrientation()) {
            case Horizontal:
                optional = findDestH(rowcol, start1, start2, dir.isPositive());
                break;
            case Vertical:
                optional = findDestV(rowcol, start1, start2, dir.isPositive());
                break;
            default:
                throw new AssertionError();
        }
        return optional.orElse(origin);
    }

    private Point2D howShouldMove(Tile origin, Direction direction) {
        Tile dest = this.findDest(origin, direction);
        Point2D diff = dest.getMatrixCoor().subtract(origin.getMatrixCoor());
        if (!dest.equals(origin) && dest.getValue() == origin.getValue()) {
            // edw prepei na ginei merge
            origin.doubleValue();
            dest.delete();
        } else if (diff.magnitude() > 0) {
            // edw prepei na paei mia thesi prin to dest ws pros thn anapodh fora
            diff = diff.add(diff.multiply(direction.getValue() / diff.magnitude()));
        }
        return diff;
    }

    public boolean move(Direction direction) {
        Map<Tile, Point2D> map = this.streamTiles().filter(t -> t.getValue() > 0)
                .collect(Collectors.toMap(Function.identity(), t -> howShouldMove(t, direction)));

        boolean result = !map.values().stream().allMatch(v -> Point2D.ZERO.equals(v));

        map.entrySet().stream().filter((e) -> (e.getValue() != Point2D.ZERO))
                .forEach((e) -> {
                    this.getChildren().add(new Tile(e.getKey().getRow(), e.getKey().getCol()));
                    e.getKey().toFront();
                });

        map.forEach(Tile::moveInLogic);
        map.keySet().forEach(Tile::moveInGraphics);
        map.keySet().stream().filter(Tile::isDeleted).forEach(t -> this.getChildren().remove(t));

        if (result) {
            result = result || this.addNewTile();
        }
        return result;
    }

    public boolean up() {
        return this.move(Direction.up);
    }

    public boolean down() {
        return this.move(Direction.down);
    }

    public boolean left() {
        return this.move(Direction.left);
    }

    public boolean right() {
        return this.move(Direction.right);
    }

}
