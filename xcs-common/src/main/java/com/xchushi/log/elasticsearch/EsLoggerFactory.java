package com.xchushi.log.elasticsearch;

import com.xchushi.log.elasticsearch.logger.TCPEsLogger;

public class EsLoggerFactory {

    public static EsLogger getLogger(Class<?> cls) {
        return TCPEsLogger.getLogger(cls);
    }

    public static EsLogger getLogger(Class<?> cls, EsLoggerBuilder esbilder) {
        return esbilder.logger(cls);
    }

}
