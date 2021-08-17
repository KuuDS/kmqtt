package me.kuuds.kmqtt.server;

import java.util.concurrent.atomic.AtomicBoolean;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import me.kuuds.kmqtt.server.session.SessionManager;
import me.kuuds.kmqtt.server.topic.SimpleTopicManager;

/**
 * Mqtt Server
 */
public class MqttServer {

    private ServerBootstrap b;

    private static final boolean FREE = false;
    private static final boolean BUSY = true;

    private static final boolean NOT_READY = false;
    private static final boolean READY = true;

    private final AtomicBoolean busy = new AtomicBoolean(FREE);
    private final AtomicBoolean ready = new AtomicBoolean(NOT_READY);
    private volatile ChannelFuture f;

    public void init() {
        b = new ServerBootstrap();
        EventLoopGroup ioGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        SystemContext ctx = new SystemContext();
        ctx.setSessionManager(new SessionManager());
        ctx.setTopicManager(new SimpleTopicManager());
        b.group(ioGroup, workerGroup).channel(NioServerSocketChannel.class).handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new MqttServerChannelInitializer(ctx));

        ready.set(READY);
    }

    public void start() {
        if (ready.get() && busy.compareAndSet(FREE, BUSY)) {
            f = b.bind(1883);
            busy.set(FREE);
        }
    }

    public void stop() {
        f.channel().close();
    }

}
