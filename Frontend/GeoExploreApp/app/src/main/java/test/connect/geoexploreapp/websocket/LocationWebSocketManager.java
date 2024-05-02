package test.connect.geoexploreapp.websocket;

import android.util.Log;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;

public class LocationWebSocketManager {
    private static LocationWebSocketManager instance;
    private LocationWebSocketClient webSocketClient;
    private WebSocketListener webSocketListener;

    private LocationWebSocketManager() {}

    public static synchronized LocationWebSocketManager getInstance() {
        if (instance == null) {
            instance = new LocationWebSocketManager();
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
            webSocketClient = new LocationWebSocketClient(serverUri);
            webSocketClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        if (webSocketClient != null && webSocketClient.isOpen()) {
            webSocketClient.send(message);
        } else {
            Log.d("LocationWebSocket", "Connection not open. Message not sent.");
        }
    }

    public void disconnectWebSocket() {
        if (webSocketClient != null) {
            webSocketClient.close();
        }
    }

    private class LocationWebSocketClient extends WebSocketClient {
        private LocationWebSocketClient(URI serverUri) {
            super(serverUri);
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            Log.d("LocationWebSocket", "Connected");
            if (webSocketListener != null) {
                webSocketListener.onWebSocketOpen(handshakedata);
            }
        }

        @Override
        public void onMessage(String message) {
            Log.d("LocationWebSocket", "Received message: " + message);
            if (webSocketListener != null) {
                webSocketListener.onWebSocketMessage(message);
            }
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            Log.d("LocationWebSocket", "Connection closed");
            if (webSocketListener != null) {
                webSocketListener.onWebSocketClose(code, reason, remote);
            }
        }

        @Override
        public void onError(Exception ex) {
            Log.d("LocationWebSocket", "Error", ex);
            if (webSocketListener != null) {
                webSocketListener.onWebSocketError(ex);
            }
        }
    }
}
