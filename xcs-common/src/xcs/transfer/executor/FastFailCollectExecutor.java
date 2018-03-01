package xcs.transfer.executor;

import org.slf4j.Logger;

import xcs.common.entity.Entity;
import xcs.log.SysLogger;
import xcs.transfer.collect.Collectible;

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
