import Codecs.MyJsonDecoder;
import Handlers.ServerHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ServerApp {
    private static final int PORT = 9000;
    private static final String HOSTNAME = "localhost";
    private static final String DIRECTORY = "D:\\Network_Storage";

    public static void main(String[] args) throws InterruptedException, IOException {
        Path dir = Paths.get(DIRECTORY);
        if (!Files.exists(dir)) {
            Files.createDirectory(dir);
        }
        new ServerApp().start();
    }

    public void start() throws InterruptedException {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap server = new ServerBootstrap();
            server
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new JsonObjectDecoder(), new StringDecoder(), new StringEncoder(), new MyJsonDecoder(), new ServerHandler(DIRECTORY));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(
                            ChannelOption.WRITE_BUFFER_WATER_MARK,
                            new WriteBufferWaterMark(1024 * 1024 * 5, 1024 * 1024 * 10)
                    );

            ChannelFuture future = server.bind(HOSTNAME, PORT).sync();

            System.out.println("Server started");

            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}