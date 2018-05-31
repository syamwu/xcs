package syamwu.xchushi.fw.arithmetic.loadbalanc;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TestBalance {

    public static void main(String[] args) {
        int cpuCount = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(cpuCount * 2, cpuCount * 10, 10000l, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
        String[] ips = { "192.168.1.101", "192.168.1.102", "192.168.1.103", "192.168.1.104", "192.168.1.105" };// 初始化ip值
        int[] loads = { 2, 1, 5, 1, 1 };// 初始化权值比
        SimpleDynamicLoadBalance<String> lb = new SimpleDynamicLoadBalance<String>(ips, loads, 1000);
        lb.dynamicLoad(1, 300);
        //executor.execute(new TuTask01(lb, 300));
        executor.execute(new TuTask02(lb, 0, 300));
        executor.execute(new TuTask02(lb, 1, 100));
        executor.execute(new TuTask02(lb, 2, 600));
        executor.execute(new TuTask02(lb, 3, 300));
        executor.execute(new TuTask02(lb, 4, 400));
    }

    static class TuTask01 implements Runnable {
        SimpleDynamicLoadBalance<String> lb;
        int seed;

        TuTask01(SimpleDynamicLoadBalance<String> lb, int seed) {
            this.lb = lb;
            this.seed = seed;
        }

        public void run() {
            while (true) {
                try {
                    String ip = lb.loadBalance();
                    System.out.print("ip:" + ip + ",");
                    lb.dynamicLoad(ip, seed);
                    Thread.sleep(100l);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class TuTask02 implements Runnable {
        SimpleDynamicLoadBalance<String> lb;
        int index;
        int seed;

        /**
         * 
         * @param lb
         * @param index
         *            权值变化因子
         */
        TuTask02(SimpleDynamicLoadBalance<String> lb, int index, int seed) {
            this.lb = lb;
            this.index = index;
            this.seed = seed;
        }

        public void run() {
            while (true) {

                try {
                    lb.dynamicLoad(index, seed);
                    Thread.sleep(100l);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
