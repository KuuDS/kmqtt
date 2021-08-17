package me.kuuds.kmqtt.server.session;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.handler.codec.mqtt.MqttConnectMessage;
import me.kuuds.kmqtt.server.auth.AuthenticationManager;
import me.kuuds.kmqtt.server.auth.SimpleAuthenticationManager;
import me.kuuds.kmqtt.server.exception.MqttServerException;

public class SessionManager {

    private AuthenticationManager AuthenticationManager = new SimpleAuthenticationManager();

    private Map<String, ClientSession> clientSessionMap = new ConcurrentHashMap<>();

    public ConnectionSession newConnectionSession() {
        return new ConnectionSession(UUID.randomUUID());
    }

    public ClientSession newClientSession(String clientId) {
        return new ClientSession(clientId);
    }

    public void authorization(ConnectionSession connectionSession, MqttConnectMessage message) {
        boolean isAuthorized = AuthenticationManager.auth();

        if (!isAuthorized) {
            connectionSession.disconnect();
            return;
        }
        String clientId = message.payload().clientIdentifier();
        connectionSession.setClientId(clientId);
        ClientSession clientSession = clientSessionMap.computeIfAbsent(connectionSession.getClientId(),
                this::newClientSession);

        boolean isDup = clientSession.addConnection(connectionSession);
        if (!isDup) {
            throw new MqttServerException();
        }
    }

    public void disconnect(ConnectionSession connectionSession) {
        ClientSession clientSession = clientSessionMap.get(connectionSession.getClientId());
        if (clientSession != null) {
            clientSession.removeConnection(connectionSession);
        }
        connectionSession.disconnect();
    }
}
