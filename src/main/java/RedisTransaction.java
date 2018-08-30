import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import sun.security.krb5.internal.crypto.Crc32CksumType;
import sun.security.krb5.internal.crypto.crc32;

import java.util.List;
import java.util.zip.CRC32;

/**
 * Describe：redis的事务
 * Author：sunqiushun
 * Date：2018-08-21 18:18:00
 */
public class RedisTransaction {
    public static void main(String[] args) {
      /*  Jedis jedis = new Jedis();
        String key = "abc";
        jedis.setnx(key, "abc");  // setnx 做初始化
        System.out.println(doubleAccount(jedis, key));
        jedis.close();*/

    }

    public static int doubleAccount(Jedis jedis, String key) {
        while (true) {
            jedis.watch(key);
            // jedis.set(key, "fff"); // 在multi之前执行修改 multi之后的代码会执行失败
            Transaction tx = jedis.multi(); // 获取事务
            tx.set(key, "bcd"); // 使用事务执行
            tx.incr(key);

            boolean condition = true; // 如果某个条件失败了 执行discard 然后继续循环 否则执行事务
            if(condition == false){
                tx.discard(); // 不执行事务
            }else{
                List<Object> res = tx.exec(); // 执行成功会返回一个有元素的list
                System.out.println(res);
                if (res != null && res.size() > 0) { // 成功了
                    break;
                }
            }
        }
        return Integer.parseInt(jedis.get(key)); // 重新获取余额
    }
}
