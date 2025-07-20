package io.github.some_example_name.model.game;

import java.util.Objects;

public class Position {
    private int row;
    private int col;


    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }
    public Position() {}

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public Position translate(int dRow, int dCol) {
        return new Position(row + dRow, col + dCol);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;
        Position position = (Position) o;
        return row == position.row && col == position.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public String toString() {
        return "(" + row + "," + col + ")";
    }
}
