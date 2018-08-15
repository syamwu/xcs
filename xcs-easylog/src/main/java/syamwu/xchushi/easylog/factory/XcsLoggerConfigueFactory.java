package syamwu.xchushi.easylog.factory;

import syamwu.xchushi.fw.common.constant.StringConstant;
import syamwu.xchushi.fw.common.environment.Configure;
import syamwu.xchushi.fw.config.FileProperties;
import syamwu.xchushi.fw.config.XcsConfigure;
import syamwu.xchushi.fw.factory.ConfigureFactory;

/**
 * easylog配置工厂
 * 
 * @author: syam_wu
 * @date: 2018
 */
public class XcsLoggerConfigueFactory extends ConfigureFactory {
    
    private static final String DEFAULT_FILE = StringConstant.CONFIG_FILE;
    
    @Override
    public Configure getInstance(Class<?> exer) {
        return getFileConfigue(exer, DEFAULT_FILE);
    }

    public Configure getFileConfigue(Class<?> exer, String fileName) {
        Configure configure = XcsConfigure.initConfigureAndGet(
                new FileProperties(fileName == null ? StringConstant.CONFIG_FILE : fileName), exer);
        if (defaultConfigure == null) {
            defaultConfigure = configure;
        }
        return configure;
    }

}
