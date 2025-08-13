package io.github.some_example_name.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.some_example_name.shared.model.actions.Action;
import io.github.some_example_name.shared.model.actions.MoveAction;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class PlayerConnection {
    public final Socket socket;
    public final PrintWriter out;
    private final BufferedReader in;
    private final Lobby lobby;
    private final String playerId;
    private final ObjectMapper mapper = new ObjectMapper();
    private Thread readerThread;
    private final AtomicBoolean running = new AtomicBoolean(false);

    public PlayerConnection(Socket socket, Lobby lobby, String playerId) throws IOException {
        this.socket = socket;
        this.lobby = lobby;
        this.playerId = playerId;
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void startListening() {
        if (running.getAndSet(true)) return;
        readerThread = new Thread(() -> {
            try {
                String line;
                while (running.get() && (line = in.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    try {
                        JsonNode node = mapper.readTree(line);
                        JsonNode typeNode = node.get("type");
                        if (typeNode != null) {
                            String t = typeNode.asText();
                            switch (t) {
                                case "ACTION": {
                                    JsonNode payload = node.get("payload");
                                    Action action = mapper.treeToValue(payload, Action.class);
                                    lobby.onActionReceived(this, action);
                                    break;
                                }
                                case "READY": {
                                    lobby.onPlayerReady(this); // جدید — باید در Lobby پیاده‌سازی شود
                                    break;
                                }
                                case "move": {
                                    MoveAction mv = mapper.treeToValue(node, MoveAction.class);
                                    lobby.onActionReceived(this, mv);
                                    break;
                                }
                                default: {
                                    System.out.println("PlayerConnection: unknown control message type=" + t);
                                }
                            }
                        } else {
                            // fallback: قدیمی — parse مستقیم به Action
                            Action action = mapper.treeToValue(node, Action.class);
                            lobby.onActionReceived(this, action);
                        }
                    } catch (Exception ex) {
                        System.err.println("PlayerConnection: failed parsing or applying action: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }

            } catch (IOException e) {
                System.err.println("PlayerConnection: IO error for " + playerId + " : " + e.getMessage());
            } finally {
                running.set(false);
                try { socket.close(); } catch (IOException ignored) {}
                lobby.onPlayerDisconnected(this);
            }
        }, "PlayerReader-" + playerId);
        readerThread.setDaemon(true);
        readerThread.start();
    }

    public void sendRaw(String json) {
        try {
            out.println(json);
            out.flush();
        } catch (Exception e) {
            System.err.println("sendRaw failed to " + playerId + ": " + e.getMessage());
        }
    }


    public String getPlayerId() { return playerId; }

    public void stop() {
        running.set(false);
        try { socket.close(); } catch (IOException ignored) {}
    }

    public Socket getSocket() {
        return socket;
    }

    public PrintWriter getOut() {
        return out;
    }
}
