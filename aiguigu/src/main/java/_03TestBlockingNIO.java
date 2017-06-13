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
 * 一、使用NIO完成网络通信的三核心：
 * 1、通道(Channel)：负责连接
 *
 *  java.nio.channels.Channel接口：
 *          |--SelectableChannel
 *          |--SocketChannel
 *          |--DaragramChannel
 *          |--Pipe.SinkChannel
 *          |--Pipe.SourceChannel
 * 2、缓冲区(Buffer)：负责数据的存取
 *
 * 3、选择器(Selector):是SelectableChannel的多路复用器。用于监控SelectableChannel的IO状况
 *
 * Created by chenminghe on 2017/6/13.
 */
public class _03TestBlockingNIO {

    @Test
    public void client() {
        // 1.获取通道
        SocketChannel socketChannel = null;
        FileChannel fileChannel = null;
        try {
            socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9898));
            fileChannel = FileChannel.open(Paths.get("d:/code.zip"), StandardOpenOption.READ);

            // 2.分配指定大小的缓冲区
            ByteBuffer buf = ByteBuffer.allocate(1024);

            // 3.读取本地文件,并发送到服务器
            while (fileChannel.read(buf) != -1) {
                buf.flip();
                socketChannel.write(buf);
                buf.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(socketChannel);
            IOUtils.closeQuietly(fileChannel);
        }
    }

    @Test
    public void server() {
        // 1、获取通道
        ServerSocketChannel ssChannel = null;
        FileChannel outChannel = null;
        SocketChannel sChannel = null;
        try {
            ssChannel = ServerSocketChannel.open();
            outChannel = FileChannel.open(Paths.get("d:/code11.zip"), StandardOpenOption.WRITE, StandardOpenOption.CREATE);

            // 2、绑定连接
            ssChannel.bind(new InetSocketAddress(9898));

            // 3、获取客户端连接通道
            sChannel = ssChannel.accept();

            // 4、分配指定大小的缓冲区
            ByteBuffer buf = ByteBuffer.allocate(1024);

            // 5、接受客户端的数据，并保持到本地
            while (sChannel.read(buf) != -1) {
                buf.flip();
                outChannel.write(buf);
                buf.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(ssChannel);
            IOUtils.closeQuietly(outChannel);
            IOUtils.closeQuietly(sChannel);
        }
    }
}
