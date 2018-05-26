package com.xchushi.fw.transfer.runner;

@Deprecated
public class FastFailCollectRunner implements Runnable {

    @Override
    public void run() {
        // TODO Auto-generated method stub
        
    }

//    private Collected<StringSpliceEntity, String> collected;
//
//    private DefalutCollectSendRunner defalutCollectSendRunner;
//    
//    private int queueLoopCount = 10;
//    
//    private int collectLoopCount = 10;
//
//    private static Logger logger = SysLoggerFactory.getLogger(FastFailCollectRunner.class);
//
//    public FastFailCollectRunner(Configure config, Collected<StringSpliceEntity, String> collectible,
//            DefalutCollectSendRunner defalutCollectSendRunner) {
//        this.collected = collectible;
//        this.defalutCollectSendRunner = defalutCollectSendRunner;
//        if (config != null) {
//            queueLoopCount = config.getProperty("queueLoopCount", Integer.class, 30);
//            collectLoopCount = config.getProperty("fastFailCollectLoopCount", Integer.class, 10);
//        }
//    }
//
//    @Override
//    public void run() {
//        while (true) {
//            try {
//                StringSpliceEntity sendEntity = collected.collect();
//                if (sendEntity != null) {
//                    defalutCollectSendRunner.sendingFailed(sendEntity, null);
//                }
//            } catch (Exception e) {
//                logger.error(e.getMessage(), e);
//            }
//        }
//    }

}
