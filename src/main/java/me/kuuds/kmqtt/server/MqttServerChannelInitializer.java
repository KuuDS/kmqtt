package me.kuuds.kmqtt.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import me.kuuds.kmqtt.server.session.ConnectionSession;
import me.kuuds.kmqtt.server.session.ConnectionSessionContext;

public class MqttServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final SystemContext ctx;

    public MqttServerChannelInitializer(SystemContext ctx) {
        super();
        this.ctx = ctx;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // SslHandler sslHandler = null;
        // if (sslEnable != null) {
        // sslHandler = context.getSslHandlerProvider().getSslHandler();
        // pipeline.addLast(sslHandler);
        // }
        pipeline.addLast("decoder", new MqttDecoder(1440));
        pipeline.addLast("encoder", MqttEncoder.INSTANCE);
        pipeline.addLast("logger", new LoggingHandler(LogLevel.INFO));

        ConnectionSession connectionSession = ctx.getSessionManager().newConnectionSession();
        ConnectionSessionContext sessionContext = new ConnectionSessionContext(ctx, connectionSession);
        MqttMessageProcessor processor = new MqttMessageProcessor(sessionContext);
        MqttServerChannelHandler handler = new MqttServerChannelHandler(processor);
        pipeline.addLast(handler);
        ch.closeFuture().addListener(handler);
    }

}
