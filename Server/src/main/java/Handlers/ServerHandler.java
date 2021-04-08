package Handlers;

import com.google.gson.Gson;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.kyper.FileData;

import java.io.FileInputStream;
import java.io.IOException;

public class ServerHandler extends SimpleChannelInboundHandler<FileData> {

    private final String directory;

    public ServerHandler(String directory) {
        this.directory = directory;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FileData msg) throws IOException, InterruptedException {
        System.out.println(msg);
        Gson gson = new Gson();
        switch (msg.getCommandType()) {
            case 0 -> {
                msg.writeFileData(directory);
                String gs = gson.toJson(new FileData(msg.getFileName(), new byte[0], msg.getMessageNumber(), FileData.RECEIVE));
                System.out.println("gs.length = " + gs.length());
                ctx.writeAndFlush(gs);
            }
            case 1 -> {

                try (FileInputStream in = new FileInputStream(directory + "\\" +  msg.getFileName())) {
                    int messageNumber = 0;
                    int length;
                    do {
                        FileData fileData = new FileData(msg.getFileName(), in.readNBytes(FileData.MAX_LENGTH), messageNumber, FileData.SEND);
                        while (true) {
                            if(ctx.channel().isWritable()) {
                                ctx.writeAndFlush(gson.toJson(fileData));
                                break;
                            } else
                                Thread.sleep(100);
                        }
                        length = fileData.getData().length;
                        messageNumber++;
                    } while (length == FileData.MAX_LENGTH);
                }
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client connected: " + ctx.name());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client disconnected: " + ctx.name());
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (!cause.getMessage().equals("Connection reset")) {
            super.exceptionCaught(ctx, cause);
        }
    }
}
