package syamwu.xchushi.fw.factory;

import syamwu.xchushi.fw.common.Asset;
import syamwu.xchushi.fw.common.environment.Configure;
import syamwu.xchushi.fw.config.XcsConfigure;

/**
 * 单例配置工厂类
 * 
 * @author: syam_wu
 * @date: 2018
 */
public class ConfigureFactory extends AbstractFactory<Configure> {

    protected static Configure defaultConfigure;

    @Override
    public Configure getInstance(Class<?> exer, Object... objs) {
          Asset.notNull(exer);
          if (defaultConfigure != null)
              return defaultConfigure;
          Configure configure = XcsConfigure.getConfigure(exer);
          return configure;
    }

}
