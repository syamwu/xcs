package test.io.nio;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

public class NioThreadExecutor {

    private static ThreadPoolExecutor pool = null;

    /**
     * 线程池初始化方法
     * 
     * corePoolSize 核心线程池大小----10 maximumPoolSize 最大线程池大小----30 keepAliveTime
     * 线程池中超过corePoolSize数目的空闲线程最大存活时间----30+单位TimeUnit TimeUnit
     * keepAliveTime时间单位----TimeUnit.MINUTES workQueue 阻塞队列----new
     * ArrayBlockingQueue<Runnable>(10)====10容量的阻塞队列 threadFactory 新建线程工厂----new
     * CustomThreadFactory()====定制的线程工厂 rejectedExecutionHandler
     * 当提交任务数超过maxmumPoolSize+workQueue之和时,
     * 即当提交第41个任务时(前面线程都没有执行完,此测试方法中用sleep(100)),
     * 任务会交给RejectedExecutionHandler来处理
     */
    public static void init() {
        pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(2000);
//        pool = new ThreadPoolExecutor(1000, 2000, 300, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>(Integer.MAX_VALUE),
//                new NioThreadFactory(), new NioRejectedExecutionHandler());
        iniPool();
    }

    public void destory() {
        if (pool != null) {
            pool.shutdownNow();
        }
    }

    public static ExecutorService getExecutorService() {
        return pool;
    }
    
    public static void iniPool() {
        pool.prestartAllCoreThreads();
    }

    public ExecutorService getCustomThreadPoolExecutor() {
        return pool;
    }

    private static class NioThreadFactory implements ThreadFactory {

        private AtomicInteger count = new AtomicInteger(0);

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            String threadName = NioThreadExecutor.class.getSimpleName() + count.addAndGet(1);
            t.setName(threadName);
            Server.bq.add(threadName);
            return t;
        }
    }

    private static class NioRejectedExecutionHandler implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            // 记录异常
            // 报警处理等
            System.out.println("error.............");
        }
    }
    
    private static AtomicInteger hh = new AtomicInteger(0);

    // 测试构造的线程池
    public static void main(String[] args) {
        //NioThreadExecutor exec = new NioThreadExecutor();
        // 1.初始化
        //exec.init();
        
        
        ExecutorService pool = NioThreadExecutor.getExecutorService();
        for (int i = 1; i < 10000; i++) {
            //System.out.println("提交第" + i + "个任务!");
            pool.execute(new Runnable() {
                @Override
                public void run() {
//                    try {
//                        Thread.sleep(3000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                    System.out.println("running====="+(hh.addAndGet(1)));
                }
            });
        }
        
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        System.out.println("endhhh:"+hh);
        pool.shutdown();

        // 2.销毁----此处不能销毁,因为任务没有提交执行完,如果销毁线程池,任务也就无法执行了
        // exec.destory();

//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

}
