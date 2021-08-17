package me.kuuds.kmqtt.server;

public class App {

    public static void main(String[] args) {
        MqttServer mqttServer = new MqttServer();
        mqttServer.init();
        mqttServer.start();
    }
}