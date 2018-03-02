package com.xchushi.transfer.runner;

import org.slf4j.Logger;

import com.xchushi.common.entity.StringSpliceEntity;
import com.xchushi.common.environment.Configure;
import com.xchushi.log.SysLoggerFactory;
import com.xchushi.transfer.collect.Collectible;

public class FastFailCollectRunner implements Runnable {

    private Collectible<StringSpliceEntity, String> collectible;

    private DefalutCollectSendRunner defalutCollectSendRunner;
    
    private int queueLoopCount = 10;
    
    private int collectLoopCount = 10;

    private static Logger logger = SysLoggerFactory.getLogger(FastFailCollectRunner.class);

    public FastFailCollectRunner(Configure config, Collectible<StringSpliceEntity, String> collectible,
            DefalutCollectSendRunner defalutCollectSendRunner) {
        this.collectible = collectible;
        this.defalutCollectSendRunner = defalutCollectSendRunner;
        if (config != null) {
            queueLoopCount = config.getProperty("queueLoopCount", Integer.class, 30);
            collectLoopCount = config.getProperty("fastFailCollectLoopCount", Integer.class, 10);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                int count = 0;
                StringSpliceEntity sendEntity = collectible.collect();
                while (sendEntity.count() < queueLoopCount && count < collectLoopCount) {
                    sendEntity = collectible.collect(sendEntity);
                }
                if (sendEntity != null) {
                    defalutCollectSendRunner.sendingFailed(sendEntity, null);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

}
