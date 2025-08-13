package io.github.some_example_name.shared.model;

public class PlayerModel {
    public int x, y;
    public String name;

    public PlayerModel() {}
    public PlayerModel(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }
}
