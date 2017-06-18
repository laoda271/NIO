import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.channels.Pipe;

/**
 * 管道写入和读取数据
 * JAVA NIO管道是2个线程之间的单向数据连接
 * Pipe有一个source通道和一个sink通道。数据会被写到sink通道，从source通道读取。
 *             _________________________________
 *            |             Pipe                |
 * Thread A--->| Sink Channel-->Source Channel--|--> Thread B
 *            |_________________________________|
 *
 *
 *
 * Created by chenminghe on 2017/6/18.
 */
public class _07TestPie {

    @Test
    public void test() throws Exception {
        // 1.获取管道
        Pipe pipe = Pipe.open();

        // 2.将缓冲区的数据写入管道
        ByteBuffer buf = ByteBuffer.allocate(1024);

        Pipe.SinkChannel sinkChannel = pipe.sink();
        buf.put(new String("通过单向管道写数据").getBytes());
        buf.flip();
        sinkChannel.write(buf);

        // 3.读取缓冲区中的数据
        Pipe.SourceChannel sourceChannel = pipe.source();
        ByteBuffer buf2 = ByteBuffer.allocate(1024);
        // 4.读取缓冲区中的数据
        int len = sourceChannel.read(buf2);
        buf2.flip();
        System.out.println(new String(buf2.array(),0,len));

        sinkChannel.close();
        sourceChannel.close();

    }
}
