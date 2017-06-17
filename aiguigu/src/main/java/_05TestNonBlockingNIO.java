import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Scanner;

/**
 * Created by chenminghe on 2017/6/14.
 *  一、使用NIO完成网络的三个核心
 *  1.通道(Channel):负责连接
 *  java.nio.channels.Channel接口：
 *      |-- SelectableChannel
 *          |--SocketChannel
 *          |--ServerSocketChannel
 *          |--DatagramChannel
 *
 *          |--Pipe.SinkChannel
 *          |--Pipe.SourceChannel
 *  2.缓冲区(Buffer):负责数据的存取
 *
 *  3.选择器(Selector):是SelectableChannel的多路复用器。用于监控SelectableChannel的IO状况
 *
 */
public class _05TestNonBlockingNIO {

    @Test
    public void client() throws IOException {

        // 1.获取通道
        SocketChannel sChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9898));

        // 2.切换到非阻塞模式
        sChannel.configureBlocking(false);

        // 3.分配指定大小的缓存区
        ByteBuffer buf = ByteBuffer.allocate(1024);

        // 4.发送数据给服务器
        Scanner scan = new Scanner(System.in);

        while (scan.hasNext()){
            String next = scan.next();
            byte[] src = next.getBytes();
            int len = src.length;
            int consumer = 0;
            while (consumer < len){
                if(consumer + 1024 > len){
                    buf.put(src,consumer,len-consumer);
                    consumer += len;
                } else{
                    buf.put(src,consumer,1024);
                    consumer += 1024;
                }
                buf.flip();
                sChannel.write(buf);
                buf.clear();
            }
        }

        // 5.关闭通道
        sChannel.close();

    }

    @Test
    public void server() throws IOException {
        // 1.获取通道
        ServerSocketChannel ssChannel = ServerSocketChannel.open();

        // 2.切换到非阻塞模式
        ssChannel.configureBlocking(false);

        // 3.绑定连接
        ssChannel.bind(new InetSocketAddress(9898));

        // 4.获取选择器
        Selector selector = Selector.open();

        // 5.将通道注册到选择器上，并且指定"监听接受事件"
        ssChannel.register(selector, SelectionKey.OP_ACCEPT);

        // 6.轮询式的获取选择器上已经"准备就绪"的事件
        while (selector.select() > 0) {

            // 7.获取当前选择器中所有注册的"选择键(已就绪监听事件)"
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();

            while (it.hasNext()) {
                // 8. 获取准备“就绪”的事件
                SelectionKey sk = it.next();

                // 9.判断具体是什么事件准备就绪
                if (sk.isAcceptable()) {
                    // 10.若“接受就绪”,获取客户端连接
                    SocketChannel sChannel = ssChannel.accept();

                    // 11.切换到非阻塞模式
                    sChannel.configureBlocking(false);

                    // 12.将该通道注册到选择器上
                    sChannel.register(selector, SelectionKey.OP_READ);
                } else if (sk.isReadable()) {
                    // 13.获取当前选择器上“读就绪”状态的通道
                    SocketChannel sChannel = (SocketChannel) sk.channel();

                    // 14.读取数据
                    ByteBuffer buf = ByteBuffer.allocate(1024);
                    int len = 0;
                    while ((len = sChannel.read(buf)) > 0) {
                        buf.flip();
                        System.out.print(new String(buf.array(), 0, len));
                        buf.clear();
                    }
                }

                // 15.取消选择键 SelectionKey
                it.remove();
            }
        }

    }

    public static void main(String[] args) throws Exception {
        _05TestNonBlockingNIO tt = new _05TestNonBlockingNIO();
        tt.client();
    }
}
