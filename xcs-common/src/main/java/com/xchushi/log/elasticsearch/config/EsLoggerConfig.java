
package com.xchushi.log.elasticsearch.config;

import org.springframework.core.env.Environment;

import com.xchushi.log.constant.LEVEL;
import com.xchushi.log.elasticsearch.exception.EsLoggerInitException;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EsLoggerConfig {
    /**
     * elasticsearch索引名称
     */
    private String index;

    /**
     * 文档类型
     */
    private String type;

    /**
     * 应用名称
     */
    private String appname;

    /**
     * 日志等级
     */
    private LEVEL level;

    /**
     * 结构版本,用以标示不同版本的文档结构
     */
    private int docVersion;

    /**
     * 应用ip地址
     */
    private String ipAddress;

    static EsLoggerConfig cfg;
    
    public static Environment env;

    public static EsLoggerConfig getCfg() {
        if (cfg == null) {
            throw new EsLoggerInitException("esconfig is null");
        }
        return cfg;
    }

    public synchronized static EsLoggerConfig initConfig(Environment env) {
        if (cfg == null) {
            cfg = new EsLoggerConfig();
        }
        EsLoggerConfig.env = env;
        String levelStr = env.getProperty("eslogger.level", "INFO");
        cfg.level = LEVEL.valueOf(levelStr);
        if (cfg.level == null) {
            throw new EsLoggerInitException("eslogger.level is a wrong value.");
        }
        cfg.index = env.getProperty("eslogger.index", "yunyi_log");
        cfg.type = env.getProperty("eslogger.type", "front");
        cfg.appname = env.getProperty("eslogger.appname", "front_app");
        cfg.ipAddress = env.getProperty("eslogger.ipAddress", "");
        cfg.docVersion = env.getProperty("eslogger.docVersion", Integer.class, 1);
        return cfg;
    }

}