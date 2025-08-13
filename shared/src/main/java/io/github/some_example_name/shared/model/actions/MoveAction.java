package io.github.some_example_name.shared.model.actions;


public class MoveAction extends Action {
    private String playerId;
    private int dx, dy;

    public MoveAction() {}  // برای deserialization

    public MoveAction(String playerId, int dx, int dy) {
        this.playerId = playerId;
        this.dx = dx;
        this.dy = dy;
    }

    public String getPlayerId() {
        return playerId;
    }

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }

    @Override
    public String toString() {
        return "MoveAction{" +
            "playerId='" + playerId + '\'' +
            ", dx=" + dx + ", dy=" + dy + '}';
    }
}
