package me.kuuds.kmqtt.server.pojo;

import io.netty.handler.codec.mqtt.MqttQoS;

public class Topic {

    private String topicPattern;
    private MqttQoS qos;

    public String getTopicPattern() {
        return topicPattern;
    }

    public MqttQoS getQos() {
        return qos;
    }

    public void setQos(MqttQoS qos) {
        this.qos = qos;
    }

    public void setTopicPattern(String topicPattern) {
        this.topicPattern = topicPattern;
    }

}
