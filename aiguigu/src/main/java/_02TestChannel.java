import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Set;

/**
 * 一、通道（Channel)：用于源节点与目标节点的连接。在Java NIO中负责缓冲区中数据的传输。Channel本身不存储数据。因此需要配合
 * 缓冲区进行传输。
 * <p>
 * 二、通道主要实现类
 * java.nio.channels.Channel接口：
 * |--FileChannel
 * |--SocketChannel
 * |--ServerSocketChannel
 * |--DatagramChannel
 * <p>
 * 三、获取通道
 * 1、JAVA针对支持通道的类提供了getChannel()方法
 * 本地IO:
 * FileInputStream/FileOutputStream
 * RandomAccessFile
 * <p>
 * 网络IO:
 * Socket
 * ServerSocket
 * DatagramSocket
 * 2.在JDK1.7中的NIO.2针对各个通道提供了静态方法open()
 * 3.在JDK1.7中的NIO.2的Files工具类的newByteChannel()
 * <p>
 * <p>
 * 四、通道之间的数据传输
 * transferFrom()
 * transferTo()
 *
 * 五、分散(Scatter)与聚集(Gather)
 * 分散读取（Scattering Reads):将通道中的数据分散到多个缓冲区
 * 聚集写入（Gathering Writes):将多个缓冲区的数据聚集到通道中
 *
 * 六、字符集：Charset
 * 编码：字符串->字节数组
 * 解码：字节数组->字符串
 *
 * Created by chenminghe on 2017/6/9.
 */
public class _02TestChannel {

    // 利用通道完成文件的复制(非直接缓冲区) 9986
    @Test
    public void test1() {
        long start = System.currentTimeMillis();

        FileInputStream fis = null;
        FileOutputStream fos = null;
        // 1、获取通道
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try {
            fis = new FileInputStream("d:/code.zip");
            fos = new FileOutputStream("d:/code2.zip");

            inChannel = fis.getChannel();
            outChannel = fos.getChannel();

            // 2、分配指定大小缓冲区
            ByteBuffer buf = ByteBuffer.allocate(1024);

            // 3、将通道中的数据存入缓冲区
            while (inChannel.read(buf) != -1) {
                buf.flip(); //切换到读模式
                // 4、将缓冲区中的数据写入通道中
                outChannel.write(buf);
                buf.clear();
            }

        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(fis);
            IOUtils.closeQuietly(fos);
        }

        long end = System.currentTimeMillis();

        System.out.println("耗费时间为: " + (end - start));

    }

    // 利用直接缓冲区完成文件的复制(内存映射文件) 4863 明显快了
    @Test
    public void test2() {
        long start = System.currentTimeMillis();

        FileChannel inchannel = null;
        FileChannel outChannel = null;
        try {
            inchannel = FileChannel.open(Paths.get("d:/code.zip"), StandardOpenOption.READ);
            outChannel = FileChannel.open(Paths.get("d:/code3.zip"), StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.CREATE);

            // 内存映射文件
            MappedByteBuffer inMappedBuf = inchannel.map(FileChannel.MapMode.READ_ONLY, 0, inchannel.size());
            MappedByteBuffer outMappedBuf = outChannel.map(FileChannel.MapMode.READ_WRITE, 0, inchannel.size());

            // 直接对缓冲区进行数据的读写操作
            byte[] dst = new byte[inMappedBuf.limit()];
            inMappedBuf.get(dst);
            outMappedBuf.put(dst);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(inchannel);
            IOUtils.closeQuietly(outChannel);
        }

        long end = System.currentTimeMillis();
        System.out.println("耗费时间为: " + (end - start));
    }

    // 通道之间数据之间传输（直接缓冲区）547 更快了
    @Test
    public void test3() {
        long start = System.currentTimeMillis();
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try {
            inChannel = FileChannel.open(Paths.get("d:/code.zip"), StandardOpenOption.READ);
            outChannel = FileChannel.open(Paths.get("d:/code5.zip"), StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.CREATE);
            inChannel.transferTo(0, inChannel.size(), outChannel);
            //outChannel.transferFrom(inChannel, 0, inChannel.size());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(inChannel);
            IOUtils.closeQuietly(outChannel);
        }
        long end = System.currentTimeMillis();
        System.out.println("耗费时间：" + (end - start));
    }

    // 分散和聚集
    @Test
    public void test4() {

        RandomAccessFile raf1 = null;
        RandomAccessFile raf2 = null;
        FileChannel inChannel = null;
        FileChannel channel2 = null;
        try {
            // 1.获取通道
            raf1 = new RandomAccessFile("d:/1.txt", "rw");
            inChannel = raf1.getChannel();
            // 2.分配指定大小的缓冲区
            ByteBuffer buf1 = ByteBuffer.allocate(10);
            ByteBuffer buf2 = ByteBuffer.allocate(1024);

            //3.分散读取
            ByteBuffer[] bufs = {buf1, buf2};
            inChannel.read(bufs);

            for (ByteBuffer byteBuffer : bufs) {
                byteBuffer.flip();
            }
            System.out.println(new String(bufs[0].array(), 0, bufs[0].limit()));
            System.out.println("----------------");
            System.out.println(new String(bufs[1].array(), 0, bufs[1].limit()));

            // 4.聚集写入
            raf2 = new RandomAccessFile("d:/2.txt", "rw");
            channel2 = raf2.getChannel();

            channel2.write(bufs);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(raf1);
            IOUtils.closeQuietly();
        }

    }

    @Test
    public void test5() {
        Map<String, Charset> map = Charset.availableCharsets();
        Set<Map.Entry<String, Charset>> set = map.entrySet();
        for (Map.Entry<String, Charset> entry : set) {
            System.out.println(entry.getKey() + "=" + entry.getValue());
        }
    }

    // 字符集
    @Test
    public void test6() throws CharacterCodingException {
        Charset gbk = Charset.forName("GBK");

        // 获取编码器
        CharsetEncoder ce = gbk.newEncoder();

        // 获取解码器
        CharsetDecoder cd = gbk.newDecoder();

        CharBuffer cBuf = CharBuffer.allocate(1024);
        cBuf.put("杜一凡威武！");
        cBuf.flip();

        // 编码
        ByteBuffer bBuf = ce.encode(cBuf);

        for (int i = 0; i < 12; i++) {
            System.out.println(bBuf.get());
        }

        // 解码
        bBuf.flip();
        CharBuffer cBuf2 = cd.decode(bBuf);
        System.out.println(cBuf2.toString());

        System.out.println("-------------------------------");

        Charset cs2 = Charset.forName("GBK");
        bBuf.flip();
        CharBuffer cBuf3 = cs2.decode(bBuf);
        System.out.println(cBuf3.toString());

    }
}
