package me.kuuds.kmqtt.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttSubscribeMessage;
import io.netty.handler.codec.mqtt.MqttUnsubscribeMessage;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class MqttServerChannelHandler extends ChannelInboundHandlerAdapter
        implements GenericFutureListener<Future<Void>> {

    private final Logger log = LoggerFactory.getLogger(MqttServerChannelHandler.class);

    private final MqttMessageProcessor processor;

    public MqttServerChannelHandler(MqttMessageProcessor processor) {
        this.processor = processor;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof MqttMessage) {
                MqttMessage message = (MqttMessage) msg;
                if (message.decoderResult().isSuccess()) {
                    processMessage(ctx, message);
                } else {
                    log.error("decode mqtt msg failed {}.", message.decoderResult().cause().getMessage());
                    ctx.close();
                }
            } else {
                ctx.close();
            }
        } finally {
            // ReferenceCountUtil.safeRelease(msg);
        }
    }

    private void processMessage(ChannelHandlerContext ctx, MqttMessage message) {
        MqttMessageType type = message.fixedHeader().messageType();
        switch (type) {
            case CONNECT:
                processor.processConnect(ctx, (MqttConnectMessage) message);
                break;
            case DISCONNECT:
                processor.processDisconnect(ctx);
                break;
            case SUBSCRIBE:
                processor.processSubscribe((MqttSubscribeMessage) message);
                break;
            case UNSUBSCRIBE:
                processor.processUnsubscribe((MqttUnsubscribeMessage) message);
                break;
            case PUBLISH:
                processor.processPublish((MqttPublishMessage) message);
                break;
            case PINGREQ:
                processor.processPingReq(ctx);
                break;
            default:
                log.debug("illegal message type []. abondoned.", type);
        }
    }

    @Override
    public void operationComplete(Future<Void> future) throws Exception {
        processor.processDisconnect();
    }

}
