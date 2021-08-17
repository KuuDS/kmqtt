package me.kuuds.kmqtt.server;

import me.kuuds.kmqtt.server.session.SessionManager;
import me.kuuds.kmqtt.server.topic.TopicManager;

public class SystemContext {

    private boolean ssl;

    private SessionManager sessionManager;
    private TopicManager topicManager;

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public TopicManager getTopicManager() {
        return topicManager;
    }

    public void setTopicManager(TopicManager topicManager) {
        this.topicManager = topicManager;
    }

    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public boolean isSsl() {
        return ssl;
    }

    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }
}
