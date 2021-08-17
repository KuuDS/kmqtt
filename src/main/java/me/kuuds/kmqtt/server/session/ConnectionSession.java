package me.kuuds.kmqtt.server.session;

import java.util.UUID;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class ConnectionSession {

    private ChannelHandlerContext ctx;
    private String clientId;
    private final UUID connectId;

    public ConnectionSession(UUID connectId) {
        this.connectId = connectId;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public UUID getConnectId() {
        return connectId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public void sendMessage(MqttMessage message) {
        sendMessage(message, new GenericFutureListener<Future<Void>>() {

            @Override
            public void operationComplete(Future<Void> future) throws Exception {
            }

        });
    }

    public void sendMessage(MqttMessage message, GenericFutureListener<Future<Void>> promiseHandler) {
        ChannelPromise promise = ctx.newPromise();
        promise.addListener(promiseHandler);
        ctx.writeAndFlush(message, promise);
    }

    public void disconnect() {
        ctx.close();
    }

}
