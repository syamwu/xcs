package xcs.log.elasticsearch;

import xcs.log.elasticsearch.logger.TCPEsLogger;

public class EsLoggerFactory {

    public static EsLogger getLogger(Class<?> cls) {
        return TCPEsLogger.getLogger(cls);
    }

    public static EsLogger getLogger(Class<?> cls, EsLoggerBuilder esbilder) {
        return esbilder.logger(cls);
    }

}
