package io.github.some_example_name.client;

import io.github.some_example_name.shared.model.GameState;
import io.github.some_example_name.shared.model.actions.MoveAction;
import io.github.some_example_name.shared.model.actions.Action;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.Socket;

public class TestClient {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 5555;

        try {
            Socket socket = new Socket(host, port);
            System.out.println("✅ Connected to server");

            ObjectMapper mapper = new ObjectMapper();

            // آماده‌سازی reader/writer
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter    writer = new PrintWriter(socket.getOutputStream(), true);

            // ۱) نخ جداگانه برای گوش دادن به سرور
            new Thread(() -> {
                try {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        // اینجا می‌آید: یا ACK یا JSON کل GameState
                        System.out.println("📥 Update from server: " + line);

                        // اگر می‌خواهی به GameState تبدیلش کنی:
                        GameState state = mapper.readValue(line, GameState.class);
                        // و بعد مثلاً:
                        // myGameView.onGameState(state);
                    }
                } catch (IOException e) {
                    System.err.println("Reader thread error: " + e.getMessage());
                }
            }).start();

            // ۲) ارسال یک اکشن نمونه
            Action action = new MoveAction("player1", 2, 3);
            String json = mapper.writeValueAsString(action);
            writer.println(json);
            System.out.println("📤 Sent action: " + json);

            // اگر بخواهی بعداً اکشن‌های دیگری بفرستی:
            // writer.println(mapper.writeValueAsString(anotherAction));

            // (این نخ main اینجا می‌تواند کارهای دیگری هم بکند یا همین‌جا منتظر بماند)
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
