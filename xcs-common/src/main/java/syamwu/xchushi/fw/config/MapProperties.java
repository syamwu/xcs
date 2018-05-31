package syamwu.xchushi.fw.config;

import java.util.Map;

import syamwu.xchushi.fw.common.environment.Propertie;

public class MapProperties implements Propertie {

    private Map<String, Object> map = null;

    public MapProperties(Map<String, Object> map) {
        this.map = map;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String key, Class<T> cls) {
        return (T) map.get(key);
    }
    
}
