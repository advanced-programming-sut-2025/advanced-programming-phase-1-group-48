package io.github.some_example_name.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.some_example_name.shared.model.GameState;

import java.util.List;

public class GameInstance implements Runnable {
    private final List<PlayerConnection> players;
    private final GameState gameState;
    private final ObjectMapper mapper = new ObjectMapper();
    private volatile boolean running = true;
    private final long tickMs;

    public GameInstance(List<PlayerConnection> players, GameState state, long tickMs) {
        this.players = players;
        this.gameState = state;
        this.tickMs = tickMs;
    }

    @Override
    public void run() {
        try {
            while (running) {
                String stateJson;
                synchronized (gameState) {
                    stateJson = mapper.writeValueAsString(java.util.Map.of("type","STATE", "payload", gameState));
                }
                for (PlayerConnection pc : players) {
                    pc.sendRaw(stateJson);
                }
                Thread.sleep(tickMs);
            }

        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() { running = false; }
}
