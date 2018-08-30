import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisURI;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.sync.RedisCommands;

import java.util.ArrayList;
import java.util.List;

/**
 * Describe：
 * Author：sunqiushun
 * Date：2018-08-17 16:42:12
 */
class RedisBloomFilter {
    private String chars;

    {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 26; i++) {
            builder.append((char) ('a' + i));
        }
        chars = builder.toString();
    }

    private String randomString(int n) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < n; i++) {
            int idx = (int) (Math.random() * chars.length());
            builder.append(chars.charAt(idx));
        }
        return builder.toString();
    }

    private List<String> randomUsers(int n) {
        List<String> users = new ArrayList<String>();
        for (int i = 0; i < 100; i++) {
            users.add(randomString(64));
        }
        return users;
    }

    public static void main(String[] args) {
        RedisBloomFilter bloomer = new RedisBloomFilter();
        List<String> users = bloomer.randomUsers(100000);
        List<String> usersTrain = users.subList(0, users.size() / 2);
        List<String> usersTest = users.subList(users.size() / 2, users.size());

        RedisClient client = RedisClient.create(RedisURI.create("redis://127.0.0.1:6379"));
        StatefulRedisConnection<String, String> connect = client.connect();
        RedisCommands<String, String> commands = connect.sync();
        commands.del("codehole");
       // commands.c("codehole", 50000, 0.001); // 对应 bf.reserve 指令
        String temp = "";
        for (String user : usersTrain) {

            commands.set("codehole", user);
            temp = user;
        }
        System.out.println(commands.exists("codehole", "ddddsdfas33322dfs"));


      /*  int falses = 0;
        for (String user : usersTest) {
            Long ret = commands.exists("codehole", user);
            if (ret > 0) {
                falses++;
            }
        }
        System.out.printf("%d %d\n", falses, usersTest.size());*/
        connect.close();
        client.shutdown();
    }
}

