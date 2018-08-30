import redis.clients.jedis.Jedis;

/**
 * Describe：
 * Author：sunqiushun
 * Date：2018-08-16 17:55:55
 */
public class HyperLogLog {
    public static void main(String[] args) {
        Jedis jedis = new Jedis();
        jedis.set("s", "he");
        // jedis.del("");
        Boolean s2 = jedis.getbit("s", 5);
        String s4 = jedis.getrange("s", 0, 0);
        System.out.println(s4);
        System.out.println(s2);
        Long s = jedis.bitcount("s", 1, 1);
        System.out.println("bitcount= " + s);
        Long s1 = jedis.bitcount("s", 0, 0);
        System.out.println("bitcount= " + s1);
        Long s3 = jedis.bitpos("s", false);
        System.out.println(s3);

        // jedis.del("");
        for (int i = 0; i < 100000; i++) {
            jedis.pfadd("codehole", "user" + i);
        }
        long total = jedis.pfcount("codehole");
        System.out.printf("%d %d\n", 100000, total);
        jedis.close();
    }
}
