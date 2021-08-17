package me.kuuds.kmqtt.server.topic;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttTopicSubscription;
import me.kuuds.kmqtt.server.session.ConnectionSessionContext;

public class SimpleTopicManager implements TopicManager {

    private final ConcurrentHashMap<String, Set<ConnectionSessionContext>> topicMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<ConnectionSessionContext, Set<String>> connSubscribeMap = new ConcurrentHashMap<>();

    @Override
    public void onSubscribe(ConnectionSessionContext ctx, MqttTopicSubscription subscription) {
        Set<ConnectionSessionContext> ctxSet = topicMap.computeIfAbsent(subscription.topicName(),
                topic -> ConcurrentHashMap.newKeySet());
        ctxSet.add(ctx);

        Set<String> topicSet = connSubscribeMap.computeIfAbsent(ctx, c -> ConcurrentHashMap.newKeySet());
        topicSet.add(subscription.topicName());
    }

    @Override
    public void onUnsubscribe(ConnectionSessionContext ctx, String topic) {
            // KeySetView<ConnectionSessionContext, Boolean> connectionSet = topicMap.remove(topic);
            topicMap.remove(topic);
    }

    @Override
    public void onPublish(ConnectionSessionContext ctx, MqttPublishMessage msg) {
        topicMap.computeIfPresent(msg.variableHeader().topicName(), (t, cnnSet) -> {
            cnnSet.forEach(c -> c.onPublish(msg));
            return cnnSet;
        });
    }

    @Override
    public void disconnect(ConnectionSessionContext ctx) {
        Set<String> topics = connSubscribeMap.remove(ctx);
        if (topics != null) {
            for (String topic : topics) {
                Set<ConnectionSessionContext> connSet = topicMap.get(topic);
                if (connSet != null) {
                    connSet.remove(ctx);
                }
            }
        }
    }

}
