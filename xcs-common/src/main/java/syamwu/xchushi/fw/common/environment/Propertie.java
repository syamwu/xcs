package syamwu.xchushi.fw.common.environment;

public interface Propertie {

    <T> T get(String key, Class<T> cls);
    
}
