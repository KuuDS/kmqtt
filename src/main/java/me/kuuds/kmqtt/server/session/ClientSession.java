package me.kuuds.kmqtt.server.session;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ClientSession {

    private final String clientId;
    private Set<ConnectionSession> connectionSet = ConcurrentHashMap.newKeySet();

    public ClientSession(String clientId) {
        this.clientId = clientId;
    }

    public String getClientId() {
        return clientId;
    }

    public boolean addConnection(ConnectionSession connection) {
        return connectionSet.add(connection);
    }

    public boolean removeConnection(ConnectionSession connectionSession) {
        return connectionSet.remove(connectionSession);
    }

}
