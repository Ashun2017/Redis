import com.sun.javafx.scene.control.skin.AccordionSkin;

/**
 * Describe：redis 中的skipList列表测试
 * Author：sunqiushun
 * Date：2018-08-28 14:11:59
 */
public class SkipListTest {

    private final static double ZSKIPLIST_P = 0.25;
    private final static int ZSKIPLIST_MAXLEVEL = 64;


    public static void main(String[] args) {
       //  int level = getLevel();

        long l = System.currentTimeMillis();
        long l1 = System.nanoTime();
        System.out.println(l + " " + ((l/60)%65535) + " " + (l/60 & 65535) + " " + l1);
       //  System.out.println(level);
    }

    public static int getLevel() {
        int level = 1;
        while (true) {
            double pow = 1 / Math.pow(2, (int) (Math.random() * 64));
            double v = ZSKIPLIST_P * 0xFFFF;
            System.out.println(pow + " " + v);
            if (pow > v) break;
            level += 1;
        }
        return (level < ZSKIPLIST_MAXLEVEL) ? level : ZSKIPLIST_MAXLEVEL;
    }
}
