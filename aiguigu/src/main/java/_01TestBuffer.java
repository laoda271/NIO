import org.junit.Test;

import java.nio.ByteBuffer;

/**
 * 一、缓冲区(Buffer):在Java NIO 中负责数据的存取。缓冲区就是数组。用于存储不同类型的数据
 *
 * 根据类型的不同(Boolean除外），提供了相应类型的缓冲区：
 * ByteBuffer
 * CharBuffer
 * ShortBuffer
 * IntBuffer
 * LongBuffer
 * FloatBuffer
 * DoubleBuffer
 *
 *
 * 上述缓冲区的管理方式几乎一样，通过allocate()获取缓冲区
 *
 * 二、缓冲区存取数据的两个核心方法：
 * put()：存入数据到缓冲区
 * get()：获取缓冲区中的数据
 *
 * 三、缓冲区四个核心属性：
 * capacity:容量，表示缓冲区中最大的存储数据的容量。一旦声明不能改变
 * limit：界限，表示缓冲区中可以操作数据的大小（limit后数据不能进行读写）
 * position：位置，表示缓冲区中正在操作数据的位置
 *
 * mark:标记，表示记录当前的position位置。可以通过reset恢复到mark位置
 *
 * 0 <= mark <= position <= limit <= capacity
 *
 * 四、直接缓冲区与非直接缓冲区
 * 非直接缓冲区：通过allocate方法分配缓冲区，将缓冲区建立在JVM的内存中
 * 直接缓冲区：通过allocateDirect方法分配直接缓冲区，将缓冲区建立在物理内存中，可以提高效率
 *
 *
 *
 *
 *
 *
 *
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

    @Test
    public void test2(){
        String str = "abcde";

        // 非直接缓冲区，建立在jvm内存之上
        ByteBuffer buf = ByteBuffer.allocate(1024);
        buf.put(str.getBytes());

        buf.flip();

        byte[] dst = new byte[buf.limit()];
        buf.get(dst,0,2);
        System.out.println(new String(dst,0,2));
        System.out.println("position: " + buf.position());

        //mark():标记,记录当前的position位置。可以通过reset恢复到mark的位置
        buf.mark();

        buf.get(dst,2,2);
        System.out.println(new String(dst,2,2));

        //reset():恢复到mark的位置
        buf.reset();
        System.out.println("after reset position: " + buf.position());

        //判断缓冲区中是否还有剩余数据
        if(buf.hasRemaining()){
            //获取缓冲区中还可以操作的数量
            System.out.println(buf.remaining());
        }
    }

    @Test
    public void test3(){
        // 直接缓冲区，建立在物理内存上，可以提升效率
        ByteBuffer buf = ByteBuffer.allocateDirect(1024);
        System.out.println(buf.isDirect());
    }
}

