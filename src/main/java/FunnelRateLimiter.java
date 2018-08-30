import com.google.common.hash.Funnel;

import java.util.HashMap;
import java.util.Map;

/**
 * Describe：漏斗限流
 * Author：sunqiushun
 * Date：2018-08-20 09:35:25
 */
public class FunnelRateLimiter {

    private static Map<String, Funnel> funnels = new HashMap<String, Funnel>();
    private static int count = 0;

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000000; i++) {
            boolean actionAllowed = isActionAllowed("funnel", "sun", 6, 2);
            if (actionAllowed == true) count++;
        }
        double time = (System.currentTimeMillis() - start) * 1.0 / 1000;
        System.out.println("\n时间：" + time + "s 次数：" + count + " 速率：" + count * 1.0 / time);
    }

    public static boolean isActionAllowed(String userId, String actionKey, int capacity, float leakingRate) {
        String key = String.format("%s:%s", userId, actionKey);
        Funnel funnel = funnels.get(key);
        if (funnel == null) {
            funnel = new Funnel(capacity, leakingRate);
            funnels.put(key, funnel);
        }
        return funnel.watering(1); // 需要1个quota
    }

    /**
     * 漏斗类
     */
    static class Funnel {
        int capacity; // 漏斗容量
        float leakingRate; // 漏斗流水的速度
        int leftQuota; // 漏斗剩余空间
        long leakingTs; // 上一次漏水时间

        public Funnel(int capacity, float leakingRate) {
            this.capacity = capacity;
            this.leakingRate = leakingRate;
            this.leftQuota = capacity;
            this.leakingTs = System.currentTimeMillis();
        }

        // 制造剩余空间
        void makeSpace() {
            long nowTs = System.currentTimeMillis();
            long deltaTs = (nowTs - leakingTs) / 1000; // 距离上一次漏水过去了多少时间 单位秒（s）
            int deltaQuota = (int) (deltaTs * leakingRate); // 腾出的空间

            if (deltaQuota < 0) { // 间隔时间太长，整数数字过大溢出 从新初始化漏斗
                this.leftQuota = capacity; // 设置漏斗的大小
                this.leakingTs = nowTs; // 设置漏斗上次流水的时间
                return;
            }
            if (deltaQuota < 1) { // 腾出空间太小，最小单位是1
                return;
            }

            this.leftQuota += deltaQuota; // 增加剩余空间
            this.leakingTs = nowTs; // 记录漏水时间
            if (this.leftQuota > this.capacity) { // 剩余空间不得大于容量
                this.leftQuota = this.capacity;
            }
        }

        // 水流 需要的空间 quota
        boolean watering(int quota) {
            makeSpace(); // 制造剩余空间
            if (this.leftQuota >= quota) { // 剩余的空间大于 需要的空间 则返回true
                this.leftQuota -= quota;
                return true;
            }
            return false;
        }
    }
}
