import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import java.io.IOException;

/**
 * Describe：使用redis实现简单的限流
 * Author：sunqiushun
 * Date：2018-08-20 10:57:26
 */
public class SimpleRateLimiter {
    private Jedis jedis;

    public SimpleRateLimiter(Jedis jedis) {
        this.jedis = jedis;
    }

    public boolean isActionAllowed(String userId, String actionKey, int period, int maxCount) {
        String key = String.format("%s:%s", userId, actionKey);
        long nowTs = System.currentTimeMillis();
        Pipeline pipe = jedis.pipelined();
        pipe.multi();
        pipe.zadd(key, nowTs, "" + nowTs);
        pipe.zremrangeByScore(key, 0, nowTs - period * 1000);
        Response<Long> count = pipe.zcard(key);
        pipe.expire(key, period + 1);
        pipe.exec();
        try {
            pipe.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return count.get() <= maxCount;
    }

    public static void main(String[] args) {
        int count = 0;
        Jedis jedis = new Jedis();
        jedis.del("laoqian:reply");
        SimpleRateLimiter limiter = new SimpleRateLimiter(jedis);
        long start = System.currentTimeMillis();
        for (int i = 0; i < 20000000; i++) {
            boolean allowed = limiter.isActionAllowed("laoqian", "reply", 1, 1000);
            if(allowed == true) count ++;
        }
        long end = System.currentTimeMillis();
        long time = end - start;
        System.out.println("时间：" + time + " 次数：" + count + " 速率：" + count*1.0/time);

    }
}
