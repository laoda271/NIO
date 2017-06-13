import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by chenminghe on 2017/6/9.
 */
public class _02TestChannel {

    // 利用通道完成文件的复制(非直接缓冲区)
    @Test
    public void test1(){
        long start = System.currentTimeMillis();

        FileInputStream fis = null;
        FileOutputStream fos = null;
        // 1、获取通道
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try{
            fis = new FileInputStream("d:/code.zip");
            fos = new FileOutputStream("d:/code2.zip");

            inChannel = fis.getChannel();
            outChannel = fos.getChannel();

            // 2、分配指定大小缓冲区
            ByteBuffer buf = ByteBuffer.allocate(1024);

            // 3、将通道中的数据存入缓冲区
            while(inChannel.read(buf) != -1){
                buf.flip(); //切换到读模式
                // 4、将缓冲区中的数据写入通道中
                outChannel.write(buf);
                buf.clear();
            }

        } catch (Throwable e){
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(fis);
            IOUtils.closeQuietly(fos);
        }

        long end = System.currentTimeMillis();

        System.out.println("耗费时间为: " + (end - start));

    }
}
