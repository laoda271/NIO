import org.junit.Test;

import java.nio.ByteBuffer;

/**
 * Created by chenminghe on 2017/6/8.
 */
public class _01TestBuffer {

    @Test
    public void test1() {
        String str = "abcde";
        // 1.分配一个指定大小的缓冲区
        ByteBuffer buf = ByteBuffer.allocate(1024);
        printBufferDetail(buf, "allocate");

        // 2. 利用put()存入数据到缓冲区
        buf.put(str.getBytes());
        printBufferDetail(buf,"put");

        // 3.切换到读模式 按开关
        buf.flip();
        printBufferDetail(buf,"flip");

        // 4.利用get读取缓存区中的数据
        byte[] dst = new byte[buf.limit()];
        buf.get(dst);
        System.out.println(new String(dst,0,dst.length));
        printBufferDetail(buf,"get");

        // 5.倒带 rewind() 倒回去读
        buf.rewind();
        printBufferDetail(buf,"rewind");

        // 6.clear() 清空缓冲区,但是缓冲区中的数据依然存在，但是处于被遗忘的状态
        buf.clear();
        printBufferDetail(buf,"clear");

        System.out.println((char)buf.get());


    }

    private void printBufferDetail(ByteBuffer buf, String method) {

        System.out.println("------------------" + method + "()-------------------------");
        System.out.println("position(位置，缓冲区中正在操作数据的位置):" + buf.position());
        System.out.println("limit(界限，缓冲区中可以操作数据的大小):" + buf.limit());
        System.out.println("capacity(容量，缓冲区中最大的存储数据容量，一旦声明不可改变):" + buf.capacity());
    }
}

