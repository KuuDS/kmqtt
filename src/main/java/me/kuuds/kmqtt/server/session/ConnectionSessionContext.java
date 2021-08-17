package me.kuuds.kmqtt.server.session;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageAggregationException;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttSubscribeMessage;
import io.netty.handler.codec.mqtt.MqttUnsubscribeMessage;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.ReflectionUtil;
import lombok.extern.slf4j.Slf4j;
import me.kuuds.kmqtt.server.SystemContext;

@Slf4j
public class ConnectionSessionContext {

    private SystemContext mainCtx;
    private ConnectionSession connectionSession;

    public ConnectionSessionContext(SystemContext ctx, ConnectionSession connectionSession) {
        this.mainCtx = ctx;
        this.connectionSession = connectionSession;
    }

    public String getClientId() {
        return connectionSession.getClientId();
    }

    public String getConnectId() {
        return connectionSession.getConnectId().toString();
    }

    public void subscribe(MqttSubscribeMessage message) {
        if (message.payload().topicSubscriptions() != null) {
            message.payload().topicSubscriptions().forEach(sub -> mainCtx.getTopicManager().onSubscribe(this, sub));
        }
    }

    public void unsubscribe(MqttUnsubscribeMessage message) {
        message.payload().topics().forEach(unsub -> mainCtx.getTopicManager().onUnsubscribe(this, unsub));
    }

    public void disconnect() {
        mainCtx.getSessionManager().disconnect(connectionSession);
        mainCtx.getTopicManager().disconnect(this);
    }

    public void connect(ChannelHandlerContext ctx, MqttConnectMessage message) {
        connectionSession.setCtx(ctx);
        mainCtx.getSessionManager().authorization(connectionSession, message);
    }

    public void publish(MqttPublishMessage message) {
        mainCtx.getTopicManager().onPublish(this, message);
    }

    public void onPublish(MqttPublishMessage message) {
        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.PUBLISH, false, MqttQoS.AT_MOST_ONCE, false,
                0);
        MqttPublishVariableHeader varHeader = new MqttPublishVariableHeader(message.variableHeader().toString(),
                message.variableHeader().packetId());
        ByteBuf payload = message.payload().slice();
        connectionSession.sendMessage(new MqttPublishMessage(fixedHeader, varHeader, payload),
                new GenericFutureListener<Future<Void>>() {
                    @Override
                    public void operationComplete(Future<Void> future) throws Exception {
                        log.debug("publish completed. Result is {}", future.isSuccess());
                    }
                });
    }

    public void sendMessage(MqttMessage message) {
        connectionSession.sendMessage(message);
    }

}
