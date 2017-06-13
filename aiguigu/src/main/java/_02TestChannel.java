import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
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
            outChannel.transferFrom(inChannel, 0, inChannel.size());
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

            for(ByteBuffer byteBuffer : bufs){
                byteBuffer.flip();
            }
            System.out.println(new String(bufs[0].array(),0,bufs[0].limit()));
            System.out.println("----------------");
            System.out.println(new String(bufs[1].array(),0,bufs[1].limit()));

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
}
