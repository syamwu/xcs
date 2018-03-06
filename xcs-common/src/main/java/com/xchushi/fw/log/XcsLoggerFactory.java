package com.xchushi.fw.log;

import com.xchushi.fw.log.elasticsearch.EsLogger;
import com.xchushi.fw.log.elasticsearch.EsLoggerBuilder;
import com.xchushi.fw.log.elasticsearch.logger.TCPEsLogger;

public class XcsLoggerFactory {

    public static EsLogger getLogger(Class<?> cls) {
        return TCPEsLogger.getLogger(cls);
    }

    public static EsLogger getLogger(Class<?> cls, EsLoggerBuilder esbilder) {
        return esbilder.logger(cls);
    }

}
