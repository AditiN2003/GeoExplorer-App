package test.connect.geoexploreapp.websocket;

import android.util.Log;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;

public class CommentWebSocketManager {
    private static CommentWebSocketManager instance;
    private CommentWebSocketClient webSocketClient;
    private WebSocketListener webSocketListener;

    private CommentWebSocketManager() {}

    public static synchronized CommentWebSocketManager getInstance() {
        if (instance == null) {
            instance = new CommentWebSocketManager();
        }
        return instance;
    }

    public void setWebSocketListener(WebSocketListener listener) {
        this.webSocketListener = listener;
    }

    public void removeWebSocketListener() {
        this.webSocketListener = null;
    }

    public void connectWebSocket(String serverUrl) {
        try {
            URI serverUri = URI.create(serverUrl);
            webSocketClient = new CommentWebSocketClient(serverUri);
            webSocketClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        if (webSocketClient != null && webSocketClient.isOpen()) {
            webSocketClient.send(message);
        } else {
            Log.d("CommentWebSocket", "Connection not open. Message not sent.");
        }
    }

    public void disconnectWebSocket() {
        if (webSocketClient != null) {
            webSocketClient.close();
        }
    }

    private class CommentWebSocketClient extends WebSocketClient {
        private CommentWebSocketClient(URI serverUri) {
            super(serverUri);
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            Log.d("CommentWebSocket", "Connected");
            if (webSocketListener != null) {
                webSocketListener.onWebSocketOpen(handshakedata);
            }
        }

        @Override
        public void onMessage(String message) {
            Log.d("CommentWebSocket", "Received message: " + message);
            if (webSocketListener != null) {
                webSocketListener.onWebSocketMessage(message);
            }
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            Log.d("CommentWebSocket", "Connection closed");
            if (webSocketListener != null) {
                webSocketListener.onWebSocketClose(code, reason, remote);
            }
        }

        @Override
        public void onError(Exception ex) {
            Log.d("CommentWebSocket", "Error", ex);
            if (webSocketListener != null) {
                webSocketListener.onWebSocketError(ex);
            }
        }
    }
}
