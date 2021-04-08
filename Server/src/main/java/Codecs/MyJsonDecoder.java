package Codecs;

import com.google.gson.Gson;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import ru.kyper.FileData;

import java.util.List;

public class MyJsonDecoder extends MessageToMessageDecoder<String> {
    @Override
    protected void decode(ChannelHandlerContext ctx, String msg, List<Object> out){
        out.add(new Gson().fromJson(msg, FileData.class));
    }
}
