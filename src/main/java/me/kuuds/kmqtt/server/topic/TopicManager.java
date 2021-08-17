package me.kuuds.kmqtt.server.topic;

import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttTopicSubscription;
import me.kuuds.kmqtt.server.session.ConnectionSessionContext;

public interface TopicManager {

    void onSubscribe(ConnectionSessionContext ctx, MqttTopicSubscription subscription);

    void onUnsubscribe(ConnectionSessionContext ctx, String topics);

    void onPublish(ConnectionSessionContext ctx, MqttPublishMessage message);

    void disconnect(ConnectionSessionContext connectionSessionContext);

}
