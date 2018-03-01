package com.xchushi.transfer.executor;

import org.slf4j.Logger;

import com.xchushi.common.entity.Entity;
import com.xchushi.log.SysLogger;
import com.xchushi.transfer.collect.Collectible;

public class FastFailCollectExecutor implements Runnable {

    private Collectible<?> collectible;

    private CollectSendExecutor collectSendExecutor;

    private static Logger logger = SysLogger.getLogger(FastFailCollectExecutor.class);

    public FastFailCollectExecutor(Collectible<?> collectible, CollectSendExecutor collectSendExecutor) {
        this.collectible = collectible;
        this.collectSendExecutor = collectSendExecutor;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Entity<?> sendEntity = collectible.collect();
                if (sendEntity != null) {
                    collectSendExecutor.sendingFailed(sendEntity, null);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

}
