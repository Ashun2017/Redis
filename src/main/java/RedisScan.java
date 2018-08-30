import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.List;
import java.util.Set;

/**
 * Describe：Scan函数测试
 * Author：sunqiushun
 * Date：2018-08-20 16:23:16
 */
public class RedisScan {
    public static void main(String[] args) {
        Jedis jedis = new Jedis();

        // Set<String> codehole = jedis.keys("scans_99*");
        String cursor = "-1";
        ScanResult<String> scan;
        List<String> result;
        ScanParams scanParams = new ScanParams();
        scanParams.match("ss*");
        scanParams.count(3);
        while (!cursor.equals("0")) {
            if (cursor.equals("-1")) {
                cursor = "0";
            }
            scan = jedis.scan(cursor, scanParams);
            result = scan.getResult();
            System.out.println(cursor + " " + result);
            cursor = scan.getStringCursor();
        }

        /*for (int i = 0; i < 50; i++){
            jedis.set("ss" + i, "ss_" + i);
            // jedis.del("scans_" + i);
        }*/


    }
}
