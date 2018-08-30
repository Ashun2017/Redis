/**
 * Describe：自定义实现BloomFilter 使用BitSet
 * Author：sunqiushun
 * Date：2018-08-17 14:44:02
 */

import java.util.BitSet;

/**
 * bloomFilter 的实现
 */
public class MyBloomFilter {
    private static final int DEFAULT_SIZE = 2 << 29;//布隆过滤器的比特长度
    private static final int[] seeds = {3, 5, 7, 11, 13, 17, 19, 23};//这里要选取质数，能很好的降低错误率
    private static BitSet bits = new BitSet(DEFAULT_SIZE); // 初始化BitSet
    private static SimpleHash[] func = new SimpleHash[seeds.length];


    public static void add(String value) {
        if (value == null) return;
        // 将字符串value哈希为8个或多个整数，然后在这些整数的bit上变为1
        System.out.print("value= " +value + " ");
        for (SimpleHash f : func) {
            bits.set(f.hash(value), true);
            System.out.print(f.hash(value) + " ");
        }
        System.out.println();
    }

    // 查询包含的方法
    public static boolean contains(String value) {
        if (value == null) return false;
        boolean ret = true;
        for (SimpleHash f : func) { //这里其实没必要全部跑完，只要一次ret==false那么就不包含这个字符串
            ret = bits.get(f.hash(value));
            if (ret == false) break;
        }
        return ret;
    }

    // 测试主类
    public static void main(String[] args) {
        String value = "bloomfilter";

        for (int i = 0; i < seeds.length; i++) {
            func[i] = new SimpleHash(DEFAULT_SIZE, seeds[i]);
        }

        int len = 10;
        for (int i = 1; i < len; i++) {
            add(value + i);
        }
        int errCount = 0;
        for (int i = 1; i < len; i++) {
            value = "bloomfilters" + i;
            if (contains(value) == true) {
                errCount++;
            }
        }
        System.out.println("判断错误的次数：" + errCount);
        System.out.println("判断错误的概率：" + (errCount * 1.0 / len) * 100 + "%");
    }
}

/**
 * hash函数实现类
 */
class SimpleHash {
    private int cap;
    private int seed;

    public SimpleHash(int cap, int seed) {
        this.cap = cap;
        this.seed = seed;
    }

    public int hash(String value) {//字符串哈希，选取好的哈希函数很重要
        int result = 0;
        int len = value.length();
        for (int i = 0; i < len; i++) {
            result = seed * result + value.charAt(i);
        }
        return (cap - 1) & result;
    }
}



