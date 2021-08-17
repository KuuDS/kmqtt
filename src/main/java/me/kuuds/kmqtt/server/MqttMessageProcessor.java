package me.kuuds.kmqtt.server;

import java.util.List;

import com.google.common.collect.Lists;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttConnAckMessage;
import io.netty.handler.codec.mqtt.MqttConnAckVariableHeader;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttSubAckMessage;
import io.netty.handler.codec.mqtt.MqttSubAckPayload;
import io.netty.handler.codec.mqtt.MqttSubscribeMessage;
import io.netty.handler.codec.mqtt.MqttUnsubAckMessage;
import io.netty.handler.codec.mqtt.MqttUnsubAckPayload;
import io.netty.handler.codec.mqtt.MqttUnsubscribeMessage;
import me.kuuds.kmqtt.server.session.ConnectionSessionContext;

public class MqttMessageProcessor {

    private final Logger log = LoggerFactory.getLogger(MqttMessageProcessor.class);

    private ConnectionSessionContext sessionCtx;

    public MqttMessageProcessor(ConnectionSessionContext sessionCtx) {
        this.sessionCtx = sessionCtx;
    }

    public void processUnsubscribe(MqttUnsubscribeMessage message) {
        sessionCtx.unsubscribe(message);
        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.UNSUBACK, false, MqttQoS.AT_LEAST_ONCE, false,
                0);
        MqttUnsubAckPayload payload = new MqttUnsubAckPayload((short) 0);
        MqttMessage ackMsg = new MqttUnsubAckMessage(fixedHeader,
                MqttMessageIdVariableHeader.from(message.variableHeader().messageId()), payload);
        sessionCtx.sendMessage(ackMsg);
    }

    public void processSubscribe(MqttSubscribeMessage message) {
        sessionCtx.subscribe(message);
        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.SUBACK, false, MqttQoS.AT_LEAST_ONCE, false,
                0);
        MqttSubAckPayload payload = new MqttSubAckPayload(List.<Integer>of(0));
        MqttMessage ackMsg = new MqttSubAckMessage(fixedHeader,
                MqttMessageIdVariableHeader.from(message.variableHeader().messageId()), payload);
        sessionCtx.sendMessage(ackMsg);
    }

    public void processPingReq(ChannelHandlerContext ctx) {
        MqttFixedHeader pingHeader = new MqttFixedHeader(MqttMessageType.PINGRESP, false, MqttQoS.AT_MOST_ONCE, false,
                0);

        MqttMessage pingMsg = new MqttMessage(pingHeader);
        ctx.writeAndFlush(pingMsg);
    }

    public void processPublish(MqttPublishMessage message) {
        sessionCtx.publish(message);

    }

    public void processConnect(ChannelHandlerContext ctx, MqttConnectMessage message) {
        sessionCtx.connect(ctx, message);
        MqttFixedHeader fixHeader = new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_LEAST_ONCE, false,
                0);
        MqttConnAckVariableHeader connAckHeader = new MqttConnAckVariableHeader(
                MqttConnectReturnCode.CONNECTION_ACCEPTED, false);
        MqttConnAckMessage ackMessage = new MqttConnAckMessage(fixHeader, connAckHeader);
        ctx.writeAndFlush(ackMessage);
    }

    public void processDisconnect() {
        log.trace("[{}|{}] execute processDisconnect", sessionCtx.getClientId(), sessionCtx.getConnectId());
        sessionCtx.disconnect();
    }

    public void processDisconnect(ChannelHandlerContext ctx) {
        processDisconnect();
        ctx.close();
    }

}
