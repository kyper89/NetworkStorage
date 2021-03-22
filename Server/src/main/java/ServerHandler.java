import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws IOException {

        System.out.println("Read message");

        Path directory = Paths.get("D:\\Network_Storage");
        if (!Files.exists(directory)) {
            Files.createDirectory(directory);
        }

        Path file = Paths.get("D:\\Network_Storage\\1.txt");
        if (!Files.exists(file)) {
            Files.createFile(file);
        }

        RandomAccessFile aFile = new RandomAccessFile("D:\\Network_Storage\\1.txt", "rw");
        FileChannel fileChannel = aFile.getChannel();

        ByteBuf in = (ByteBuf) msg;
        fileChannel.write(in.nioBuffer());
        fileChannel.close();

        ctx.writeAndFlush("File accepted");
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        System.out.println("Client connected");
    }
}
