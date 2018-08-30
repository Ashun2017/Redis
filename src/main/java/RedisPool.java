import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Describe：jedispool的封装使用
 * Author：sunqiushun
 * Date：2018-08-26 18:53:39
 */
public class RedisPool {
    private JedisPool pool; // jedis线程池

    public RedisPool() {
        this.pool = new JedisPool();
    }

    public void execute(CallWithJedis caller) {
        Jedis jedis = pool.getResource();
        try {
            caller.call(jedis);
        } catch (Exception e) { // 此处可以进行多次调用 可以设置调用时间限制
            caller.call(jedis);
        } finally { // 释放jedis
            jedis.close();
        }
    }

    public static void main(String[] args) {
        RedisPool redis = new RedisPool();
        // 因为接口类中的变量属于闭包之中 不可以直接获取 所以使用Holder类
        Holder<String> countHolder = new Holder<>();

        // 方法二：使用Lambda表达式 推荐使用
        redis.execute(jedis -> {
            String value = jedis.get("sun");
            countHolder.value(value);
        });
        System.out.println(countHolder.value());

        // 方法一： 使用传统的方式生成接口类
        redis.execute(new CallWithJedis() {
            @Override
            public void call(Jedis jedis) {
                String temp = jedis.get("sun");
                countHolder.value(temp + " cc");
            }
        });
        System.out.println(countHolder.value());
    }

    // 定义接口类
    interface CallWithJedis {
        public void call(Jedis jedis);
    }

    // 静态内部holder类
    static class Holder<T> {
        private T value;

        public Holder() {
        }

        public Holder(T value) {
            this.value = value;
        }

        public void value(T value) {
            this.value = value;
        }

        public T value() {
            return value;
        }
    }
}



