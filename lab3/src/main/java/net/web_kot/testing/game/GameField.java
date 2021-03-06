package net.web_kot.testing.game;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

/**
 * Representing a 512 game field
 */
public class GameField {

    /**
     * Game field width and height
     */
    public static final int SIZE = 4;

    /**
     * Enumeration with possible cells moving directions
     */
    public enum Direction { 
        
        UP(-1, 0), DOWN(1, 0), LEFT(0, -1), RIGHT(0, 1);
        
        /**
         * Coordinate delta x when moving in this direction
         */
        public final int dx;
        
        /**
         * Coordinate delta y when moving in this direction
         */
        public final int dy;
        
        Direction(int _dx, int _dy) { dx = _dx; dy = _dy; }
        
    }
    
    private static final Random rand = new Random();
    private int[][] field = new int[SIZE][SIZE];
    private int score = 0;
    
    /**
     * Set game field cell with 2^pow value
     * @param cell field cell
     * @param pow power value
     */
    public void setCellValue(Cell cell, int pow) {
        if(pow < 0) throw new IllegalArgumentException();
        field[cell.getRow()][cell.getColumn()] = pow;
    }

    /**
     * Returns log2 of value stored in cell
     * @param cell field cell
     * @return log2(value)
     */
    public int getCellValue(Cell cell) {
        return field[cell.getRow()][cell.getColumn()];
    }

    /**
     * Return whether cell occupied by any number
     * @param cell cell for checking
     * @return cell occupied or not 
     */
    public boolean isCellOccupied(Cell cell) {
        return field[cell.getRow()][cell.getColumn()] != 0;
    }

    /**
     * Returns list representing all empty cells on game field
     * @return list with cells coordinates
     */
    public ArrayList<Cell> getEmptyCells() {
        ArrayList<Cell> result = new ArrayList<>();
        for(int i = 0; i < SIZE; i++)
            for(int j = 0; j < SIZE; j++)
                if(field[i][j] == 0) result.add(Cell.at(i, j));
        return result;
    }

    /**
     * Returns random cell from all empty cells on game field
     * @return cell coordinates
     */
    public Cell getRandomEmptyCell() {
        ArrayList<Cell> empty = getEmptyCells();
        return empty.get(rand.nextInt(empty.size()));
    }

    /**
     * Moves all values in cells in specified direction 
     * and merges cells with same value
     * @param dir direction
     * @return is at least one cell has been moved
     */
    public boolean move(Direction dir) {
        boolean[][] merged = new boolean[SIZE][SIZE];

        AtomicBoolean moved = new AtomicBoolean(false);
        streamForDelta(dir.dx).forEach((i) ->
            streamForDelta(dir.dy).forEach((j) -> {
                Cell current = Cell.at(i, j);
                
                int value = getCellValue(current);
                if(value == 0) return;
                
                Pair<Cell, Cell> pair = findFarthestPosition(Cell.at(i, j), dir);
                setCellValue(current, 0);
                
                Cell next = pair.getRight();
                if(next != null && !merged[next.getRow()][next.getColumn()] && getCellValue(next) == value) {
                    setCellValue(pair.getRight(), value + 1);
                    merged[next.getRow()][next.getColumn()] = true;
                    
                    score += 1 << (value + 1);
                    moved.set(true);
                } else {
                    setCellValue(pair.getLeft(), value);
                    if(!pair.getLeft().equals(current)) moved.set(true);
                }
            })
        );
        return moved.get();
    }
    
    private IntStream streamForDelta(int d) {
        if(d == -1) return IntStream.range(0, SIZE);
        return IntStream.iterate(SIZE - 1, i -> i - 1).limit(SIZE);
    }

    /**
     * Returns farthest position on which current cell can be moved in given direction
     * Also returns next cell in given direction (or null if there is no such cell)
     * for merge possibility checking (for empty cells behavior undefined)
     * @param cell non-empty cell coordinates
     * @param dir direction
     * @return pair of position and next cell
     */
    public Pair<Cell, Cell> findFarthestPosition(Cell cell, Direction dir) {
        Cell next = nextCellAt(cell, dir);
        if(next == null || isCellOccupied(next)) return Pair.of(cell, next);
        
        Cell previous;
        do {
            previous = next;
            next = nextCellAt(next, dir);
        } while(next != null && !isCellOccupied(next));
        
        return Pair.of(previous, next);
    }

    /**
     * Returns next cell in given direction (or null if there is no such cell)
     * @param cell cell coordinates
     * @param dir direction
     * @return next cell position
     */
    public Cell nextCellAt(Cell cell, Direction dir) {
        try {
            return Cell.at(cell.getRow() + dir.dx, cell.getColumn() + dir.dy);
        } catch(IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Adds random tile to game field on empty position
     * @return coordinates of added cell
     */
    public Cell addRandomTile() {
        Cell cell = getRandomEmptyCell();
        setCellValue(cell, rand.nextInt(10) == 0 ? 2 : 1);
        return cell;
    }

    /**
     * Returns whether we can do any movement
     * @return can do any movement or not
     */
    public boolean canMove() {
        for(int i = 0; i < SIZE; i++)
            for(int j = 0; j < SIZE; j++) {
                Cell cell = Cell.at(i, j);
                int value = getCellValue(cell);
                
                for(Direction dir : Direction.values()) {
                    Cell neighbour = nextCellAt(cell, dir);
                    if(neighbour != null) {
                        int val = getCellValue(neighbour);
                        if(val == 0 || val == value) return true;
                    }
                }
            }
        return false;
    }

    /**
     * Returns player total score
     * @return score
     */
    public int getScore() {
        return score;
    }
    
}
