import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Scanner;

/**
 * Created by chenminghe on 2017/6/17.
 */
public class _06TestNonBlockingNIO2 {

    @Test
    public void send() throws Exception {
        DatagramChannel dc = DatagramChannel.open();
        dc.configureBlocking(false);

        ByteBuffer buf = ByteBuffer.allocate(1024);
        Scanner scan = new Scanner(System.in);
        while (scan.hasNext()) {
            String next = scan.next();
            byte[] bytes = next.getBytes();
            int len = bytes.length;
            int consume = 0;
            while (consume < len) {
                if (consume + 1024 > len) {
                    buf.put(bytes, consume, len - consume);
                    consume += len;
                } else {
                    buf.put(bytes, consume, 1024);
                    consume += 1024;
                }
                buf.flip();
                dc.send(buf, new InetSocketAddress("127.0.0.1", 9898));
                buf.clear();
            }
        }
        dc.close();
    }

    @Test
    public void receive() throws IOException {

        // 1.获取通道
        DatagramChannel dc = DatagramChannel.open();
        // 2.切换到非阻塞模式
        dc.configureBlocking(false);
        // 3.绑定连接
        dc.bind(new InetSocketAddress(9898));
        // 4.获取选择器
        Selector selector = Selector.open();
        // 5.将通道注册到选择器上,并且指定监听事件
        dc.register(selector, SelectionKey.OP_READ);

        // 6.轮询式的获取选择器上已经“准备就绪”的事件
        while (selector.select() > 0) {

            // 7. 获取当前选择器中所有注册的"选择键"(已就绪的监听事件)
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();

            while (it.hasNext()) {
                SelectionKey sk = it.next();

                // 9.判断是什么事件准备就绪
                if (sk.isReadable()) {
                    ByteBuffer buf = ByteBuffer.allocate(1024);
                    dc.receive(buf);
                    buf.flip();
                    System.out.print(new String(buf.array(), 0, buf.limit()));
                    buf.clear();
                }
            }

            it.remove();
        }
    }

    public static void main(String[] args) throws Exception {
        new _06TestNonBlockingNIO2().send();
    }
}
