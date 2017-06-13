import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Created by chenminghe on 2017/6/13.
 */
public class _04TestBlockingNIO2 {

    @Test
    public void client(){
        SocketChannel socketChannel = null;
        FileChannel inChannel = null;
        try{
            socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9898));
            inChannel = FileChannel.open(Paths.get("d:/code.zip"), StandardOpenOption.READ);
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            while(inChannel.read(byteBuffer) != -1){
                byteBuffer.flip();
                socketChannel.write(byteBuffer);
                byteBuffer.clear();
            }
            socketChannel.shutdownOutput();

            // 接收服务器端的反馈
            int len = 0;
            while((len = socketChannel.read(byteBuffer)) != -1){
                byteBuffer.flip();
                System.out.println(new String(byteBuffer.array(),0,len));
                byteBuffer.clear();
            }

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(socketChannel);
            IOUtils.closeQuietly(inChannel);
        }
    }

    @Test
    public void server(){
        ServerSocketChannel serverSocketChannel = null;
        FileChannel outChannel = null;
        SocketChannel socketChannel = null;
        try{
            serverSocketChannel = ServerSocketChannel.open();
            outChannel = FileChannel.open(Paths.get("d:/code.zipnn"), StandardOpenOption.WRITE,StandardOpenOption.CREATE);

            serverSocketChannel.bind(new InetSocketAddress(9898));
            socketChannel = serverSocketChannel.accept();

            ByteBuffer buf = ByteBuffer.allocate(1024);

            while (socketChannel.read(buf) != -1){
                buf.flip();
                outChannel.write(buf);
                buf.clear();
            }

            // 发送反馈消息给客户端
            buf.put("服务端接受数据成功!\r\naaa".getBytes());
            buf.flip();
            socketChannel.write(buf);

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(serverSocketChannel);
            IOUtils.closeQuietly(outChannel);
            IOUtils.closeQuietly(socketChannel);
        }

    }
}
